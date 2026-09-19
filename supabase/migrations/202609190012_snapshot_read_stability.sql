begin;
-- Polling an unchanged round must not emit another Realtime UPDATE.
create or replace function private.advance_match(p_match uuid) returns void language plpgsql security definer set search_path='' as $$
declare m public.matches; r public.match_rounds; t timestamptz:=clock_timestamp(); next_round integer; changed integer:=0; begin
 select * into strict m from public.matches where id=p_match for update;
 if m.status in ('results','closed') then return; end if;
 for r in select * from public.match_rounds where match_id=p_match and status<>'results' and deadline<=t order by ordinal for update loop
  insert into public.score_events(match_id,round_id,user_id,delta) select match_id,round_id,user_id,awarded_points from private.submissions where round_id=r.id on conflict(round_id,user_id) do nothing;
  update public.match_participants p set score=p.score+s.awarded_points from private.submissions s where s.round_id=r.id and p.match_id=p_match and p.user_id=s.user_id;
  update public.match_rounds set status='results',reveal=(select answer from private.content_answers where content_id=r.content_id) where id=r.id;
  changed:=changed+1;
 end loop;
 update public.match_phases set status='results' where match_id=p_match and deadline<=t and status<>'results';
 select ordinal into next_round from public.match_rounds where match_id=p_match and starts_at<=t and deadline>t;
 if next_round is not null then
  update public.match_rounds set status='active' where match_id=p_match and ordinal=next_round and status='pending';
  if found then changed:=changed+1; end if;
  update public.match_phases set status='active' where match_id=p_match and starts_at<=t and deadline>t;
  update public.matches set status='active',active_round=next_round,version=version+greatest(changed,0) where id=p_match and (status<>'active' or active_round is distinct from next_round or changed>0);
 elsif not exists(select 1 from public.match_rounds where match_id=p_match and status<>'results') then
  perform private.finalize_duel(p_match);
 end if;
end $$;

commit;
