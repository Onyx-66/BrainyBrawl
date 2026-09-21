begin;
create table private.image_round_choices(round_id uuid primary key references public.match_rounds(id),ids jsonb not null check(jsonb_array_length(ids)=10));
revoke all on private.image_round_choices from public,anon,authenticated;
create function private.prepare_image_choices() returns trigger language plpgsql security definer set search_path='' as $$
declare a jsonb;selected jsonb;begin
 if not exists(select 1 from public.content_items where id=new.content_id and kind='image_guess' and payload->>'answer_pool'='true') then return new;end if;
 select answer into strict a from private.content_answers where content_id=new.content_id;
 select jsonb_agg(id order by random()) into selected from (
  (select value->>'id' id from jsonb_array_elements(a->'choices') where (value->>'correct')::boolean order by random() limit 4)
  union all
  (select value->>'id' id from jsonb_array_elements(a->'choices') where not (value->>'correct')::boolean order by random() limit 6)
 ) picks;
 if jsonb_array_length(selected)<>10 then raise exception 'invalid_image_pool';end if;
 insert into private.image_round_choices values(new.id,selected);
 return new;
end $$;
create trigger prepare_image_choices after insert on public.match_rounds for each row execute function private.prepare_image_choices();
create function private.round_display(r uuid,payload jsonb,reading boolean) returns jsonb language sql stable security definer set search_path='' as $$
 select case when reading then payload-'options'-'choices'-'answer_pool'
 when exists(select 1 from private.image_round_choices where round_id=r) then
 (payload-'options'-'answer_pool')||jsonb_build_object('options',(select jsonb_agg(o order by k.position) from private.image_round_choices c cross join lateral jsonb_array_elements_text(c.ids) with ordinality k(id,position) join lateral jsonb_array_elements(payload->'options') o on o->>'id'=k.id where c.round_id=r))
 else payload-'answer_pool' end;
$$;
create function private.round_reveal(r uuid,reveal jsonb) returns jsonb language sql stable security definer set search_path='' as $$
 select case when reveal is null then null when exists(select 1 from private.image_round_choices where round_id=r) then
 (reveal-'choices')||jsonb_build_object('choices',(select jsonb_agg(o) from jsonb_array_elements(reveal->'choices') o where exists(select 1 from private.image_round_choices c where c.round_id=r and c.ids ? (o->>'id')))) else reveal end;
$$;
revoke all on function private.prepare_image_choices(),private.round_display(uuid,jsonb,boolean),private.round_reveal(uuid,jsonb) from public,anon,authenticated;
create or replace function private.match_snapshot_base(p_match uuid) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; result jsonb; t timestamptz; begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 perform private.advance_match(p_match);
 t:=clock_timestamp();
 select * into strict m from public.matches where id=p_match;
 select jsonb_build_object('schema_version',1,'server_time',t,'match',to_jsonb(m),
 'participants',(select coalesce(jsonb_agg(to_jsonb(p)||jsonb_build_object('name',case when private.blocked(u,p.user_id) then null else pr.username end) order by p.user_id),'[]')
   from public.match_participants p join public.profiles pr on pr.id=p.user_id where p.match_id=p_match),
 'rounds',(select coalesce(jsonb_agg((to_jsonb(r)-'first_correct'-'reveal')||jsonb_build_object('reveal',private.round_reveal(r.id,r.reveal))||jsonb_build_object(
   'kind',c.kind,'content',private.round_display(r.id,c.payload,t<r.answer_opens_at),
   'submission',(select jsonb_build_object('action',s.action,'receipt',s.id) from private.submissions s where s.round_id=r.id and s.user_id=u)) order by r.ordinal),'[]')
   from public.match_rounds r join public.content_items c on c.id=r.content_id where r.match_id=p_match and r.status<>'pending'),
 'starts_at',(select min(starts_at) from public.match_rounds where match_id=p_match),
 'results',(select coalesce(jsonb_agg(to_jsonb(r) order by rank),'[]') from public.match_results r where match_id=p_match),
 'tie_rolls',(select coalesce(jsonb_agg(to_jsonb(r) order by attempt),'[]') from private.tie_rolls r where match_id=p_match)) into result;
 return result;
end $$;
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
   is_correct:=private.learning_answer(p_action->>'answer',coalesce(answer->'accepted_answers','[]'::jsonb)||(select jsonb_agg(o->>'label') from jsonb_array_elements(c.payload->'options') o where o->>'id'=answer->>'correct_option_id'),
      (select coalesce(jsonb_agg(o->>'label'),'[]') from jsonb_array_elements(c.payload->'options') o where o->>'id'<>answer->>'correct_option_id'));
  else raise exception 'invalid_action'; end if;
  if is_correct and (m.mode<>'duel' or r.first_correct is null) then
   points:=1; update public.match_rounds set first_correct=u where id=p_round;
  end if;
 elsif c.kind='image_guess' then
  if (select count(*) from jsonb_object_keys(p_action))<>1 or jsonb_typeof(p_action->'choice_ids')<>'array' or jsonb_array_length(p_action->'choice_ids')<>4 then raise exception 'invalid_selection'; end if;
  select array_agg(value) into selected from jsonb_array_elements_text(p_action->'choice_ids');
  if (select count(distinct x) from unnest(selected) x)<>4 then raise exception 'invalid_selection'; end if;
  if c.payload->>'answer_pool'='true' and not exists(select 1 from private.image_round_choices p where p.round_id=r.id and p.ids ?& selected) then raise exception 'invalid_selection';end if;
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
