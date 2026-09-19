begin;
create table private.game_actions(
 id uuid primary key default gen_random_uuid(),match_id uuid not null references public.matches(id),
 round_id uuid not null references public.match_rounds(id),user_id uuid not null references public.profiles(id),
 team_id uuid not null,idempotency_key uuid not null,action jsonb not null,result jsonb not null,
 points integer not null check(points>=0),received_at timestamptz not null default clock_timestamp(),
 unique(user_id,idempotency_key)
);
create index game_actions_round on private.game_actions(round_id,team_id,received_at);
create table private.puzzle_placements(
 round_id uuid not null references public.match_rounds(id),team_id uuid not null,piece_id text not null,
 slot_id text not null,user_id uuid not null references public.profiles(id),
 primary key(round_id,team_id,piece_id),unique(round_id,team_id,slot_id)
);
revoke all on private.game_actions,private.puzzle_placements from public,anon,authenticated;
-- Actions carry intent only. Receipt timestamps, ownership, validity and points
-- are resolved under the match lock. No client score or timestamp is accepted.
create function public.submit_team_action(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; r public.match_rounds; c public.content_items;
 participant public.match_participants; prior private.game_actions; answer jsonb; piece jsonb;
 t timestamptz; correct boolean:=false; points integer:=0; result jsonb; normalized text;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 select * into prior from private.game_actions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id<>p_match or prior.round_id<>p_round or prior.action<>p_action then raise exception 'idempotency_conflict'; end if;
  return prior.result;
 end if;
 if m.mode<>'duo' then raise exception 'unsupported_mode'; end if;
 select * into strict participant from public.match_participants where match_id=p_match and user_id=u;
 if not participant.eligible or participant.team_id is null then raise exception 'not_eligible'; end if;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if m.status<>'active' or r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if jsonb_typeof(p_action)<>'object' or octet_length(p_action::text)>4096 then raise exception 'invalid_action'; end if;
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
revoke all on function public.submit_team_action(uuid,uuid,uuid,jsonb) from public,anon;
grant execute on function public.submit_team_action(uuid,uuid,uuid,jsonb) to authenticated;
commit;
