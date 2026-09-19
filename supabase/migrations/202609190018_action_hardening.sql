begin;
create or replace function public.submit_team_action(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; r public.match_rounds; c public.content_items;
 participant public.match_participants; prior private.game_actions; answer jsonb; piece jsonb;
 t timestamptz; correct boolean:=false; points integer:=0; result jsonb; normalized text;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 select * into prior from private.game_actions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id is distinct from p_match or prior.round_id is distinct from p_round or prior.action is distinct from p_action then raise exception 'idempotency_conflict'; end if;
  return prior.result;
 end if;
 if m.mode<>'duo' then raise exception 'unsupported_mode'; end if;
 select * into strict participant from public.match_participants where match_id=p_match and user_id=u;
 if not participant.eligible or participant.team_id is null then raise exception 'not_eligible'; end if;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if m.status<>'active' or r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if jsonb_typeof(p_action) is distinct from 'object' or octet_length(p_action::text)>4096 then raise exception 'invalid_action'; end if;
 if (select count(*) from private.game_actions where user_id=u and received_at>t-interval '1 second')>=20 then raise exception 'rate_limited'; end if;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 select a.answer into strict answer from private.content_answers a where content_id=r.content_id;
 if c.kind='collaborative_puzzle' then
  if (select count(*) from jsonb_object_keys(p_action))<>3 or not(p_action ?& array['piece_id','slot_id','rotation']) then raise exception 'invalid_action'; end if;
  select value into piece from jsonb_array_elements(answer->'pieces') where value->>'id'=p_action->>'piece_id';
  if piece is null then raise exception 'invalid_piece'; end if;
  if (piece->>'side'='LEFT' and participant.seat<>0) or (piece->>'side'='RIGHT' and participant.seat<>1) then raise exception 'not_eligible'; end if;
  if exists(select 1 from private.puzzle_placements where round_id=p_round and team_id=participant.team_id and piece_id=p_action->>'piece_id') then raise exception 'already_placed'; end if;
  if not exists(select 1 from jsonb_array_elements(answer->'pieces') where value->>'slot'=p_action->>'slot_id') then raise exception 'invalid_slot'; end if;
  if jsonb_typeof(p_action->'rotation')<>'number' or (p_action->>'rotation') not in ('0','90','180','270') then raise exception 'invalid_rotation'; end if;
  correct:=piece->>'slot'=p_action->>'slot_id' and (piece->>'rotation')::integer=(p_action->>'rotation')::integer;
  if correct then
   insert into private.puzzle_placements(round_id,team_id,piece_id,slot_id,user_id) values(p_round,participant.team_id,p_action->>'piece_id',p_action->>'slot_id',u);
   points:=1;
  end if;
 elsif c.kind='word_scramble' then
  if (select count(*) from jsonb_object_keys(p_action))<>1 or not(p_action ? 'answer') or jsonb_typeof(p_action->'answer')<>'string' or length(p_action->>'answer') not between 1 and 200 then raise exception 'invalid_action'; end if;
  if exists(select 1 from private.game_actions ga where ga.round_id=p_round and ga.team_id=participant.team_id and ga.result->>'correct'='true') then raise exception 'already_submitted'; end if;
  normalized:=lower(regexp_replace(trim(normalize(p_action->>'answer',NFKC)),'\s+',' ','g'));
  correct:=exists(select 1 from jsonb_array_elements_text(answer->'accepted_answers') a where lower(regexp_replace(trim(normalize(a,NFKC)),'\s+',' ','g'))=normalized);
  if correct then
   points:=case when exists(select 1 from private.game_actions ga where ga.round_id=p_round and ga.result->>'correct'='true') then (answer->>'reduced_points')::integer else (answer->>'full_points')::integer end;
  end if;
 else raise exception 'unsupported_round'; end if;
 if points<0 or points>1000 then raise exception 'invalid_content_score'; end if;
 result:=jsonb_build_object('accepted',true,'correct',correct,'points',points,'receipt',gen_random_uuid());
 insert into private.game_actions(match_id,round_id,user_id,team_id,idempotency_key,action,result,points,received_at)
 values(p_match,p_round,u,participant.team_id,p_key,p_action,result,points,t);
 if points>0 then
  update public.match_participants set score=score+points where match_id=p_match and user_id=u;
  insert into public.score_events(match_id,round_id,user_id,delta) values(p_match,p_round,u,points)
  on conflict(round_id,user_id) do update set delta=public.score_events.delta+excluded.delta;
 end if;
 update public.matches set version=version+1 where id=p_match;
 return result;
end $$;
create or replace function public.submit_relay_action(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();m public.matches;r public.match_rounds;c public.content_items;member public.match_participants;
 prior private.game_actions;precision private.precision_states;sorting private.sort_states;stream text[];
 t timestamptz;turn_start timestamptz;marker_position numeric;relative numeric;pass bigint;width numeric;speed numeric;zone numeric;
 correct boolean:=false;points integer:=0;next_streak integer;item text;answer jsonb;receipt jsonb;active_seat integer;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 select * into prior from private.game_actions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id is distinct from p_match or prior.round_id is distinct from p_round or prior.action is distinct from p_action then raise exception 'idempotency_conflict'; end if;
  return prior.result;
 end if;
 if m.mode<>'squad' then raise exception 'unsupported_mode'; end if;
 select * into strict member from public.match_participants where match_id=p_match and user_id=u;
 if not member.eligible or member.team_id is null then raise exception 'not_eligible'; end if;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if m.status<>'active' or r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if jsonb_typeof(p_action) is distinct from 'object' or octet_length(p_action::text)>1024 then raise exception 'invalid_action'; end if;
 if (select count(*) from private.game_actions where user_id=u and received_at>t-interval '1 second')>=30 then raise exception 'rate_limited'; end if;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 perform private.prepare_relay_round(p_round);
 if c.kind='precision_tap' then
  if p_action<>'{}'::jsonb then raise exception 'invalid_action'; end if;
  turn_start:=r.starts_at+member.seat*interval '20 seconds';
  if t<turn_start or t>=turn_start+interval '20 seconds' then raise exception 'not_eligible'; end if;
  select * into strict precision from private.precision_states where round_id=p_round and user_id=u for update;
  speed:=(c.payload->>'rotation_degrees_per_second')::numeric+precision.streak*(c.payload->>'speed_increment')::numeric;
  width:=least((c.payload->>'max_hot_zone_degrees')::numeric,(c.payload->>'hot_zone_degrees')::numeric+precision.streak*(c.payload->>'width_increment')::numeric);
  if speed<=0 or width<=0 or width>360 then raise exception 'invalid_precision_content'; end if;
  marker_position:=precision.position+extract(epoch from t-precision.anchor_at)*speed;
  relative:=marker_position-precision.zone_start;pass:=floor(relative/360);zone:=mod(mod(relative,360)+360,360);
  correct:=zone<=width;
  if correct and precision.last_hit_pass is not distinct from pass then
   next_streak:=precision.streak;
  else
   next_streak:=case when correct then precision.streak+1 else 0 end;
   points:=case when correct then next_streak else 0 end;
  end if;
  update private.precision_states set position=marker_position,anchor_at=t,streak=next_streak,
   score=score+points,last_hit_pass=case when correct then pass else last_hit_pass end where round_id=p_round and user_id=u;
 elsif c.kind='speed_sort' then
  if (select count(*) from jsonb_object_keys(p_action))<>2 or not(p_action ?& array['index','bucket']) or jsonb_typeof(p_action->'index')<>'number' or jsonb_typeof(p_action->'bucket')<>'string' then raise exception 'invalid_action'; end if;
  select * into strict sorting from private.sort_states where round_id=p_round and team_id=member.team_id for update;
  active_seat:=sorting.item_index%4;
  if member.seat<>active_seat then raise exception 'not_eligible'; end if;
  if p_action->>'index'<>sorting.item_index::text then raise exception 'stale_item'; end if;
  select items into strict stream from private.sort_streams where round_id=p_round;
  if not exists(select 1 from private.content_answers ca where ca.content_id=any(stream) and ca.answer->>'bucket'=p_action->>'bucket') then raise exception 'invalid_bucket'; end if;
  item:=stream[(sorting.item_index%cardinality(stream))+1];
  select a.answer into strict answer from private.content_answers a where content_id=item;
  correct:=answer->>'bucket'=p_action->>'bucket';points:=case when correct then 1 else 0 end;
  update private.sort_states set item_index=item_index+1,streak=case when correct then streak+1 else 0 end where round_id=p_round and team_id=member.team_id;
 else raise exception 'unsupported_round';end if;
 receipt:=jsonb_build_object('accepted',true,'correct',correct,'points',points,'receipt',gen_random_uuid());
 insert into private.game_actions(match_id,round_id,user_id,team_id,idempotency_key,action,result,points,received_at)
 values(p_match,p_round,u,member.team_id,p_key,p_action,receipt,points,t);
 if points>0 then
  update public.match_participants set score=score+points where match_id=p_match and user_id=u;
  insert into public.score_events(match_id,round_id,user_id,delta) values(p_match,p_round,u,points)
  on conflict(round_id,user_id) do update set delta=public.score_events.delta+excluded.delta;
 end if;
 update public.matches set version=version+1 where id=p_match;
 return receipt;
end $$;
create or replace function public.start_match(p_room uuid,p_locale text default 'en') returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();r public.rooms;m uuid;phase uuid;content_id text;team_size integer;
 starts timestamptz;duration integer;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501';end if;
 if r.mode='duel' then return private.start_duel(p_room,p_locale);end if;
 if r.mode='solo' then raise exception 'rules_not_approved';end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m;end if;
 if p_locale not in ('en','fr','ar') or r.status<>'lobby' then raise exception 'invalid_start';end if;
 team_size:=case when r.mode='duo' then 2 else 4 end;
 if (select count(distinct team_id) from public.room_members where room_id=p_room)<2 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds' or team_id is null)) or
 exists(select 1 from public.room_members where room_id=p_room group by team_id having count(*)<>team_size) then raise exception 'players_not_ready';end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable';end if;
 if exists(select 1 from public.room_members rm where rm.room_id=p_room and (select count(*) from public.loadouts where user_id=rm.user_id)<>2) then raise exception 'loadout_required';end if;
 if (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale)<(case when r.mode='duo' then 15 else 20 end) then raise exception 'approved_content_required';end if;
 if r.mode='duo' then
  select c.id into content_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='collaborative_puzzle' and c.status='APPROVED' and c.locale=p_locale order by random() limit 1;
  if content_id is null or (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='word_scramble' and c.status='APPROVED' and c.locale=p_locale)<5 then raise exception 'approved_content_required';end if;
  duration:=120;
 else
  select c.id into content_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='precision_tap' and c.status='APPROVED' and c.locale in (p_locale,'global') order by random() limit 1;
  if content_id is null or not exists(select 1 from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='speed_sort' and c.status='APPROVED' and c.locale=p_locale group by c.payload->>'theme' having count(distinct a.answer->>'bucket')>=2) then raise exception 'approved_content_required';end if;
  duration:=80;
 end if;
 starts:=clock_timestamp()+interval '3 seconds';
 insert into public.matches(room_id,mode) values(p_room,r.mode) returning id into m;
 insert into public.match_participants(match_id,user_id,team_id,seat) select m,user_id,team_id,seat from public.room_members where room_id=p_room;
 insert into private.team_match_state(match_id,locale) values(m,p_locale);
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline)
  values(m,0,case when r.mode='duo' then 'collaborative_puzzle' else 'precision_tap' end,'pending',starts,starts+make_interval(secs=>duration)) returning id into phase;
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase,0,content_id,starts,starts,starts+make_interval(secs=>duration));
 insert into public.match_phases(match_id,ordinal,kind,status) values
  (m,1,case when r.mode='duo' then 'theme_draft' else 'speed_sort' end,'pending'),
  (m,2,case when r.mode='duo' then 'word_scramble' else 'theme_draft' end,'pending');
 update public.rooms set status='playing',matchmaking=false,version=version+1 where id=p_room;
 return m;
