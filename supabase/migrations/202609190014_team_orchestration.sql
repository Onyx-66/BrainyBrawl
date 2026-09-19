begin;
create table private.team_match_state(
 match_id uuid primary key references public.matches(id),locale text not null,stage integer not null default 0,
 question integer not null default 0,ranked_teams uuid[] not null default '{}'
);
create table private.team_drafts(
 match_id uuid not null references public.matches(id),question integer not null,
 choosing_team uuid not null,answerers jsonb not null,round_id uuid unique references public.match_rounds(id),
 theme text,primary key(match_id,question)
);
create table private.team_rankings(
 match_id uuid not null references public.matches(id),stage integer not null,team_id uuid not null,
 total integer not null,rolls integer[] not null default '{}',rank integer,
 primary key(match_id,stage,team_id)
);
revoke all on private.team_match_state,private.team_drafts,private.team_rankings from public,anon,authenticated;
create function private.rank_teams(p_match uuid,p_stage integer) returns uuid[] language plpgsql security definer set search_path='' as $$
declare ordered uuid[];begin
 perform 1 from public.matches where id=p_match for update;
 if not exists(select 1 from private.team_rankings where match_id=p_match and stage=p_stage) then
  insert into private.team_rankings(match_id,stage,team_id,total)
   select p_match,p_stage,team_id,sum(score)::integer from public.match_participants where match_id=p_match group by team_id;
  loop
   with ties as(select total,rolls from private.team_rankings where match_id=p_match and stage=p_stage group by total,rolls having count(*)>1)
   update private.team_rankings r set rolls=r.rolls||private.roll_twenty() from ties t
    where r.match_id=p_match and r.stage=p_stage and r.total=t.total and r.rolls=t.rolls;
   exit when not found;
  end loop;
  with ranked as(select team_id,row_number() over(order by total desc,rolls desc,team_id)::integer as rank from private.team_rankings where match_id=p_match and stage=p_stage)
   update private.team_rankings r set rank=x.rank from ranked x where r.match_id=p_match and r.stage=p_stage and r.team_id=x.team_id;
 end if;
 select array_agg(team_id order by rank) into ordered from private.team_rankings where match_id=p_match and stage=p_stage;
 return ordered;
end $$;
create function private.prepare_team_draft(p_match uuid,p_question integer) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches;s private.team_match_state;draw jsonb;chooser uuid;team uuid;answerer uuid;
 answers jsonb:='{}';scheduled integer;best integer;players uuid[];rolls integer[];high integer;winner integer;
begin
 select * into strict m from public.matches where id=p_match for update;
 if exists(select 1 from private.team_drafts where match_id=p_match and question=p_question) then return; end if;
 select * into strict s from private.team_match_state where match_id=p_match;
 if m.mode='squad' then
  draw:=private.squad_draft_draw(p_match,p_question);chooser:=(draw->>'choosing_team')::uuid;answers:=draw->'answerers';
 else
  scheduled:=case when p_question<=10 then p_question else p_question-10 end;
  chooser:=s.ranked_teams[((scheduled-1)%cardinality(s.ranked_teams))+1];
  foreach team in array s.ranked_teams loop
   if p_question<=5 or p_question>10 then
    select user_id into strict answerer from public.match_participants where match_id=p_match and team_id=team and seat=case when p_question<=5 then 0 else 1 end;
   else
    select max(score) into best from public.match_participants where match_id=p_match and team_id=team;
    select array_agg(user_id order by seat) into players from public.match_participants where match_id=p_match and team_id=team and score=best;
    if cardinality(players)=1 then answerer:=players[1];
    else
     loop
      rolls:=array[private.roll_twenty(),private.roll_twenty()];exit when rolls[1]<>rolls[2];
     end loop;
     answerer:=players[case when rolls[1]>rolls[2] then 1 else 2 end];
    end if;
   end if;
   answers:=answers||jsonb_build_object(team::text,answerer);
  end loop;
 end if;
 insert into private.team_drafts(match_id,question,choosing_team,answerers) values(p_match,p_question,chooser,answers);
 update private.team_match_state set question=p_question where match_id=p_match;
 update public.matches set version=version+1 where id=p_match;
end $$;
create function private.finalize_team_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches;teams uuid[];begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return;end if;
 teams:=private.rank_teams(p_match,99);
 insert into public.match_results(match_id,user_id,rank,score,winner)
 select p_match,p.user_id,r.rank,r.total,r.rank=1 from public.match_participants p join private.team_rankings r on r.match_id=p.match_id and r.team_id=p.team_id and r.stage=99 where p.match_id=p_match;
 insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key)
 select user_id,'flames',1,'match_win',p_match::text,'flame:'||p_match::text||':'||user_id::text from public.match_participants where match_id=p_match and team_id=teams[1] on conflict(idempotency_key) do nothing;
 update public.matches set status='results',completed_at=clock_timestamp(),version=version+1 where id=p_match;
 update public.rooms set status='closed',matchmaking=false,version=version+1 where id=m.room_id;
