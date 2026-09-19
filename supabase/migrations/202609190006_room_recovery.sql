begin;
create or replace function public.heartbeat(p_room uuid) returns timestamptz language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); t timestamptz:=clock_timestamp(); begin
 if not private.room_member(p_room,u) then raise exception 'not_member' using errcode='42501'; end if;
 perform 1 from public.rooms where id=p_room for update;
 update public.room_members set last_seen=t where room_id=p_room and user_id=u and last_seen<t-interval '8 seconds';
 if found then update public.rooms set version=version+1 where id=p_room; end if;
 return t;
end $$;
create function public.current_room() returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r uuid; begin
 select m.room_id into r from public.room_members m join public.rooms room on room.id=m.room_id
 where m.user_id=u and room.status<>'closed' order by room.created_at desc limit 1;return r;
end $$;
create function public.room_snapshot(p_room uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; next_host uuid; result jsonb; begin
 if not private.room_member(p_room,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict r from public.rooms where id=p_room for update;
 if r.status='lobby' and not exists(select 1 from public.room_members where room_id=p_room and user_id=r.host_id and last_seen>clock_timestamp()-interval '45 seconds') then
  select user_id into next_host from public.room_members where room_id=p_room and last_seen>clock_timestamp()-interval '45 seconds' order by joined_at,user_id limit 1;
  if next_host is not null then update public.rooms set host_id=next_host,version=version+1 where id=p_room returning * into r; end if;
 end if;
 select jsonb_build_object('schema_version',1,'server_time',clock_timestamp(),'room',to_jsonb(r),
  'members',(select coalesce(jsonb_agg(jsonb_build_object('user_id',m.user_id,'team_id',m.team_id,'seat',m.seat,'ready',m.ready,'last_seen',m.last_seen,'username',case when private.blocked(u,m.user_id) then null else p.username end) order by m.joined_at,m.user_id),'[]') from public.room_members m join public.profiles p on p.id=m.user_id where m.room_id=p_room),
  'teams',(select coalesce(jsonb_agg(to_jsonb(t) order by position),'[]') from public.teams t where room_id=p_room),
  'match_id',(select id from public.matches where room_id=p_room),
  'reactions',(select coalesce(jsonb_agg(jsonb_build_object('id',e.id,'user_id',e.user_id,'reaction_id',e.reaction_id,'created_at',e.created_at)),'[]') from public.reaction_events e where room_id=p_room and created_at>clock_timestamp()-interval '5 seconds' and not private.blocked(u,e.user_id) and not exists(select 1 from public.reports where reporter=u and event_id=e.id))
 ) into result;return result;
end $$;
create function public.pending_invites() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result jsonb; begin
 select coalesce(jsonb_agg(jsonb_build_object('room_id',i.room_id,'sender',p.username,'mode',r.mode)),'[]') into result
 from public.room_invites i join public.rooms r on r.id=i.room_id join public.profiles p on p.id=i.sender
 where i.recipient=u and i.expires_at>now() and r.status='lobby' and not private.blocked(u,i.sender);
 return result;
end $$;
create function public.close_abandoned_rooms() returns integer language plpgsql security definer set search_path='' as $$
declare n integer; begin
 update public.rooms r set status='closed',matchmaking=false,version=version+1 where status='lobby'
 and not exists(select 1 from public.room_members where room_id=r.id and last_seen>clock_timestamp()-interval '10 minutes');
 get diagnostics n=row_count;return n;
end $$;
revoke all on function public.current_room(),public.room_snapshot(uuid),public.pending_invites(),public.close_abandoned_rooms() from public,anon,authenticated;
grant execute on function public.current_room(),public.room_snapshot(uuid),public.pending_invites() to authenticated;
grant execute on function public.close_abandoned_rooms() to service_role;
-- Only small authoritative/version or preset-event tables enter Realtime.
-- PGlite tests have no publication; local/hosted Supabase creates this publication.
do $$ declare t text; begin
 if exists(select 1 from pg_publication where pubname='supabase_realtime') then
  foreach t in array array['rooms','matches','room_invites','reaction_events'] loop
   if not exists(select 1 from pg_publication_tables where pubname='supabase_realtime' and schemaname='public' and tablename=t) then
    execute format('alter publication supabase_realtime add table public.%I',t);
   end if;
  end loop;
 end if;
end $$;
commit;