end $$;
create or replace function public.choose_draft_theme(p_match uuid,p_question integer,p_theme text) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();m public.matches;draft private.team_drafts;s private.team_match_state;
 phase uuid;item text;created_round uuid;t timestamptz;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501';end if;
 select * into strict m from public.matches where id=p_match for update;
 perform private.advance_match(p_match);
 t:=clock_timestamp();
 select * into strict draft from private.team_drafts where match_id=p_match and question=p_question;
 if not exists(select 1 from public.match_participants where match_id=p_match and user_id=u and team_id=draft.choosing_team and eligible) then raise exception 'not_eligible';end if;
 if draft.round_id is not null then
  if draft.theme is distinct from p_theme then raise exception 'idempotency_conflict';end if;
  return draft.round_id;
 end if;
 select * into strict s from private.team_match_state where match_id=p_match;
 if s.question<>p_question or m.status in ('results','closed') or length(p_theme) not between 1 and 200 then raise exception 'invalid_draft';end if;
 select c.id into item from public.content_items c join private.content_answers a on a.content_id=c.id
  where c.kind='question_round' and c.status='APPROVED' and c.locale=s.locale and c.payload->>'theme'=p_theme
  and not exists(select 1 from public.match_rounds where match_id=p_match and content_id=c.id) order by random() limit 1;
 if item is null then raise exception 'invalid_theme';end if;
 select id into strict phase from public.match_phases where match_id=p_match and kind='theme_draft';
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline)
 values(p_match,phase,p_question+case when m.mode='squad' then 1 else 0 end,item,'active',t,t+interval '15 seconds',t+interval '35 seconds') returning id into created_round;
 update private.team_drafts set round_id=created_round,theme=p_theme where match_id=p_match and question=p_question;
 update public.matches set status='active',active_round=p_question+case when m.mode='squad' then 1 else 0 end,version=version+1 where id=p_match;
 return created_round;