end $$;
create function private.advance_team_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches;s private.team_match_state;r public.match_rounds;t timestamptz:=clock_timestamp();
 teams uuid[];phase uuid;content record;round_ordinal integer;start_at timestamptz;draft_round uuid;
begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return;end if;
 select * into strict s from private.team_match_state where match_id=p_match for update;
 for r in select * from public.match_rounds where match_id=p_match and status<>'results' and deadline<=t order by ordinal for update loop
  insert into public.score_events(match_id,round_id,user_id,delta) select match_id,round_id,user_id,awarded_points from private.submissions where round_id=r.id on conflict(round_id,user_id) do nothing;
  update public.match_participants p set score=p.score+x.awarded_points from private.submissions x where x.round_id=r.id and p.match_id=p_match and p.user_id=x.user_id;
  update public.match_rounds set status='results',reveal=(select answer from private.content_answers where content_id=r.content_id) where id=r.id;
  update private.sort_states set streak=0 where round_id=r.id;
  update public.matches set version=version+1 where id=p_match;
 end loop;
 update public.match_phases set status='active' where match_id=p_match and status='pending' and starts_at<=t and deadline>t;
 update public.match_rounds set status='active' where match_id=p_match and starts_at<=t and deadline>t and status='pending';
 if found then update public.matches set status='active',version=version+1 where id=p_match;end if;
 select mr.ordinal into round_ordinal from public.match_rounds mr where mr.match_id=p_match and mr.status='active' order by mr.ordinal limit 1;
 if round_ordinal is not null then
  update public.matches set active_round=round_ordinal where id=p_match and active_round is distinct from round_ordinal;return;
 end if;
 if exists(select 1 from public.match_rounds where match_id=p_match and status='pending') then return;end if;
 -- All created rounds are finished. Advance the mode plan without inventing a
 -- chooser timeout: a theme waits for the authorized team (reconnect-safe).
 if s.stage=0 then
  teams:=private.rank_teams(p_match,0);
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=0;
  update private.team_match_state set stage=1,ranked_teams=teams where match_id=p_match;
  if m.mode='squad' then
   select id into strict phase from public.match_phases where match_id=p_match and ordinal=1;
   select id into content from public.content_items where kind='speed_sort' and status='APPROVED' and locale=s.locale order by random() limit 1;
   insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline,status)
    values(p_match,phase,1,content.id,t,t,t+interval '90 seconds','active');
   update public.match_phases set status='active',starts_at=t,deadline=t+interval '90 seconds' where id=phase;
   update public.matches set active_round=1,version=version+1 where id=p_match;
  else
   update public.match_phases set status='active',starts_at=t where match_id=p_match and ordinal=1;
   perform private.prepare_team_draft(p_match,1);
  end if;
 elsif s.stage=1 and m.mode='squad' then
  teams:=private.rank_teams(p_match,1);
  update private.team_match_state set stage=2,ranked_teams=teams where match_id=p_match;
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=1;
  update public.match_phases set status='active',starts_at=t where match_id=p_match and ordinal=2;
  perform private.prepare_team_draft(p_match,1);
 elsif (s.stage=1 and m.mode='duo') or (s.stage=2 and m.mode='squad') then
  select round_id into draft_round from private.team_drafts where match_id=p_match and question=s.question;
  if draft_round is null then return;end if;
  if s.question<(case when m.mode='duo' then 15 else 20 end) then perform private.prepare_team_draft(p_match,s.question+1);
  elsif m.mode='squad' then
   update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=2;
   perform private.finalize_team_match(p_match);
  else
   update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=1;
   update private.team_match_state set stage=2 where match_id=p_match;
   select id into strict phase from public.match_phases where match_id=p_match and ordinal=2;
   update public.match_phases set status='active',starts_at=t,deadline=t+interval '75 seconds' where id=phase;
   round_ordinal:=16;
   for content in select c.id from public.content_items c join private.content_answers a on a.content_id=c.id where c.kind='word_scramble' and c.status='APPROVED' and c.locale=s.locale order by random() limit 5 loop
    start_at:=t+(round_ordinal-16)*interval '15 seconds';
    insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline,status)
     values(p_match,phase,round_ordinal,content.id,start_at,start_at,start_at+interval '15 seconds',case when round_ordinal=16 then 'active' else 'pending' end);
    round_ordinal:=round_ordinal+1;
   end loop;
   update public.matches set active_round=16,version=version+1 where id=p_match;
  end if;
 elsif s.stage=2 and m.mode='duo' then
  update public.match_phases set status='results',deadline=t where match_id=p_match and ordinal=2;
  perform private.finalize_team_match(p_match);
 end if;
end $$;
-- Preserve the verified duel engine, dispatching team modes through their plan.
alter function private.advance_match(uuid) rename to advance_duel;
create function private.advance_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
begin
 if (select mode from public.matches where id=p_match) in ('duo','squad') then perform private.advance_team_match(p_match);
 else perform private.advance_duel(p_match);end if;
end $$;
revoke all on function private.rank_teams(uuid,integer),private.prepare_team_draft(uuid,integer),private.finalize_team_match(uuid),private.advance_team_match(uuid),private.advance_match(uuid) from public,anon,authenticated;
commit;
