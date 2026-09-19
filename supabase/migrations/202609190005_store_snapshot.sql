begin;
create function public.store_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select jsonb_build_object(
  'items',(select coalesce(jsonb_agg(jsonb_build_object('id',s.id,'cosmetic_id',c.id,'kind',c.kind,'label_key',c.label_key,'currency',s.currency,'price',s.price,'vault',s.vault,'owned',exists(select 1 from public.inventory where user_id=u and cosmetic_id=c.id)) order by s.vault,s.price,s.id),'[]') from public.store_items s join public.cosmetics c on c.id=s.cosmetic_id where s.enabled and c.approved),
  'boosts',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key)),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u and c.approved and c.kind='boost'),
  'loadout',(select coalesce(jsonb_agg(cosmetic_id order by slot),'[]') from public.loadouts where user_id=u)
 ) into result;return result;
end $$;
revoke all on function public.store_snapshot() from public,anon,authenticated;
grant execute on function public.store_snapshot() to authenticated;
commit;