end $$;
create or replace function public.move_puzzle_cursor(p_match uuid,p_round uuid,p_x numeric,p_y numeric) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();team uuid;r public.match_rounds;t timestamptz;begin
 if p_x is null or p_y is null or not(p_x between 0 and 1 and p_y between 0 and 1) then raise exception 'invalid_cursor';end if;
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501';end if;
 perform 1 from public.matches where id=p_match for update;
 t:=clock_timestamp();
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match;
 if r.status<>'active' or t<r.starts_at or t>=r.deadline or not exists(select 1 from public.content_items where id=r.content_id and kind='collaborative_puzzle') then raise exception 'round_not_accepting';end if;
 select team_id into strict team from public.match_participants where match_id=p_match and user_id=u and eligible;
 insert into private.puzzle_cursors(round_id,user_id,team_id,x,y,updated_at) values(p_round,u,team,p_x,p_y,t)
 on conflict(round_id,user_id) do update set x=excluded.x,y=excluded.y,updated_at=t where private.puzzle_cursors.updated_at<t-interval '100 milliseconds';
 if found then update public.matches set version=version+1 where id=p_match;end if;
end $$;
create or replace function private.advance_team_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches;s private.team_match_state;r public.match_rounds;t timestamptz;
 teams uuid[];phase uuid;content record;round_ordinal integer;start_at timestamptz;draft_round uuid;
