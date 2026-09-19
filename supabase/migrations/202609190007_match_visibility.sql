begin;
-- Competitive content is delivered only by the timed, membership-checked snapshot.
-- Offline content is independently bundled through the approved XML pipeline.
drop policy content_approved on public.content_items;
revoke select on public.match_rounds from authenticated;
grant select(id,match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline,reveal)
 on public.match_rounds to authenticated;
create or replace function public.match_snapshot(p_match uuid) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); m public.matches; result jsonb; t timestamptz; begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 perform private.advance_match(p_match);
 t:=clock_timestamp();
 select * into strict m from public.matches where id=p_match;
 select jsonb_build_object('schema_version',1,'server_time',t,'match',to_jsonb(m),
 'participants',(select coalesce(jsonb_agg(to_jsonb(p)||jsonb_build_object('name',case when private.blocked(u,p.user_id) then null else pr.username end) order by p.user_id),'[]')
   from public.match_participants p join public.profiles pr on pr.id=p.user_id where p.match_id=p_match),
 'rounds',(select coalesce(jsonb_agg((to_jsonb(r)-'first_correct')||jsonb_build_object(
   'kind',c.kind,'content',case when t<r.answer_opens_at then c.payload-'options'-'choices' else c.payload end,
   'submission',(select jsonb_build_object('action',s.action,'receipt',s.id) from private.submissions s where s.round_id=r.id and s.user_id=u)) order by r.ordinal),'[]')
   from public.match_rounds r join public.content_items c on c.id=r.content_id where r.match_id=p_match and r.status<>'pending'),
 'starts_at',(select min(starts_at) from public.match_rounds where match_id=p_match),
 'results',(select coalesce(jsonb_agg(to_jsonb(r) order by rank),'[]') from public.match_results r where match_id=p_match),
 'tie_rolls',(select coalesce(jsonb_agg(to_jsonb(r) order by attempt),'[]') from private.tie_rolls r where match_id=p_match)) into result;
 return result;
end $$;
commit;
