begin;
-- Joining the public pool never opens or creates a team/Solo room.
create function public.join_public_room(p_mode public.game_mode) returns uuid
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; existing public.rooms; begin
 if p_mode is null or p_mode='duel' then raise exception 'invalid_mode'; end if;
 perform pg_advisory_xact_lock(hashtextextended(u::text,1));
 select r.* into existing from public.rooms r join public.room_members m on m.room_id=r.id
 where m.user_id=u and r.status<>'closed' order by r.created_at limit 1;
 if found then
  if existing.mode=p_mode then return existing.id; end if;
  raise exception 'already_in_room';
 end if;
 perform private.rate_limit('public_room_search',20,60);
 select r.id into result from public.rooms r
 where r.mode=p_mode and r.matchmaking and r.status='lobby'
 and exists(select 1 from public.room_members m where m.room_id=r.id and m.user_id=r.host_id and m.last_seen>clock_timestamp()-interval '45 seconds')
 and not exists(select 1 from private.enforcement e where e.user_id=r.host_id and (e.disabled or e.suspended_until>clock_timestamp()))
 and (select count(*) from public.room_members m where m.room_id=r.id)<private.room_capacity(r.mode)
 and not exists(select 1 from public.room_members m where m.room_id=r.id and private.blocked(m.user_id,u))
 order by r.created_at,r.id limit 1 for update of r skip locked;
 if result is not null then perform private.assign_member(result,u); end if;
 return result;
end $$;
revoke all on function public.join_public_room(public.game_mode) from public,anon;
grant execute on function public.join_public_room(public.game_mode) to authenticated;
commit;
