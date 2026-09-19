begin;
create function public.player_preview(p_target uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();begin
 if private.blocked(u,p_target) or not exists(select 1 from public.profiles where id=p_target) or
 (p_target<>u and not exists(select 1 from public.friendships where status='accepted' and ((requester=u and recipient=p_target) or (requester=p_target and recipient=u)))) or
 exists(select 1 from private.enforcement where user_id=p_target and (disabled or suspended_until>clock_timestamp())) then raise exception 'unavailable';end if;
 return jsonb_build_object(
 'profile',(select jsonb_build_object('id',id,'player_number',player_number,'username',username) from public.profiles where id=p_target),
 'inventory',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key)),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=p_target and c.approved and c.kind<>'boost'),
 'mode_stats',(select coalesce(jsonb_agg(to_jsonb(stats) order by mode),'[]') from (select m.mode,count(*)::integer as played,count(*) filter(where r.winner)::integer as wins,max(r.score) as best from public.match_results r join public.matches m on m.id=r.match_id where r.user_id=p_target group by m.mode) stats));
end $$;
revoke all on function public.player_preview(uuid) from public,anon;
grant execute on function public.player_preview(uuid) to authenticated;
commit;