begin
 select * into strict m from public.matches where id=p_match for update;
 t:=clock_timestamp();
 if m.status in ('results','closed') then return;end if;
 select * into strict s from private.team_match_state where match_id=p_match for update;
 for r in select * from public.match_rounds where match_id=p_match and status<>'results' and deadline<=t order by ordinal for update loop
  insert into public.score_events(match_id,round_id,user_id,delta) select match_id,round_id,user_id,awarded_points from private.submissions where round_id=r.id on conflict(round_id,user_id) do nothing;
  update public.match_participants p set score=p.score+x.awarded_points from private.submissions x where x.round_id=r.id and p.match_id=p_match and p.user_id=x.user_id;
  update public.match_rounds set status='results',reveal=(select answer from private.content_answers where content_id=r.content_id) where id=r.id;
  update private.sort_states set streak=0 where round_id=r.id;
  update public.matches set version=version+1 where id=p_match;
 end loop;
 update public.match_phases set status='active' where match_id=p_match and status='pending' and starts_at<=t and deadline>t;
 update public.match_rounds set status='active' where match_id=p_match and starts_at<=t and deadline>t and status='pending';
 if found then update public.matches set status='active',version=version+1 where id=p_match;end if;
 select mr.ordinal into round_ordinal from public.match_rounds mr where mr.match_id=p_match and mr.status='active' order by mr.ordinal limit 1;
 if round_ordinal is not null then
  update public.matches set active_round=round_ordinal where id=p_match and active_round is distinct from round_ordinal;return;
 end if;
 if exists(select 1 from public.match_rounds where match_id=p_match and status='pending') then return;end if;
 -- All created rounds are finished. Advance the mode plan without inventing a
 -- chooser timeout: a theme waits for the authorized team (reconnect-safe).
 if s.stage=0 then
  teams:=private.rank_teams(p_match,0);
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=0;
  update private.team_match_state set stage=1,ranked_teams=teams where match_id=p_match;
  if m.mode='squad' then
   select id into strict phase from public.match_phases where match_id=p_match and ordinal=1;
   select ci.id into content from public.content_items ci where ci.kind='speed_sort' and ci.status='APPROVED' and ci.locale=s.locale and ci.payload->>'theme' in (select x.payload->>'theme' from public.content_items x join private.content_answers a on a.content_id=x.id where x.kind='speed_sort' and x.status='APPROVED' and x.locale=s.locale group by x.payload->>'theme' having count(distinct a.answer->>'bucket')>=2) order by random() limit 1;
   insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline,status)
    values(p_match,phase,1,content.id,t,t,t+interval '90 seconds','active');
   update public.match_phases set status='active',starts_at=t,deadline=t+interval '90 seconds' where id=phase;
   update public.matches set active_round=1,version=version+1 where id=p_match;
  else
   update public.match_phases set status='active',starts_at=t where match_id=p_match and ordinal=1;
   perform private.prepare_team_draft(p_match,1);
  end if;
 elsif s.stage=1 and m.mode='squad' then
  teams:=private.rank_teams(p_match,1);
  update private.team_match_state set stage=2,ranked_teams=teams where match_id=p_match;
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=1;
  update public.match_phases set status='active',starts_at=t where match_id=p_match and ordinal=2;
  perform private.prepare_team_draft(p_match,1);
 elsif (s.stage=1 and m.mode='duo') or (s.stage=2 and m.mode='squad') then
  select round_id into draft_round from private.team_drafts where match_id=p_match and question=s.question;
  if draft_round is null then return;end if;
  if s.question<(case when m.mode='duo' then 15 else 20 end) then perform private.prepare_team_draft(p_match,s.question+1);
  elsif m.mode='squad' then
   update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=2;
   perform private.finalize_team_match(p_match);
  else
   update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=1;
   update private.team_match_state set stage=2 where match_id=p_match;
   select id into strict phase from public.match_phases where match_id=p_match and ordinal=2;
   update public.match_phases set status='active',starts_at=t,deadline=t+interval '75 seconds' where id=phase;
   round_ordinal:=16;
   for content in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='word_scramble' and c.status='APPROVED' and c.locale=s.locale order by random() limit 5 loop
    start_at:=t+(round_ordinal-16)*interval '15 seconds';
    insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline,status)
     values(p_match,phase,round_ordinal,content.id,start_at,start_at,start_at+interval '15 seconds',case when round_ordinal=16 then 'active' else 'pending' end);
    round_ordinal:=round_ordinal+1;
   end loop;
   update public.matches set active_round=16,version=version+1 where id=p_match;
  end if;
 elsif s.stage=2 and m.mode='duo' then
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=2;
  perform private.finalize_team_match(p_match);
 end if;
end $$;
commit;
