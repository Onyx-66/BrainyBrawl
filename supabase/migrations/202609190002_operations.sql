begin;
create function private.actor() returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid := auth.uid(); begin
 if u is null or not exists(select 1 from public.profiles where id=u) then raise exception 'unauthorized' using errcode='42501'; end if;
 if exists(select 1 from private.enforcement where user_id=u and (disabled or suspended_until>now())) then raise exception 'account_restricted' using errcode='42501'; end if;
 return u;
end $$;

create function private.on_signup() returns trigger language plpgsql security definer set search_path='' as $$
begin
 insert into public.profiles(id,username) values(new.id,coalesce(nullif(new.raw_user_meta_data->>'username',''),'Player_'||substr(replace(new.id::text,'-',''),1,12)));
 return new;
end $$;
create trigger brainybrawl_signup after insert on auth.users for each row execute function private.on_signup();

create function public.update_profile(p_username text) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 update public.profiles set username=p_username where id=u;
end $$;

-- Email lookup returns only the public preview and uses exact equality. No email is
-- returned. Calls are rate-limited below and blocked accounts remain undiscoverable.
create function private.rate_limit(p_action text,p_limit integer,p_seconds integer) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 perform pg_advisory_xact_lock(hashtextextended(u::text||p_action,0));
 if (select count(*) from private.audit_events where actor=u and action=p_action and created_at>clock_timestamp()-make_interval(secs=>p_seconds))>=p_limit then
  raise exception 'rate_limited';
 end if;
 insert into private.audit_events(actor,action) values(u,p_action);
end $$;
create function public.search_players(p_query text) returns table(id uuid,player_number bigint,username text,avatar_id text)
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 perform private.rate_limit('player_search',10,60);
 if length(trim(p_query))<3 or length(p_query)>254 then return; end if;
 return query select p.id,p.player_number,p.username,p.avatar_id from public.profiles p
 join auth.users a on a.id=p.id
 where p.id<>u and not private.blocked(u,p.id) and
 (lower(p.username)=lower(trim(p_query)) or p.player_number::text=trim(p_query) or lower(a.email)=lower(trim(p_query)))
 limit 10;
end $$;
create function public.friend_action(p_target uuid,p_action text) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 if p_target=u or private.blocked(u,p_target) then raise exception 'unavailable'; end if;
 perform pg_advisory_xact_lock(hashtextextended(least(u,p_target)::text||greatest(u,p_target)::text,0));
 if p_action='request' then
  perform private.rate_limit('friend_request',20,60);
  insert into public.friendships(requester,recipient) values(u,p_target) on conflict do nothing;
 elsif p_action='accept' then
  update public.friendships set status='accepted' where requester=p_target and recipient=u and status='pending';
 elsif p_action in ('decline','remove') then
  delete from public.friendships where (requester=u and recipient=p_target) or (recipient=u and requester=p_target);
 else raise exception 'invalid_action'; end if;
end $$;
create function public.set_block(p_target uuid,p_blocked boolean) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 if p_target=u then raise exception 'invalid_target'; end if;
 perform pg_advisory_xact_lock(hashtextextended(least(u,p_target)::text||greatest(u,p_target)::text,0));
 if p_blocked then
  insert into public.blocks(blocker,blocked) values(u,p_target) on conflict do nothing;
  delete from public.friendships where (requester=u and recipient=p_target) or (recipient=u and requester=p_target);
  delete from public.room_invites where (sender=u and recipient=p_target) or (recipient=u and sender=p_target);
 else delete from public.blocks where blocker=u and blocked=p_target; end if;
end $$;
create function public.report_player(p_target uuid,p_category text,p_details text,p_event uuid default null) returns uuid
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; begin
 perform private.rate_limit('report',10,60);
 if p_event is not null and not exists(select 1 from public.reaction_events e where e.id=p_event and e.user_id=p_target and private.room_member(e.room_id,u)) then raise exception 'invalid_event'; end if;
 insert into public.reports(reporter,target,category,details,event_id) values(u,p_target,p_category,p_details,p_event) returning id into result;
 return result;
end $$;

create function private.room_capacity(p_mode public.game_mode) returns integer language sql immutable set search_path='' as $$
 select case p_mode when 'duel' then 2 when 'solo' then 20 else 40 end
