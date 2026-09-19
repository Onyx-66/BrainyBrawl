begin;
create table private.precision_states(
 round_id uuid not null references public.match_rounds(id),user_id uuid not null references public.profiles(id),
 position numeric not null default 0,anchor_at timestamptz not null,zone_start integer not null check(zone_start between 0 and 359),
 streak integer not null default 0,score integer not null default 0,last_hit_pass bigint,
 primary key(round_id,user_id)
);
create table private.sort_streams(round_id uuid primary key references public.match_rounds(id),items text[] not null);
create table private.sort_states(round_id uuid not null references public.match_rounds(id),team_id uuid not null,
 item_index integer not null default 0,streak integer not null default 0,primary key(round_id,team_id));
revoke all on private.precision_states,private.sort_streams,private.sort_states from public,anon,authenticated;
create function private.prepare_relay_round(p_round uuid) returns void language plpgsql security definer set search_path='' as $$
declare r public.match_rounds;c public.content_items;items text[];begin
 select * into strict r from public.match_rounds where id=p_round for update;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 if c.kind='precision_tap' then
  insert into private.precision_states(round_id,user_id,anchor_at,zone_start)
  select r.id,p.user_id,r.starts_at+p.seat*interval '20 seconds',private.random_below(360)
  from public.match_participants p where p.match_id=r.match_id on conflict do nothing;
 elsif c.kind='speed_sort' then
  if not exists(select 1 from private.sort_streams where round_id=p_round) then
   select array_agg(x.id order by random()) into items from public.content_items x join private.content_answers a on a.content_id=x.id
    where x.kind='speed_sort' and x.status='APPROVED' and x.locale=c.locale and x.payload->>'theme'=c.payload->>'theme';
   if cardinality(items) is null or (select count(distinct answer->>'bucket') from private.content_answers where content_id=any(items))<2 then raise exception 'invalid_sort_content'; end if;
   insert into private.sort_streams(round_id,items) values(p_round,items);
  end if;
  insert into private.sort_states(round_id,team_id) select r.id,p.team_id from public.match_participants p where p.match_id=r.match_id group by p.team_id on conflict do nothing;
 else raise exception 'unsupported_round';end if;
end $$;
revoke all on function private.prepare_relay_round(uuid) from public,anon,authenticated;
create function public.submit_relay_action(p_match uuid,p_round uuid,p_key uuid,p_action jsonb) returns jsonb
language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();m public.matches;r public.match_rounds;c public.content_items;member public.match_participants;
 prior private.game_actions;precision private.precision_states;sorting private.sort_states;stream text[];
 t timestamptz;turn_start timestamptz;marker_position numeric;relative numeric;pass bigint;width numeric;speed numeric;zone numeric;
 correct boolean:=false;points integer:=0;next_streak integer;item text;answer jsonb;receipt jsonb;active_seat integer;
