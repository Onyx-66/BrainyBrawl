begin;
alter table public.profiles drop constraint profiles_username_check;
alter table public.profiles add constraint profiles_username_check check(username ~ '^[A-Za-z0-9_.]{3,24}$');
create table private.administrators(user_id uuid primary key references public.profiles(id) on delete cascade,created_at timestamptz not null default now());
alter table private.administrators enable row level security;
revoke all on private.administrators from public,anon,authenticated;
create function public.bootstrap_admin(p_user uuid) returns void language sql security definer set search_path='' as $$
 insert into private.administrators(user_id) values(p_user) on conflict do nothing;
$$;
revoke all on function public.bootstrap_admin(uuid) from public,anon,authenticated;
grant execute on function public.bootstrap_admin(uuid) to service_role;
alter function public.profile_snapshot() rename to profile_snapshot_details;
alter function public.profile_snapshot_details() set schema private;
revoke all on function private.profile_snapshot_details() from public,anon,authenticated;
create function public.profile_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); earned bigint;
begin
 select coalesce(sum(delta),0) into earned from public.currency_ledger where user_id=u and currency='flames' and delta>0 and reason='match_win';
 return private.profile_snapshot_details()||jsonb_build_object('lifetime_flames',earned,'level',1+earned/10,'is_admin',exists(select 1 from private.administrators where user_id=u));
end $$;
revoke all on function public.profile_snapshot() from public,anon;
grant execute on function public.profile_snapshot() to authenticated;
commit;
