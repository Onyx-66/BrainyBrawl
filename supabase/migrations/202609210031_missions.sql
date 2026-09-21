begin;
create table private.mission_definitions(id text primary key,metric text not null check(metric in ('played','wins','correct')),target integer not null check(target>0),coins integer not null check(coins>=0),flames integer not null check(flames>=0));
insert into private.mission_definitions values
 ('play_1','played',1,100,0),('play_5','played',5,300,1),('play_20','played',20,1000,3),
 ('win_1','wins',1,150,1),('win_5','wins',5,500,2),('correct_25','correct',25,250,1);
create table private.mission_claims(user_id uuid not null references public.profiles(id),mission_id text not null references private.mission_definitions(id),claimed_at timestamptz not null default now(),primary key(user_id,mission_id));
revoke all on private.mission_definitions,private.mission_claims from public,anon,authenticated;
-- Only the trusted claim function can insert mission rewards into this protected ledger.
do $$declare c record;begin
 for c in select conname from pg_constraint where conrelid='public.currency_ledger'::regclass and contype='c' and pg_get_constraintdef(oid) like '%match_win%' loop
  execute format('alter table public.currency_ledger drop constraint %I',c.conname);
 end loop;
end $$;
alter table public.currency_ledger add constraint currency_ledger_reward_reason check(currency<>'flames' or delta<0 or reason in ('match_win','mission_reward'));
create function private.mission_progress(u uuid,metric text) returns bigint language sql stable security definer set search_path='' as $$
 select case metric
 when 'played' then (select count(*) from public.match_results where user_id=u)
 when 'wins' then (select count(*) from public.match_results where user_id=u and winner)
 when 'correct' then (select count(*) from private.submissions s join public.match_rounds r on r.id=s.round_id join public.content_items c on c.id=r.content_id where s.user_id=u and s.awarded_points>0 and c.kind='question_round')
 else 0 end;
$$;
create function public.mission_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();begin
 return (select coalesce(jsonb_agg(to_jsonb(d)||jsonb_build_object('progress',least(d.target,private.mission_progress(u,d.metric)),'claimed',exists(select 1 from private.mission_claims c where c.user_id=u and c.mission_id=d.id)) order by d.target,d.id),'[]') from private.mission_definitions d);
end $$;
create function public.claim_mission(p_mission text) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();d private.mission_definitions;begin
 perform 1 from public.profiles where id=u for update;
 select * into strict d from private.mission_definitions where id=p_mission;
 if exists(select 1 from private.mission_claims where user_id=u and mission_id=d.id) then return;end if;
 if private.mission_progress(u,d.metric)<d.target then raise exception 'mission_incomplete';end if;
 insert into private.mission_claims(user_id,mission_id) values(u,d.id);
 if d.coins>0 then insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values(u,'gold',d.coins,'mission_reward',d.id,'mission:'||u||':'||d.id||':gold');end if;
 if d.flames>0 then insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values(u,'flames',d.flames,'mission_reward',d.id,'mission:'||u||':'||d.id||':flames');end if;
end $$;
revoke all on function private.mission_progress(uuid,text) from public,anon,authenticated;
revoke all on function public.mission_snapshot(),public.claim_mission(text) from public,anon;
grant execute on function public.mission_snapshot(),public.claim_mission(text) to authenticated;
create or replace function public.profile_snapshot() returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor(); earned bigint;
begin
 select coalesce(sum(delta),0) into earned from public.currency_ledger where user_id=u and currency='flames' and delta>0 and reason in ('match_win','mission_reward');
 return private.profile_snapshot_details()||jsonb_build_object('lifetime_flames',earned,'level',1+earned/10,'is_admin',exists(select 1 from private.administrators where user_id=u));
end $$;
commit;
