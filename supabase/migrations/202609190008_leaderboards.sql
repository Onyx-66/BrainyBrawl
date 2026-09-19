begin;
create index results_user_created on public.match_results(user_id,created_at);
create index matches_mode_completed on public.matches(mode,completed_at) where status in ('results','closed');
-- Ranks are derived exclusively from finalized server results. Equal values share
-- competition rank. Weekly periods start Monday 00:00 UTC. Offline stays local.
create function public.leaderboard(p_mode text,p_metric text,p_period text default 'all_time',p_friends boolean default false,p_limit integer default 50)
returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); cutoff timestamptz; response jsonb;
begin
 if p_mode not in ('duel','duo','squad','solo') or p_period not in ('weekly','all_time') or p_limit not between 1 and 100 then raise exception 'invalid_filter'; end if;
 if not ((p_mode='duel' and p_metric='win_rate') or (p_mode='solo' and p_metric='highest_score') or
 (p_mode in ('duo','squad') and p_metric in ('highest_score','win_rate','top_rate'))) then raise exception 'invalid_metric'; end if;
 cutoff:=case when p_period='weekly' then date_trunc('week',clock_timestamp() at time zone 'UTC') at time zone 'UTC' else '-infinity'::timestamptz end;
 with totals as (
  select mp.match_id,count(distinct coalesce(mp.team_id,mp.user_id))::numeric as competitors
  from public.match_participants mp join public.matches m on m.id=mp.match_id where m.mode::text=p_mode and m.completed_at>=cutoff group by mp.match_id
 ), aggregate_scores as (
  select r.user_id,count(*) as played,max(r.score) as high_score,
   avg(case when r.winner then 1.0 else 0.0 end)*100 as win_rate,
   avg(case when r.rank<=ceil(t.competitors*case when p_mode='duo' then 0.05 else 0.03 end) then 1.0 else 0.0 end)*100 as top_rate
  from public.match_results r join public.matches m on m.id=r.match_id join totals t on t.match_id=m.id
  where m.mode::text=p_mode and m.status in ('results','closed') and m.completed_at>=cutoff
   and not private.blocked(u,r.user_id)
   and not exists(select 1 from private.enforcement e where e.user_id=r.user_id and (e.disabled or e.suspended_until>clock_timestamp()))
   and (not p_friends or r.user_id=u or exists(select 1 from public.friendships f where f.status='accepted' and
     ((f.requester=u and f.recipient=r.user_id) or (f.recipient=u and f.requester=r.user_id))))
  group by r.user_id
 ), metrics as (
  select a.user_id,p.username,a.played,case p_metric when 'highest_score' then a.high_score::numeric when 'win_rate' then a.win_rate else a.top_rate end as value
  from aggregate_scores a join public.profiles p on p.id=a.user_id
 ), ranked as (
  select *,rank() over(order by value desc) as rank from metrics
 ) select jsonb_build_object('mode',p_mode,'metric',p_metric,'period',p_period,'friends_only',p_friends,
  'generated_at',clock_timestamp(),'rows',(select coalesce(jsonb_agg(to_jsonb(top_rows) order by rank,user_id),'[]') from (select * from ranked order by rank,user_id limit p_limit) top_rows),
  'own',(select to_jsonb(ranked) from ranked where user_id=u)) into response;
 return response;
end $$;
revoke all on function public.leaderboard(text,text,text,boolean,integer) from public,anon;
grant execute on function public.leaderboard(text,text,text,boolean,integer) to authenticated;
commit;
