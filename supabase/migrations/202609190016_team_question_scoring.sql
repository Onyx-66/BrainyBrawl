begin;
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
  if prior.match_id<>p_match or prior.round_id<>p_round or prior.action is distinct from p_action then raise exception 'idempotency_conflict'; end if;
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
