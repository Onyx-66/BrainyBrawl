begin;
alter table public.profiles add column photo_path text;
alter table public.profiles add constraint photo_path_owned check(photo_path is null or photo_path=id::text||'/photo.jpg');
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types)
values('profile-photos','profile-photos',false,350000,array['image/jpeg']) on conflict(id) do nothing;
create function private.photo_visible(path text) returns boolean language plpgsql security definer set search_path='' as $$
begin
 perform private.actor();
 if path !~ '^[0-9a-f-]{36}/photo[.]jpg$' then return false;end if;
 return private.visible_profile(split_part(path,'/',1)::uuid);
end $$;
revoke all on function private.photo_visible(text) from public,anon;
grant execute on function private.photo_visible(text) to authenticated;
create function private.photo_owned(path text) returns boolean language sql security definer set search_path='' as $$
 select path=private.actor()::text||'/photo.jpg';
$$;
revoke all on function private.photo_owned(text) from public,anon;
grant execute on function private.photo_owned(text) to authenticated;
create policy brawl_photo_read on storage.objects for select to authenticated using(bucket_id='profile-photos' and private.photo_visible(name));
create policy brawl_photo_insert on storage.objects for insert to authenticated with check(bucket_id='profile-photos' and private.photo_owned(name));
create policy brawl_photo_update on storage.objects for update to authenticated using(bucket_id='profile-photos' and private.photo_owned(name)) with check(bucket_id='profile-photos' and private.photo_owned(name));
create policy brawl_photo_delete on storage.objects for delete to authenticated using(bucket_id='profile-photos' and private.photo_owned(name));
create function public.set_profile_photo() returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();begin
 if not exists(select 1 from storage.objects where bucket_id='profile-photos' and name=u::text||'/photo.jpg') then raise exception 'invalid_photo';end if;
 update public.profiles set photo_path=u::text||'/photo.jpg' where id=u;
end $$;
revoke all on function public.set_profile_photo() from public,anon;
grant execute on function public.set_profile_photo() to authenticated;
commit;
