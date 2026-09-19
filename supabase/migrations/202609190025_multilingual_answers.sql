begin;
create function private.normalize_answer(value text) returns text language sql immutable set search_path='' as $$
 select lower(regexp_replace(trim(translate(regexp_replace(normalize(value,NFKD),U&'[\0300-\036F\064B-\065F\0670\0640]','','g'),'٠١٢٣٤٥٦٧٨٩۰۱۲۳۴۵۶۷۸۹','01234567890123456789')),'\s+',' ','g'));
$$;
revoke all on function private.normalize_answer(text) from public,anon,authenticated;
create or replace function public.submit_answer(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; r public.match_rounds; c public.content_items; answer jsonb;
 prior private.submissions; t timestamptz; points integer:=0; is_correct boolean:=false; selected text[]; option jsonb;
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
  if (select count(*) from jsonb_object_keys(p_action))<>1 then raise exception 'invalid_action'; end if;
  if p_action ? 'option_id' then
   if jsonb_typeof(p_action->'option_id')<>'string' or not exists(select 1 from jsonb_array_elements(c.payload->'options') o where o->>'id'=p_action->>'option_id') then raise exception 'invalid_option'; end if;
   is_correct:=p_action->>'option_id'=answer->>'correct_option_id';
  elsif p_action ? 'answer' then
   if jsonb_typeof(p_action->'answer')<>'string' or length(p_action->>'answer') not between 1 and 200 then raise exception 'invalid_action'; end if;
   is_correct:=exists(select 1 from jsonb_array_elements_text(coalesce(answer->'accepted_answers','[]'::jsonb)) a where private.normalize_answer(a)=private.normalize_answer(p_action->>'answer'))
    or exists(select 1 from jsonb_array_elements(c.payload->'options') o where o->>'id'=answer->>'correct_option_id' and private.normalize_answer(o->>'label')=private.normalize_answer(p_action->>'answer'));
  else raise exception 'invalid_action'; end if;
  if is_correct and (m.mode<>'duel' or r.first_correct is null) then
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
  normalized:=private.normalize_answer(p_action->>'answer');
  correct:=exists(select 1 from jsonb_array_elements_text(answer->'accepted_answers') a where private.normalize_answer(a)=normalized);
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
commit;
