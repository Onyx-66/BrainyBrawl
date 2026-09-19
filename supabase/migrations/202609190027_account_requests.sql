begin;
-- Auth accounts may predate installation of the Brainy Brawl signup trigger.
do $$
declare u record; candidate text; suffix integer;
begin
 for u in select a.id,a.raw_user_meta_data->>'username' as username from auth.users a where not exists(select 1 from public.profiles p where p.id=a.id) order by a.id loop
  candidate:=u.username;
  if candidate is null or candidate !~ '^[A-Za-z0-9_.]{3,24}$' or exists(select 1 from public.profiles where lower(username)=lower(candidate)) then
   candidate:='Player_'||substr(replace(u.id::text,'-',''),1,12);suffix:=0;
   while exists(select 1 from public.profiles where lower(username)=lower(candidate)) loop
    suffix:=suffix+1;candidate:='Player_'||substr(replace(u.id::text,'-',''),1,8)||'_'||suffix;
   end loop;
  end if;
  insert into public.profiles(id,username) values(u.id,candidate);
 end loop;
end $$;
create table private.deletion_requests(user_id uuid primary key references public.profiles(id) on delete cascade,requested_at timestamptz not null default now(),status text not null default 'pending' check(status in ('pending','processing','completed')));
alter table private.deletion_requests enable row level security;
revoke all on private.deletion_requests from public,anon,authenticated;
create function public.request_account_deletion() returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=auth.uid();begin
 if u is null or not exists(select 1 from public.profiles where id=u) then raise exception 'unauthorized';end if;
 insert into private.deletion_requests(user_id) values(u) on conflict(user_id) do nothing;
end $$;
revoke all on function public.request_account_deletion() from public,anon;
grant execute on function public.request_account_deletion() to authenticated;
create function public.admin_deletion_requests() returns jsonb language sql security definer set search_path='' as $$
 select coalesce(jsonb_agg(to_jsonb(r) order by r.requested_at),'[]') from private.deletion_requests r where status<>'completed';
$$;
revoke all on function public.admin_deletion_requests() from public,anon,authenticated;
grant execute on function public.admin_deletion_requests() to service_role;
commit;
