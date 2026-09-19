begin;
alter function public.profile_snapshot() rename to profile_snapshot_base;
alter function public.profile_snapshot_base() set schema private;
revoke all on function private.profile_snapshot_base() from public,anon,authenticated;
create function public.profile_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();begin
 return private.profile_snapshot_base()||jsonb_build_object(
  'email',(select email from auth.users where id=u),
  'providers',(select coalesce(jsonb_agg(provider order by provider),'[]') from (select distinct provider from auth.identities where user_id=u and provider in ('email','google','discord')) identity_providers),
  'mode_stats',(select coalesce(jsonb_agg(to_jsonb(stats) order by mode),'[]') from (
   select m.mode,count(*)::integer as played,count(*) filter(where r.winner)::integer as wins,max(r.score) as best
   from public.match_results r join public.matches m on m.id=r.match_id where r.user_id=u group by m.mode
  ) stats));
end $$;
revoke all on function public.profile_snapshot() from public,anon;
grant execute on function public.profile_snapshot() to authenticated;
commit;
