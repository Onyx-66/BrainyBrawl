begin;
alter function public.start_match(uuid,text) rename to start_duel;
alter function public.start_duel(uuid,text) set schema private;
revoke all on function private.start_duel(uuid,text) from public,anon,authenticated;
create function public.start_match(p_room uuid,p_locale text default 'en') returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();r public.rooms;m uuid;phase uuid;content_id text;team_size integer;
 starts timestamptz:=clock_timestamp()+interval '3 seconds';duration integer;
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
 if exists(select 1 from public.room_members rm where rm.room_id=p_room and (select count(*) from public.loadouts where user_id=rm.user_id)<>2) then raise exception 'loadout_required';end if;
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
create function public.choose_draft_theme(p_match uuid,p_question integer,p_theme text) returns uuid language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();m public.matches;draft private.team_drafts;s private.team_match_state;
 phase uuid;item text;created_round uuid;t timestamptz:=clock_timestamp();
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501';end if;
 select * into strict m from public.matches where id=p_match for update;
 perform private.advance_match(p_match);
 select * into strict draft from private.team_drafts where match_id=p_match and question=p_question;
 if not exists(select 1 from public.match_participants where match_id=p_match and user_id=u and team_id=draft.choosing_team and eligible) then raise exception 'not_eligible';end if;
 if draft.round_id is not null then
  if draft.theme is distinct from p_theme then raise exception 'idempotency_conflict';end if;
  return draft.round_id;
 end if;
 select * into strict s from private.team_match_state where match_id=p_match;
 if s.question<>p_question or m.status in ('results','closed') or length(p_theme) not between 1 and 200 then raise exception 'invalid_draft';end if;
 select c.id into item from public.content_items c join private.content_answers a on a.content_id=c.id
  where c.kind='question_round' and c.status='APPROVED' and c.locale=s.locale and c.payload->>'theme'=p_theme
  and not exists(select 1 from public.match_rounds where match_id=p_match and content_id=c.id) order by random() limit 1;
 if item is null then raise exception 'invalid_theme';end if;
 select id into strict phase from public.match_phases where match_id=p_match and kind='theme_draft';
 insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline)
 values(p_match,phase,p_question+case when m.mode='squad' then 1 else 0 end,item,'active',t,t+interval '15 seconds',t+interval '35 seconds') returning id into created_round;
 update private.team_drafts set round_id=created_round,theme=p_theme where match_id=p_match and question=p_question;
 update public.matches set status='active',active_round=p_question+case when m.mode='squad' then 1 else 0 end,version=version+1 where id=p_match;
 return created_round;
end $$;
revoke all on function public.start_match(uuid,text),public.choose_draft_theme(uuid,integer,text) from public,anon;
grant execute on function public.start_match(uuid,text),public.choose_draft_theme(uuid,integer,text) to authenticated;
commit;
