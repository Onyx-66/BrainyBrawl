-- Solo Online schedule delegated to engineering by the owner on 2026-09-19.
-- Existing private action/sort team_id columns act as a player scope for Solo; no public teams are fabricated.
begin;
create function private.start_solo(p_room uuid,p_locale text) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();r public.rooms;m uuid;phase uuid;precision_id text;sort_id text;
 starts timestamptz;item record;ordinal integer:=2;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501';end if;
 if r.mode<>'solo' then raise exception 'unsupported_mode';end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m;end if;
 if r.status<>'lobby' or p_locale not in ('en','fr','ar') or p_locale is null then raise exception 'invalid_start';end if;
 if (select count(*) from public.room_members where room_id=p_room) not between 2 and 20 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds' or team_id is not null)) then raise exception 'players_not_ready';end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable';end if;
 if exists(select 1 from public.room_members rm where rm.room_id=p_room and (select count(*) from public.loadouts where user_id=rm.user_id) not in (0,2)) then raise exception 'loadout_required';end if;
 select c.id into precision_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='precision_tap' and c.status='APPROVED' and c.locale in (p_locale,'global') order by random() limit 1;
 select c.id into sort_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='speed_sort' and c.status='APPROVED' and c.locale=p_locale and c.payload->>'theme' in
  (select x.payload->>'theme' from public.content_items x join private.content_answers y on y.content_id=x.id where x.kind='speed_sort' and x.status='APPROVED' and x.locale=p_locale group by x.payload->>'theme' having count(distinct y.answer->>'bucket')>=2) order by random() limit 1;
 if precision_id is null or sort_id is null or (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale)<15 then raise exception 'approved_content_required';end if;
 starts:=clock_timestamp()+interval '3 seconds';
 insert into public.matches(room_id,mode) values(p_room,'solo') returning id into m;
 insert into public.match_participants(match_id,user_id,seat) select m,user_id,0 from public.room_members where room_id=p_room;
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,0,'precision_tap','pending',starts,starts+interval '20 seconds') returning id into phase;
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline) values(m,phase,0,precision_id,starts,starts,starts+interval '20 seconds');
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,1,'speed_sort','pending',starts+interval '20 seconds',starts+interval '110 seconds') returning id into phase;
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline) values(m,phase,1,sort_id,starts+interval '20 seconds',starts+interval '20 seconds',starts+interval '110 seconds');
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,2,'question_round','pending',starts+interval '110 seconds',starts+interval '560 seconds') returning id into phase;
 for item in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale order by random() limit 15 loop
  insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
   values(m,phase,ordinal,item.id,starts+make_interval(secs=>110+(ordinal-2)*30),starts+make_interval(secs=>120+(ordinal-2)*30),starts+make_interval(secs=>140+(ordinal-2)*30));
  ordinal:=ordinal+1;
 end loop;
 update public.rooms set status='playing',matchmaking=false,version=version+1 where id=p_room;
 return m;
