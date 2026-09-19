begin;
alter table private.submissions add column awarded_points integer not null default 0 check(awarded_points between 0 and 1000);
-- Round schedules are created transactionally, using server time only. Published
-- content and private answer keys must have passed the offline importer first.
create function public.start_match(p_room uuid,p_locale text default 'en') returns uuid
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; m uuid; phase1 uuid; phase2 uuid;
 item record; ordinal integer:=0; start_time timestamptz:=clock_timestamp()+interval '3 seconds';
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

create function private.roll_twenty() returns integer language plpgsql volatile set search_path='' as $$
declare bits bigint; begin
 loop
  bits:=('x'||substr(replace(gen_random_uuid()::text,'-',''),1,8))::bit(32)::bigint;
  if bits<4294967280 then return (bits%20)::integer+1; end if;
 end loop;
end $$;
revoke all on function private.roll_twenty() from public,anon,authenticated;

create function private.finalize_duel(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches; high integer; winner_id uuid; attempt integer:=0; a uuid; b uuid;
 roll_a integer; roll_b integer;
begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return; end if;
 if m.mode<>'duel' or exists(select 1 from public.match_rounds where match_id=p_match and status<>'results') then raise exception 'match_not_finished'; end if;
 select max(score) into high from public.match_participants where match_id=p_match;
 if (select count(*) from public.match_participants where match_id=p_match and score=high)=1 then
  select user_id into winner_id from public.match_participants where match_id=p_match and score=high;
 else
  select min(user_id::text)::uuid,max(user_id::text)::uuid into a,b from public.match_participants where match_id=p_match;
  -- Server-owned twenty-slot roulette; equal rolls always reroll. No client seeds.
  loop
   attempt:=attempt+1; roll_a:=private.roll_twenty(); roll_b:=private.roll_twenty();
   insert into private.tie_rolls(match_id,user_id,attempt,roll) values(p_match,a,attempt,roll_a),(p_match,b,attempt,roll_b);
   exit when roll_a<>roll_b;
  end loop;
  winner_id:=case when roll_a>roll_b then a else b end;
 end if;
 insert into public.match_results(match_id,user_id,rank,score,winner)
 select p_match,user_id,case when user_id=winner_id then 1 else 2 end,score,user_id=winner_id from public.match_participants where match_id=p_match;
 insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key)
 values(winner_id,'flames',1,'match_win',p_match::text,'flame:'||p_match::text||':'||winner_id::text)
 on conflict(idempotency_key) do nothing;
 update public.matches set status='results',completed_at=clock_timestamp(),version=version+1 where id=p_match;
 update public.rooms set status='closed',version=version+1 where id=m.room_id;
end $$;

create function private.advance_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
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
  update public.matches set status='active',active_round=next_round,version=version+greatest(changed,0) where id=p_match;
 elsif not exists(select 1 from public.match_rounds where match_id=p_match and status<>'results') then
  perform private.finalize_duel(p_match);
 end if;
end $$;

create function public.match_snapshot(p_match uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; result jsonb; begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 perform private.advance_match(p_match);
 select * into strict m from public.matches where id=p_match;
 select jsonb_build_object('schema_version',1,'server_time',clock_timestamp(),'match',to_jsonb(m),
 'participants',(select coalesce(jsonb_agg(to_jsonb(p)),'[]') from public.match_participants p where match_id=p_match),
 'rounds',(select coalesce(jsonb_agg(to_jsonb(r) order by ordinal),'[]') from public.match_rounds r where match_id=p_match and status<>'pending'),
 'results',(select coalesce(jsonb_agg(to_jsonb(r) order by rank),'[]') from public.match_results r where match_id=p_match),
 'tie_rolls',(select coalesce(jsonb_agg(to_jsonb(r) order by attempt),'[]') from private.tie_rolls r where match_id=p_match)) into result;
 return result;
end $$;

create function public.submit_answer(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; r public.match_rounds; c public.content_items; answer jsonb;
 prior private.submissions; t timestamptz; points integer:=0; selected text[]; option jsonb;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 -- A replay returns its existing receipt even if the deadline has since passed.
 select * into prior from private.submissions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id<>p_match or prior.round_id<>p_round or prior.action<>p_action then raise exception 'idempotency_conflict'; end if;
  return jsonb_build_object('accepted',true,'receipt',prior.id);
 end if;
 perform private.advance_match(p_match);
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if not exists(select 1 from public.match_participants where match_id=p_match and user_id=u and eligible) then raise exception 'not_eligible'; end if;
 if exists(select 1 from private.submissions where round_id=p_round and user_id=u) then raise exception 'already_submitted'; end if;
 if jsonb_typeof(p_action)<>'object' or octet_length(p_action::text)>4096 then raise exception 'invalid_action'; end if;
 select * into strict c from public.content_items where id=r.content_id;
 select a.answer into strict answer from private.content_answers a where content_id=r.content_id;
 if c.kind='question_round' then
  if (select count(*) from jsonb_object_keys(p_action))<>1 or not(p_action ? 'option_id') then raise exception 'invalid_action'; end if;
  if not exists(select 1 from jsonb_array_elements(c.payload->'options') o where o->>'id'=p_action->>'option_id') then raise exception 'invalid_option'; end if;
  if p_action->>'option_id'=answer->>'correct_option_id' and r.first_correct is null then
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

create function public.advance_due_matches() returns integer language plpgsql security definer set search_path='' as $$
declare m record; n integer:=0; begin
 for m in select id from public.matches where status in ('countdown','active') order by created_at for update skip locked limit 100 loop
  perform private.advance_match(m.id); n:=n+1;
 end loop; return n;
end $$;
revoke all on function public.start_match(uuid,text),public.match_snapshot(uuid),public.submit_answer(uuid,uuid,uuid,jsonb),public.advance_due_matches() from public,anon,authenticated;
grant execute on function public.start_match(uuid,text),public.match_snapshot(uuid),public.submit_answer(uuid,uuid,uuid,jsonb) to authenticated;
grant execute on function public.advance_due_matches() to service_role;
revoke all on function private.finalize_duel(uuid),private.advance_match(uuid) from public,anon,authenticated;
commit;
