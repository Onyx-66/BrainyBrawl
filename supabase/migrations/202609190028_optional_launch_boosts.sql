-- Owner decision: launch matches may start without boosts. Ownership checks remain for any two-item loadout.
begin;
create or replace function public.set_ready(p_room uuid,p_ready boolean) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 if p_ready and (select count(*) from public.loadouts where user_id=u) not in (0,2) then raise exception 'loadout_required'; end if;
 update public.room_members set ready=p_ready,last_seen=clock_timestamp() where room_id=p_room and user_id=u;
 if not found then raise exception 'not_member' using errcode='42501'; end if;
 update public.rooms set version=version+1 where id=p_room;
end $$;
create or replace function public.set_loadout(p_items text[]) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); begin
 perform pg_advisory_xact_lock(hashtextextended(u::text,2));
 if p_items is null or cardinality(p_items) not in (0,2) then raise exception 'invalid_loadout';end if;
 if cardinality(p_items)=2 and (p_items[1]=p_items[2] or (select count(*) from public.inventory i join public.cosmetics c on c.id=i.cosmetic_id where i.user_id=u and c.kind='boost' and c.approved and i.cosmetic_id=any(p_items))<>2) then raise exception 'invalid_loadout'; end if;
 perform r.id from public.rooms r join public.room_members m on m.room_id=r.id where m.user_id=u and r.status<>'closed' order by r.id for update of r;
 if exists(select 1 from public.room_members m join public.rooms r on r.id=m.room_id where m.user_id=u and r.status='playing') then raise exception 'match_active'; end if;
 delete from public.loadouts where user_id=u;
 if cardinality(p_items)=2 then insert into public.loadouts(user_id,slot,cosmetic_id) values(u,1,p_items[1]),(u,2,p_items[2]);end if;
 update public.room_members set ready=false where user_id=u;
end $$;
create or replace function private.start_duel(p_room uuid,p_locale text default 'en') returns uuid
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); r public.rooms; m uuid; phase1 uuid; phase2 uuid;
 item record; ordinal integer:=0; start_time timestamptz;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501'; end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m; end if;
 if r.status<>'lobby' then raise exception 'room_not_open'; end if;
 if r.mode<>'duel' then raise exception 'rules_not_approved'; end if;
 if p_locale not in ('en','fr','ar') then raise exception 'invalid_locale'; end if;
 if (select count(*) from public.room_members where room_id=p_room)<>2 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds')) then raise exception 'players_not_ready'; end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable'; end if;
 if exists(select 1 from public.room_members m where room_id=p_room and (select count(*) from public.loadouts where user_id=m.user_id) not in (0,2)) then raise exception 'loadout_required'; end if;
 if (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale)<15 or
 (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='image_guess' and c.status='APPROVED' and c.locale=p_locale and a.answer->>'scoring_policy' in ('all_selected','correct_only'))<5 then raise exception 'approved_content_required'; end if;
 start_time:=clock_timestamp()+interval '3 seconds';
 insert into public.matches(room_id,mode) values(p_room,r.mode) returning id into m;
 insert into public.match_participants(match_id,user_id,team_id,seat) select m,user_id,team_id,seat from public.room_members where room_id=p_room;
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,0,'question_round','pending',start_time,start_time+interval '450 seconds') returning id into phase1;
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline) values(m,1,'image_guess','pending',start_time+interval '450 seconds',start_time+interval '600 seconds') returning id into phase2;
 for item in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id
 where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale order by random() limit 15 loop
  insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase1,ordinal,item.id,start_time+ordinal*interval '30 seconds',start_time+ordinal*interval '30 seconds'+interval '10 seconds',start_time+(ordinal+1)*interval '30 seconds');
  ordinal:=ordinal+1;
 end loop;
 for item in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id
 where c.kind='image_guess' and c.status='APPROVED' and c.locale=p_locale and a.answer->>'scoring_policy' in ('all_selected','correct_only') order by random() limit 5 loop
  insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase2,ordinal,item.id,start_time+ordinal*interval '30 seconds',start_time+ordinal*interval '30 seconds',start_time+(ordinal+1)*interval '30 seconds');
  ordinal:=ordinal+1;
 end loop;
 update public.rooms set status='playing',matchmaking=false,version=version+1 where id=p_room;
 return m;