end $$;
create function private.finalize_solo(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches;contenders uuid[];winner_id uuid;candidate uuid;draw_attempt integer:=0;high integer;
begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return;end if;
 if m.mode<>'solo' or exists(select 1 from public.match_rounds where match_id=p_match and status<>'results') then raise exception 'match_not_finished';end if;
 select array_agg(user_id order by user_id) into contenders from public.match_participants where match_id=p_match and score=(select max(score) from public.match_participants where match_id=p_match);
 -- Only the tied leaders reroll; each draw is persisted and never supplied by a client.
 while cardinality(contenders)>1 loop
  draw_attempt:=draw_attempt+1;
  foreach candidate in array contenders loop
   insert into private.tie_rolls(match_id,user_id,attempt,roll) values(p_match,candidate,draw_attempt,private.roll_twenty());
  end loop;
  select max(roll) into high from private.tie_rolls where match_id=p_match and private.tie_rolls.attempt=draw_attempt;
  select array_agg(user_id order by user_id) into contenders from private.tie_rolls where match_id=p_match and private.tie_rolls.attempt=draw_attempt and roll=high;
 end loop;
 winner_id:=contenders[1];
 insert into public.match_results(match_id,user_id,rank,score,winner)
  select p_match,user_id,rank() over(order by score desc,(user_id=winner_id) desc),score,user_id=winner_id from public.match_participants where match_id=p_match;
 insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values(winner_id,'flames',1,'match_win',p_match::text,'flame:'||p_match::text||':'||winner_id::text) on conflict(idempotency_key) do nothing;
 update public.matches set status='results',completed_at=clock_timestamp(),version=version+1 where id=p_match;
 update public.rooms set status='closed',version=version+1 where id=m.room_id;
end $$;

create or replace function public.start_match(p_room uuid,p_locale text default 'en') returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();r public.rooms;m uuid;phase uuid;content_id text;team_size integer;
 starts timestamptz;duration integer;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501';end if;
 if r.mode='duel' then return private.start_duel(p_room,p_locale);end if;
 if r.mode='solo' then return private.start_solo(p_room,p_locale);end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m;end if;
 if p_locale not in ('en','fr','ar') or r.status<>'lobby' then raise exception 'invalid_start';end if;
 team_size:=case when r.mode='duo' then 2 else 4 end;
 if (select count(distinct team_id) from public.room_members where room_id=p_room)<2 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds' or team_id is null)) or
 exists(select 1 from public.room_members where room_id=p_room group by team_id having count(*)<>team_size) then raise exception 'players_not_ready';end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable';end if;
 if exists(select 1 from public.room_members rm where rm.room_id=p_room and (select count(*) from public.loadouts where user_id=rm.user_id) not in (0,2)) then raise exception 'loadout_required';end if;
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
create or replace function private.prepare_relay_round(p_round uuid) returns void language plpgsql security definer set search_path='' as $$
declare r public.match_rounds;c public.content_items;items text[];solo boolean;begin
 select * into strict r from public.match_rounds where id=p_round for update;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 select mode='solo' into solo from public.matches where id=r.match_id;
 if c.kind='precision_tap' then
  insert into private.precision_states(round_id,user_id,anchor_at,zone_start)
  select r.id,p.user_id,r.starts_at+(case when solo then 0 else p.seat end)*interval '20 seconds',private.random_below(360)
  from public.match_participants p where p.match_id=r.match_id on conflict do nothing;
 elsif c.kind='speed_sort' then
  if not exists(select 1 from private.sort_streams where round_id=p_round) then
   select array_agg(x.id order by random()) into items from public.content_items x join private.content_answers a on a.content_id=x.id
    where x.kind='speed_sort' and x.status='APPROVED' and x.locale=c.locale and x.payload->>'theme'=c.payload->>'theme';
   if cardinality(items) is null or (select count(distinct answer->>'bucket') from private.content_answers where content_id=any(items))<2 then raise exception 'invalid_sort_content'; end if;
   insert into private.sort_streams(round_id,items) values(p_round,items);
  end if;
  insert into private.sort_states(round_id,team_id) select distinct r.id,case when solo then p.user_id else p.team_id end from public.match_participants p where p.match_id=r.match_id on conflict do nothing;
 else raise exception 'unsupported_round';end if;
end $$;
create or replace function public.submit_relay_action(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();m public.matches;r public.match_rounds;c public.content_items;member public.match_participants;
 prior private.game_actions;precision private.precision_states;sorting private.sort_states;stream text[];
 t timestamptz;turn_start timestamptz;marker_position numeric;relative numeric;pass bigint;width numeric;speed numeric;zone numeric;
 correct boolean:=false;points integer:=0;next_streak integer;item text;answer jsonb;receipt jsonb;active_seat integer;state_owner uuid;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 select * into prior from private.game_actions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id is distinct from p_match or prior.round_id is distinct from p_round or prior.action is distinct from p_action then raise exception 'idempotency_conflict'; end if;
  return prior.result;
 end if;
 if m.mode not in ('squad','solo') then raise exception 'unsupported_mode'; end if;
 select * into strict member from public.match_participants where match_id=p_match and user_id=u;
 if not member.eligible or (m.mode='squad' and member.team_id is null) then raise exception 'not_eligible'; end if;
 state_owner:=case when m.mode='solo' then u else member.team_id end;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if m.status<>'active' or r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if jsonb_typeof(p_action) is distinct from 'object' or octet_length(p_action::text)>1024 then raise exception 'invalid_action'; end if;
 if (select count(*) from private.game_actions where user_id=u and received_at>t-interval '1 second')>=30 then raise exception 'rate_limited'; end if;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 perform private.prepare_relay_round(p_round);
 if c.kind='precision_tap' then
  if p_action<>'{}'::jsonb then raise exception 'invalid_action'; end if;
  turn_start:=r.starts_at+(case when m.mode='solo' then 0 else member.seat end)*interval '20 seconds';
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
  select * into strict sorting from private.sort_states where round_id=p_round and team_id=state_owner for update;
  active_seat:=sorting.item_index%4;
  if m.mode='squad' and member.seat<>active_seat then raise exception 'not_eligible'; end if;
  if p_action->>'index'<>sorting.item_index::text then raise exception 'stale_item'; end if;
  select items into strict stream from private.sort_streams where round_id=p_round;
  if not exists(select 1 from private.content_answers ca where ca.content_id=any(stream) and ca.answer->>'bucket'=p_action->>'bucket') then raise exception 'invalid_bucket'; end if;
  item:=stream[(sorting.item_index%cardinality(stream))+1];
  select a.answer into strict answer from private.content_answers a where content_id=item;
  correct:=answer->>'bucket'=p_action->>'bucket';points:=case when correct then 1 else 0 end;
  update private.sort_states set item_index=item_index+1,streak=case when correct then streak+1 else 0 end where round_id=p_round and team_id=state_owner;
 else raise exception 'unsupported_round';end if;
 receipt:=jsonb_build_object('accepted',true,'correct',correct,'points',points,'receipt',gen_random_uuid());
 insert into private.game_actions(match_id,round_id,user_id,team_id,idempotency_key,action,result,points,received_at)
 values(p_match,p_round,u,state_owner,p_key,p_action,receipt,points,t);
 if points>0 then
  update public.match_participants set score=score+points where match_id=p_match and user_id=u;
  insert into public.score_events(match_id,round_id,user_id,delta) values(p_match,p_round,u,points)
  on conflict(round_id,user_id) do update set delta=public.score_events.delta+excluded.delta;
 end if;
 update public.matches set version=version+1 where id=p_match;
 return receipt;
end $$;
create or replace function private.advance_solo(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches; r public.match_rounds; t timestamptz:=clock_timestamp(); next_round integer; changed integer:=0; begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return; end if;
 for r in select * from public.match_rounds where match_id=p_match and status<>'results' and deadline<=t order by ordinal for update loop
  insert into public.score_events(match_id,round_id,user_id,delta) select match_id,round_id,user_id,awarded_points from private.submissions where round_id=r.id on conflict(round_id,user_id) do nothing;
  update public.match_participants p set score=p.score+s.awarded_points from private.submissions s where s.round_id=r.id and p.match_id=p_match and p.user_id=s.user_id;
  update public.match_rounds set status='results',reveal=(select answer from private.content_answers where content_id=r.content_id) where id=r.id;
  changed:=changed+1;
 end loop;
 update public.match_phases set status='results' where match_id=p_match and deadline<=t and status<>'results';
 select ordinal into next_round from public.match_rounds where match_id=p_match and starts_at<=t and deadline>t;
 if next_round is not null then
  update public.match_rounds set status='active' where match_id=p_match and ordinal=next_round and status='pending';
  if found then changed:=changed+1; end if;
  update public.match_phases set status='active' where match_id=p_match and starts_at<=t and deadline>t;
  update public.matches set status='active',active_round=next_round,version=version+greatest(changed,0) where id=p_match and (status<>'active' or active_round is distinct from next_round or changed>0);
 elsif not exists(select 1 from public.match_rounds where match_id=p_match and status<>'results') then
  perform private.finalize_solo(p_match);
 end if;
end $$;
create or replace function private.advance_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
begin
 case (select mode from public.matches where id=p_match)
 when 'solo' then perform private.advance_solo(p_match);
 when 'duel' then perform private.advance_duel(p_match);
 else perform private.advance_team_match(p_match);
 end case;
end $$;
create function private.solo_view(p_match uuid,p_user uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare r public.match_rounds;c public.content_items;sorting private.sort_states;stream text[];item text;board jsonb;
begin
 select * into r from public.match_rounds where match_id=p_match and status='active' order by ordinal limit 1;
 if not found then return '{}'::jsonb;end if;
 select * into strict c from public.content_items where id=r.content_id;
 if c.kind in ('precision_tap','speed_sort') then
  perform private.prepare_relay_round(r.id);
  if c.kind='precision_tap' then
   board:=jsonb_build_object('kind',c.kind,'config',c.payload,'players',(select jsonb_agg(to_jsonb(ps)) from private.precision_states ps where ps.round_id=r.id and ps.user_id=p_user));
  else
   select * into strict sorting from private.sort_states where round_id=r.id and team_id=p_user;
   select items into strict stream from private.sort_streams where round_id=r.id;
   item:=stream[(sorting.item_index%cardinality(stream))+1];
   board:=jsonb_build_object('kind',c.kind,'index',sorting.item_index,'streak',sorting.streak,'active_user',p_user,
    'label',(select payload->>'label' from public.content_items where id=item),
    'buckets',(select jsonb_agg(bucket order by bucket) from (select distinct a.answer->>'bucket' as bucket from private.content_answers a where a.content_id=any(stream)) buckets));
  end if;
 end if;
 return jsonb_build_object('board',board);
end $$;
create or replace function public.match_snapshot(p_match uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();base jsonb;begin
 base:=private.match_snapshot_base(p_match);
 if base->'match'->>'mode'='solo' then return base||private.solo_view(p_match,u);end if;
 return base||private.team_view(p_match,u);
end $$;
revoke all on function private.start_solo(uuid,text),private.finalize_solo(uuid),private.advance_solo(uuid),private.solo_view(uuid,uuid) from public,anon,authenticated;
commit;
