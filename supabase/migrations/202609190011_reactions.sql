begin;
create function public.reaction_catalog(p_locale text) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 if p_locale not in ('en','fr','ar') then raise exception 'invalid_locale'; end if;
 select coalesce(jsonb_agg(jsonb_build_object('id',r.id,'text',l.value,'duration_ms',1500) order by r.id),'[]') into result
 from public.reactions r join public.localization_entries l on l.key=r.localization_key and l.locale=p_locale where r.approved;
 return result;
end $$;
create or replace function public.send_reaction(p_room uuid,p_reaction text) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; r public.rooms; begin
 if not private.room_member(p_room,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict r from public.rooms where id=p_room for update;
 if r.status='closed' then raise exception 'room_not_open'; end if;
 if not exists(select 1 from public.reactions where id=p_reaction and approved) then raise exception 'invalid_reaction'; end if;
 perform private.rate_limit('reaction',3,5);
 insert into public.reaction_events(room_id,user_id,reaction_id) values(p_room,u,p_reaction) returning id into result;
 update public.rooms set version=version+1 where id=p_room;
 return result;
end $$;
revoke all on function public.reaction_catalog(text) from public,anon;
grant execute on function public.reaction_catalog(text) to authenticated;
commit;