end $$;
create or replace function public.start_match(p_room uuid,p_locale text default 'en') returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();r public.rooms;m uuid;phase uuid;content_id text;team_size integer;
 starts timestamptz;duration integer;
begin
 select * into strict r from public.rooms where id=p_room for update;
 if r.host_id<>u then raise exception 'host_required' using errcode='42501';end if;
 if r.mode='duel' then return private.start_duel(p_room,p_locale);end if;
 if r.mode='solo' then raise exception 'rules_not_approved';end if;
 select id into m from public.matches where room_id=p_room;
 if m is not null then return m;end if;
 if p_locale not in ('en','fr','ar') or r.status<>'lobby' then raise exception 'invalid_start';end if;
 team_size:=case when r.mode='duo' then 2 else 4 end;
 if (select count(distinct team_id) from public.room_members where room_id=p_room)<2 or
 exists(select 1 from public.room_members where room_id=p_room and (not ready or last_seen<clock_timestamp()-interval '45 seconds' or team_id is null)) or
 exists(select 1 from public.room_members where room_id=p_room group by team_id having count(*)<>team_size) then raise exception 'players_not_ready';end if;
 if exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id and a.user_id<>b.user_id where a.room_id=p_room and private.blocked(a.user_id,b.user_id)) then raise exception 'unavailable';end if;
 if exists(select 1 from public.room_members rm where rm.room_id=p_room and (select count(*) from public.loadouts where user_id=rm.user_id) not in (0,2)) then raise exception 'loadout_required';end if;
 if (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='question_round' and c.status='APPROVED' and c.locale=p_locale)<(case when r.mode='duo' then 15 else 20 end) then raise exception 'approved_content_required';end if;
 if r.mode='duo' then
  select c.id into content_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='collaborative_puzzle' and c.status='APPROVED' and c.locale=p_locale order by random() limit 1;
  if content_id is null or (select count(*) from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='word_scramble' and c.status='APPROVED' and c.locale=p_locale)<5 then raise exception 'approved_content_required';end if;
  duration:=120;
 else
  select c.id into content_id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='precision_tap' and c.status='APPROVED' and c.locale in (p_locale,'global') order by random() limit 1;
  if content_id is null or not exists(select 1 from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='speed_sort' and c.status='APPROVED' and c.locale=p_locale group by c.payload->>'theme' having count(distinct a.answer->>'bucket')>=2) then raise exception 'approved_content_required';end if;
  duration:=80;
 end if;
 starts:=clock_timestamp()+interval '3 seconds';
 insert into public.matches(room_id,mode) values(p_room,r.mode) returning id into m;
 insert into public.match_participants(match_id,user_id,team_id,seat) select m,user_id,team_id,seat from public.room_members where room_id=p_room;
 insert into private.team_match_state(match_id,locale) values(m,p_locale);
 insert into public.match_phases(match_id,ordinal,kind,status,starts_at,deadline)
  values(m,0,case when r.mode='duo' then 'collaborative_puzzle' else 'precision_tap' end,'pending',starts,starts+make_interval(secs=>duration)) returning id into phase;
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline)
  values(m,phase,0,content_id,starts,starts,starts+make_interval(secs=>duration));
 insert into public.match_phases(match_id,ordinal,kind,status) values
  (m,1,case when r.mode='duo' then 'theme_draft' else 'speed_sort' end,'pending'),
  (m,2,case when r.mode='duo' then 'word_scramble' else 'theme_draft' end,'pending');
 update public.rooms set status='playing',matchmaking=false,version=version+1 where id=p_room;
 return m;
end $$;
commit;