$$;
create function private.assign_member(p_room uuid,p_user uuid) returns void language plpgsql security definer set search_path='' as $$
declare r public.rooms; team uuid; team_size integer; slot integer; pos integer; begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 if exists(select 1 from public.room_members where room_id=p_room and user_id=p_user) then return; end if;
 if exists(select 1 from public.room_members m join public.rooms x on x.id=m.room_id where m.user_id=p_user and x.status<>'closed') then raise exception 'already_in_room'; end if;
 if (select count(*) from public.room_members where room_id=p_room)>=private.room_capacity(r.mode) then raise exception 'room_full'; end if;
 if exists(select 1 from public.room_members where room_id=p_room and private.blocked(user_id,p_user)) then raise exception 'unavailable'; end if;
 if r.mode in ('duo','squad') then
  team_size:=case r.mode when 'duo' then 2 else 4 end;
  select t.id,s.n into team,slot from public.teams t cross join generate_series(0,team_size-1) s(n)
  where t.room_id=p_room and not exists(select 1 from public.room_members m where m.team_id=t.id and m.seat=s.n)
  order by t.position,s.n limit 1;
  if team is null then
   select coalesce(max(position),-1)+1 into pos from public.teams where room_id=p_room;
   insert into public.teams(room_id,name,position) select p_room,username||'''s Team',pos from public.profiles where id=p_user returning id into team;
   slot:=0;
  end if;
 else slot:=0; end if;
 insert into public.room_members(room_id,user_id,team_id,seat) values(p_room,p_user,team,slot);
 update public.rooms set version=version+1 where id=p_room;
end $$;
create function public.create_room(p_mode public.game_mode) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; begin
 perform pg_advisory_xact_lock(hashtextextended(u::text,1));
 perform private.rate_limit('create_room',10,60);
 insert into public.rooms(host_id,mode) values(u,p_mode) returning id into result;
 perform private.assign_member(result,u); return result;
end $$;
create function public.join_room(p_room uuid) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; begin
 perform pg_advisory_xact_lock(hashtextextended(u::text,1));
 select * into strict r from public.rooms where id=p_room for update;
 if not private.room_member(p_room,u) and not r.matchmaking and not exists(select 1 from public.room_invites where room_id=p_room and recipient=u and expires_at>now()) then raise exception 'invite_required' using errcode='42501'; end if;
 perform private.assign_member(p_room,u); return p_room;
end $$;
create function public.quick_match() returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; begin
 perform pg_advisory_xact_lock(hashtextextended('duel_pool',0));
 perform pg_advisory_xact_lock(hashtextextended(u::text,1));
 select r.id into result from public.rooms r where r.mode='duel' and r.matchmaking and r.status='lobby'
 and (select count(*) from public.room_members where room_id=r.id)<2
 and not exists(select 1 from public.room_members where room_id=r.id and private.blocked(user_id,u))
 order by r.created_at limit 1 for update;
 if result is null then
  insert into public.rooms(host_id,mode,matchmaking) values(u,'duel',true) returning id into result;
 end if;
 perform private.assign_member(result,u); return result;
end $$;
create function public.set_matchmaking(p_room uuid,p_enabled boolean) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 update public.rooms set matchmaking=p_enabled,version=version+1 where id=p_room and host_id=u and status='lobby';
 if not found then raise exception 'host_required' using errcode='42501'; end if;
end $$;
create function public.set_ready(p_room uuid,p_ready boolean) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 if p_ready and (select count(*) from public.loadouts where user_id=u)<>2 then raise exception 'loadout_required'; end if;
 update public.room_members set ready=p_ready,last_seen=clock_timestamp() where room_id=p_room and user_id=u;
 if not found then raise exception 'not_member' using errcode='42501'; end if;
 update public.rooms set version=version+1 where id=p_room;
end $$;
create function public.heartbeat(p_room uuid) returns timestamptz language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); t timestamptz:=clock_timestamp(); begin
 update public.room_members set last_seen=t where room_id=p_room and user_id=u;
 if not found then raise exception 'not_member' using errcode='42501'; end if; return t;
end $$;
create function public.leave_room(p_room uuid) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; successor uuid; begin
 select * into strict r from public.rooms where id=p_room for update;
 -- Leaving an active match does not remove the authoritative participant or invent a forfeit.
 if r.status='playing' then update public.room_members set last_seen='epoch' where room_id=p_room and user_id=u; return; end if;
 delete from public.room_members where room_id=p_room and user_id=u;
 if not found then return; end if;
 select user_id into successor from public.room_members where room_id=p_room order by joined_at,user_id limit 1;
 if successor is null then update public.rooms set status='closed',matchmaking=false,version=version+1 where id=p_room;
 else update public.rooms set host_id=case when host_id=u then successor else host_id end,version=version+1 where id=p_room; end if;
end $$;
create function public.invite_friend(p_room uuid,p_friend uuid) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 perform private.rate_limit('invite',20,60);
 if not private.room_member(p_room,u) or private.blocked(u,p_friend) or not exists(select 1 from public.friendships where status='accepted' and ((requester=u and recipient=p_friend) or (recipient=u and requester=p_friend))) then raise exception 'unavailable' using errcode='42501'; end if;
 if not exists(select 1 from public.rooms where id=p_room and status='lobby') then raise exception 'room_not_open'; end if;
 insert into public.room_invites(room_id,sender,recipient) values(p_room,u,p_friend) on conflict(room_id,recipient) do update set sender=u,expires_at=now()+interval '10 minutes';
end $$;
create function public.rename_team(p_room uuid,p_name text) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 update public.teams set name=p_name where room_id=p_room and id in(select team_id from public.room_members where room_id=p_room and user_id=u and seat=0);
 if not found then raise exception 'team_captain_required' using errcode='42501'; end if;
 update public.rooms set version=version+1 where id=p_room;
end $$;

create function public.balances() returns table(currency public.currency_kind,balance bigint) language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 return query select c,coalesce(sum(l.delta),0)::bigint from unnest(enum_range(null::public.currency_kind)) c
 left join public.currency_ledger l on l.currency=c and l.user_id=u group by c;
end $$;
create function public.purchase_item(p_item text,p_key uuid) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); item public.store_items; result public.purchases; balance bigint; begin
 -- Same lock serializes all debits for this user, including different item IDs.
 perform pg_advisory_xact_lock(hashtextextended(u::text,2));
 select * into result from public.purchases where user_id=u and idempotency_key=p_key;
 if found then
  if result.item_id<>p_item then raise exception 'idempotency_conflict'; end if;
  return result.id;
 end if;
 select * into strict item from public.store_items where id=p_item and enabled;
 if not exists(select 1 from public.cosmetics where id=item.cosmetic_id and approved and (not item.vault or (legendary and kind in ('avatar','frame','banner')))) then raise exception 'invalid_item'; end if;
 if exists(select 1 from public.inventory where user_id=u and cosmetic_id=item.cosmetic_id) then raise exception 'already_owned'; end if;
 select coalesce(sum(delta),0) into balance from public.currency_ledger where user_id=u and currency=item.currency;
 if balance<item.price then raise exception 'insufficient_funds'; end if;
 insert into public.purchases(user_id,item_id,idempotency_key) values(u,p_item,p_key) returning * into result;
 insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key)
 values(u,item.currency,-item.price,'store_purchase',result.id::text,'purchase:'||result.id::text);
 insert into public.inventory(user_id,cosmetic_id) values(u,item.cosmetic_id);
 return result.id;
end $$;
create function public.set_loadout(p_items text[]) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 perform pg_advisory_xact_lock(hashtextextended(u::text,2));
 if cardinality(p_items)<>2 or p_items[1]=p_items[2] or (select count(*) from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u and c.kind='boost' and c.approved and i.cosmetic_id=any(p_items))<>2 then raise exception 'invalid_loadout'; end if;
 perform r.id from public.rooms r join public.room_members m on m.room_id=r.id where m.user_id=u and r.status<>'closed' order by r.id for update of r;
 if exists(select 1 from public.room_members m join public.rooms r on r.id=m.room_id where m.user_id=u and r.status='playing') then raise exception 'match_active'; end if;
 delete from public.loadouts where user_id=u;
 insert into public.loadouts(user_id,slot,cosmetic_id) values(u,1,p_items[1]),(u,2,p_items[2]);
 update public.room_members set ready=false where user_id=u;
end $$;
create function public.send_reaction(p_room uuid,p_reaction text) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); result uuid; begin
 if not private.room_member(p_room,u) then raise exception 'not_member' using errcode='42501'; end if;
 if not exists(select 1 from public.reactions where id=p_reaction and approved) then raise exception 'invalid_reaction'; end if;
 perform private.rate_limit('reaction',3,5);
 insert into public.reaction_events(room_id,user_id,reaction_id) values(p_room,u,p_reaction) returning id into result;
 return result;
end $$;

-- Revoke PUBLIC execution on every entry point, then grant only the user operations.
revoke all on all functions in schema public from public,anon,authenticated;
grant execute on function public.update_profile(text),public.search_players(text),public.friend_action(uuid,text),
 public.set_block(uuid,boolean),public.report_player(uuid,text,text,uuid),public.create_room(public.game_mode),
 public.join_room(uuid),public.quick_match(),public.set_matchmaking(uuid,boolean),public.set_ready(uuid,boolean),
 public.heartbeat(uuid),public.leave_room(uuid),public.invite_friend(uuid,uuid),public.rename_team(uuid,text),
 public.balances(),public.purchase_item(text,uuid),public.set_loadout(text[]),public.send_reaction(uuid,text) to authenticated;
revoke all on function private.actor(),private.on_signup(),private.rate_limit(text,integer,integer),private.room_capacity(public.game_mode),private.assign_member(uuid,uuid) from public,anon,authenticated;
commit;