begin
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501'; end if;
 select * into strict m from public.matches where id=p_match for update;
 select * into prior from private.game_actions where user_id=u and idempotency_key=p_key;
 if found then
  if prior.match_id<>p_match or prior.round_id<>p_round or prior.action<>p_action then raise exception 'idempotency_conflict'; end if;
  return prior.result;
 end if;
 if m.mode<>'squad' then raise exception 'unsupported_mode'; end if;
 select * into strict member from public.match_participants where match_id=p_match and user_id=u;
 if not member.eligible or member.team_id is null then raise exception 'not_eligible'; end if;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match for update;
 t:=clock_timestamp();
 if m.status<>'active' or r.status<>'active' or t<r.answer_opens_at or t>=r.deadline then raise exception 'round_not_accepting'; end if;
 if jsonb_typeof(p_action)<>'object' or octet_length(p_action::text)>1024 then raise exception 'invalid_action'; end if;
 if (select count(*) from private.game_actions where user_id=u and received_at>t-interval '1 second')>=30 then raise exception 'rate_limited'; end if;
 select * into strict c from public.content_items where id=r.content_id and status='APPROVED';
 perform private.prepare_relay_round(p_round);
 if c.kind='precision_tap' then
  if p_action<>'{}'::jsonb then raise exception 'invalid_action'; end if;
  turn_start:=r.starts_at+member.seat*interval '20 seconds';
  if t<turn_start or t>=turn_start+interval '20 seconds' then raise exception 'not_eligible'; end if;
  select * into strict precision from private.precision_states where round_id=p_round and user_id=u for update;
  speed:=(c.payload->>'rotation_degrees_per_second')::numeric+precision.streak*(c.payload->>'speed_increment')::numeric;
  width:=least((c.payload->>'max_hot_zone_degrees')::numeric,(c.payload->>'hot_zone_degrees')::numeric+precision.streak*(c.payload->>'width_increment')::numeric);
  if speed<=0 or width<=0 or width>360 then raise exception 'invalid_precision_content'; end if;
  marker_position:=precision.position+extract(epoch from t-precision.anchor_at)*speed;
  relative:=marker_position-precision.zone_start;pass:=floor(relative/360);zone:=mod(mod(relative,360)+360,360);
  correct:=zone<=width;
  if correct and precision.last_hit_pass is not distinct from pass then
   next_streak:=precision.streak;
  else
   next_streak:=case when correct then precision.streak+1 else 0 end;
   points:=case when correct then next_streak else 0 end;
  end if;
  update private.precision_states set position=marker_position,anchor_at=t,streak=next_streak,
   score=score+points,last_hit_pass=case when correct then pass else last_hit_pass end where round_id=p_round and user_id=u;
 elsif c.kind='speed_sort' then
  if (select count(*) from jsonb_object_keys(p_action))<>2 or not(p_action ?& array['index','bucket']) or jsonb_typeof(p_action->'index')<>'number' or jsonb_typeof(p_action->'bucket')<>'string' then raise exception 'invalid_action'; end if;
  select * into strict sorting from private.sort_states where round_id=p_round and team_id=member.team_id for update;
  active_seat:=sorting.item_index%4;
  if member.seat<>active_seat then raise exception 'not_eligible'; end if;
  if p_action->>'index'<>sorting.item_index::text then raise exception 'stale_item'; end if;
  select items into strict stream from private.sort_streams where round_id=p_round;
  if not exists(select 1 from private.content_answers ca where ca.content_id=any(stream) and ca.answer->>'bucket'=p_action->>'bucket') then raise exception 'invalid_bucket'; end if;
  item:=stream[(sorting.item_index%cardinality(stream))+1];
  select a.answer into strict answer from private.content_answers a where content_id=item;
  correct:=answer->>'bucket'=p_action->>'bucket';points:=case when correct then 1 else 0 end;
  update private.sort_states set item_index=item_index+1,streak=case when correct then streak+1 else 0 end where round_id=p_round and team_id=member.team_id;
 else raise exception 'unsupported_round';end if;
 receipt:=jsonb_build_object('accepted',true,'correct',correct,'points',points,'receipt',gen_random_uuid());
 insert into private.game_actions(match_id,round_id,user_id,team_id,idempotency_key,action,result,points,received_at)
 values(p_match,p_round,u,member.team_id,p_key,p_action,receipt,points,t);
 if points>0 then
  update public.match_participants set score=score+points where match_id=p_match and user_id=u;
  insert into public.score_events(match_id,round_id,user_id,delta) values(p_match,p_round,u,points)
  on conflict(round_id,user_id) do update set delta=public.score_events.delta+excluded.delta;
 end if;
 update public.matches set version=version+1 where id=p_match;
 return receipt;
end $$;
revoke all on function public.submit_relay_action(uuid,uuid,uuid,jsonb) from public,anon;
grant execute on function public.submit_relay_action(uuid,uuid,uuid,jsonb) to authenticated;
commit;
