begin;
create or replace function private.start_duel(p_room uuid,p_locale text default 'en') returns uuid
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; m uuid; phase1 uuid; phase2 uuid;
 item record; ordinal integer:=0; start_time timestamptz;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501'; end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m; end if;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 if r.mode<>'duel' then raise exception 'rules_not_approved'; end if;
 if p_locale not in ('en','fr','ar') then raise exception 'invalid_locale'; end if;
 if (select count(*) from public.room_members where room_id=p_room)<>2 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds')) then raise exception 'players_not_ready'; end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable'; end if;
 if exists(select 1 from public.room_members m where room_id=p_room and (select count(*) from public.loadouts where user_id=m.user_id)<>2) then raise exception 'loadout_required'; end if;
 if (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale)<15 or
 (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='image_guess' and c.status='APPROVED' and c.locale=p_locale and a.answer->>'scoring_policy' in ('all_selected','correct_only'))<5 then raise exception 'approved_content_required'; end if;
 start_time:=clock_timestamp()+interval '3 seconds';
 insert into public.matches(room_id,mode) values(p_room,r.mode) returning id into m;
 insert into public.match_participants(match_id,user_id,team_id,seat) select m,user_id,team_id,seat from public.room_members where room_id=p_room;
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,0,'question_round','pending',start_time,start_time+interval '450 seconds') returning id into phase1;
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,1,'image_guess','pending',start_time+interval '450 seconds',start_time+interval '600 seconds') returning id into phase2;
 for item in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id
 where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale order by random() limit 15 loop
  insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase1,ordinal,item.id,start_time+ordinal*interval '30 seconds',start_time+ordinal*interval '30 seconds'+interval '10 seconds',start_time+(ordinal+1)*interval '30 seconds');
  ordinal:=ordinal+1;
 end loop;
 for item in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id
 where c.kind='image_guess' and c.status='APPROVED' and c.locale=p_locale and a.answer->>'scoring_policy' in ('all_selected','correct_only') order by random() limit 5 loop
  insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase2,ordinal,item.id,start_time+ordinal*interval '30 seconds',start_time+ordinal*interval '30 seconds',start_time+(ordinal+1)*interval '30 seconds');
  ordinal:=ordinal+1;
 end loop;
 update public.rooms set status='playing',matchmaking=false,version=version+1 where id=p_room;
 return m;
end $$;
create or replace function private.advance_duel(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches; r public.match_rounds; t timestamptz; next_round integer; changed integer:=0; begin
 select * into strict m from public.matches where id=p_match for update;
 t:=clock_timestamp();
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
  perform private.finalize_duel(p_match);
 end if;
end $$;
create or replace function public.submit_answer(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; r public.match_rounds; c public.content_items; answer jsonb;
 prior private.submissions; t timestamptz; points integer:=0; selected text[]; option jsonb;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 -- A replay returns its existing receipt even if the deadline has since passed.
 select * into prior from private.submissions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id is distinct from p_match or prior.round_id is distinct from p_round or prior.action is distinct from p_action then raise exception 'idempotency_conflict'; end if;
  return jsonb_build_object('accepted',true,'receipt',prior.id);
 end if;
 perform private.advance_match(p_match);
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if not exists(select 1 from public.match_participants where match_id=p_match and user_id=u and eligible) then raise exception 'not_eligible'; end if;
 if exists(select 1 from private.submissions where round_id=p_round and user_id=u) then raise exception 'already_submitted'; end if;
 if jsonb_typeof(p_action) is distinct from 'object' or octet_length(p_action::text)>4096 then raise exception 'invalid_action'; end if;
 select * into strict c from public.content_items where id=r.content_id;
 select a.answer into strict answer from private.content_answers a where content_id=r.content_id;
 if m.mode in ('duo','squad') and not exists(select 1 from private.team_drafts d join public.match_participants p on p.match_id=d.match_id where d.round_id=p_round and p.user_id=u and d.answerers->>p.team_id::text=u::text) then raise exception 'not_eligible';end if;
 if c.kind='question_round' then
  if (select count(*) from jsonb_object_keys(p_action))<>1 or not(p_action ? 'option_id') then raise exception 'invalid_action'; end if;
  if not exists(select 1 from jsonb_array_elements(c.payload->'options') o where o->>'id'=p_action->>'option_id') then raise exception 'invalid_option'; end if;
  if p_action->>'option_id'=answer->>'correct_option_id' and (m.mode<>'duel' or r.first_correct is null) then
   points:=1; update public.match_rounds set first_correct=u where id=p_round;
  end if;
 elsif c.kind='image_guess' then
  if (select count(*) from jsonb_object_keys(p_action))<>1 or jsonb_typeof(p_action->'choice_ids')<>'array' or jsonb_array_length(p_action->'choice_ids')<>4 then raise exception 'invalid_selection'; end if;
  select array_agg(value) into selected from jsonb_array_elements_text(p_action->'choice_ids');
  if (select count(distinct x) from unnest(selected) x)<>4 then raise exception 'invalid_selection'; end if;
  if (select count(*) from jsonb_array_elements(answer->'choices') o where o->>'id'=any(selected))<>4 then raise exception 'invalid_selection'; end if;
  if answer->>'scoring_policy' not in ('all_selected','correct_only') or not(answer ? 'scoring_policy') then raise exception 'rules_not_approved'; end if;
  select coalesce(sum((o->>'points')::integer),0) into points from jsonb_array_elements(answer->'choices') o
  where o->>'id'=any(selected) and (answer->>'scoring_policy'='all_selected' or (o->>'correct')::boolean);
 else raise exception 'unsupported_round'; end if;
 if points<0 or points>1000 then raise exception 'invalid_content_score'; end if;
 insert into private.submissions(match_id,round_id,user_id,action,received_at,idempotency_key,awarded_points) values(p_match,p_round,u,p_action,t,p_key,points) returning * into prior;
 update public.matches set version=version+1 where id=p_match;
 -- Do not reveal correctness/point weights until the round deadline.
 return jsonb_build_object('accepted',true,'receipt',prior.id);
end $$;
commit;
