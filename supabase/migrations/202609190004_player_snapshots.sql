begin;
create function public.profile_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select jsonb_build_object(
  'profile',(select to_jsonb(p) from public.profiles p where id=u),
  'currencies',(select jsonb_agg(to_jsonb(b)) from public.balances() b),
  'inventory',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key,'asset_ref',c.asset_ref)),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u),
  'played',(select count(*) from public.match_results where user_id=u),
  'wins',(select count(*) from public.match_results where user_id=u and winner)
 ) into result; return result;
end $$;
create function public.social_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select jsonb_build_object(
  'friends',(select coalesce(jsonb_agg(jsonb_build_object('user_id',p.id,'player_number',p.player_number,'username',p.username,'status',f.status,'incoming',f.recipient=u) order by p.username),'[]')
   from public.friendships f join public.profiles p on p.id=case when f.requester=u then f.recipient else f.requester end
   where (f.requester=u or f.recipient=u) and not private.blocked(u,p.id)),
  'blocks',(select coalesce(jsonb_agg(jsonb_build_object('user_id',b.blocked,'player_number',p.player_number)),'[]')
   from public.blocks b join public.profiles p on p.id=b.blocked where b.blocker=u)
 ) into result; return result;
end $$;
create function public.equip_cosmetic(p_item text) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); kind text; begin
 select c.kind into kind from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u and c.id=p_item and c.approved;
 if kind='avatar' then update public.profiles set avatar_id=p_item where id=u;
 elsif kind='frame' then update public.profiles set frame_id=p_item where id=u;
 elsif kind='banner' then update public.profiles set banner_id=p_item where id=u;
 else raise exception 'invalid_cosmetic'; end if;
end $$;
revoke all on function public.profile_snapshot(),public.social_snapshot(),public.equip_cosmetic(text) from public,anon,authenticated;
grant execute on function public.profile_snapshot(),public.social_snapshot(),public.equip_cosmetic(text) to authenticated;
commit;
