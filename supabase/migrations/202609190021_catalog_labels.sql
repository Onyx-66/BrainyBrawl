begin;
create function private.cosmetic_labels(p_key text) returns jsonb language sql stable security definer set search_path='' as $$
 select coalesce(jsonb_object_agg(locale,value),'{}') from public.localization_entries where key=p_key and locale in ('en','fr','ar') and length(trim(value))>0
$$;
revoke all on function private.cosmetic_labels(text) from public,anon,authenticated;

create or replace function public.store_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select jsonb_build_object(
  'items',(select coalesce(jsonb_agg(jsonb_build_object('id',s.id,'cosmetic_id',c.id,'kind',c.kind,'label_key',c.label_key,'labels',private.cosmetic_labels(c.label_key),'currency',s.currency,'price',s.price,'vault',s.vault,'owned',exists(select 1 from public.inventory where user_id=u and cosmetic_id=c.id)) order by s.vault,s.price,s.id),'[]') from public.store_items s join public.cosmetics c on c.id=s.cosmetic_id where s.enabled and c.approved),
  'boosts',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key,'labels',private.cosmetic_labels(c.label_key))),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u and c.approved and c.kind='boost'),
  'loadout',(select coalesce(jsonb_agg(cosmetic_id order by slot),'[]') from public.loadouts where user_id=u)
 ) into result;return result;
end $$;
create or replace function private.profile_snapshot_base() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select jsonb_build_object(
  'profile',(select to_jsonb(p) from public.profiles p where id=u),
  'currencies',(select jsonb_agg(to_jsonb(b)) from public.balances() b),
  'inventory',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key,'labels',private.cosmetic_labels(c.label_key),'asset_ref',c.asset_ref)),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u),
  'played',(select count(*) from public.match_results where user_id=u),
  'wins',(select count(*) from public.match_results where user_id=u and winner)
 ) into result; return result;
end $$;
create or replace function public.player_preview(p_target uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();begin
 if private.blocked(u,p_target) or not exists(select 1 from public.profiles where id=p_target) or
 (p_target<>u and not exists(select 1 from public.friendships where status='accepted' and ((requester=u and recipient=p_target) or (requester=p_target and recipient=u)))) or
 exists(select 1 from private.enforcement where user_id=p_target and (disabled or suspended_until>clock_timestamp())) then raise exception 'unavailable';end if;
 return jsonb_build_object(
 'profile',(select jsonb_build_object('id',id,'player_number',player_number,'username',username) from public.profiles where id=p_target),
 'inventory',(select coalesce(jsonb_agg(jsonb_build_object('id',c.id,'kind',c.kind,'label_key',c.label_key,'labels',private.cosmetic_labels(c.label_key))),'[]') from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=p_target and c.approved and c.kind<>'boost'),
 'mode_stats',(select coalesce(jsonb_agg(to_jsonb(stats) order by mode),'[]') from (select m.mode,count(*)::integer as played,count(*) filter(where r.winner)::integer as wins,max(r.score) as best from public.match_results r join public.matches m on m.id=r.match_id where r.user_id=p_target group by m.mode) stats));
end $$;
commit;
