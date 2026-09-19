begin;
create table private.puzzle_cursors(round_id uuid not null references public.match_rounds(id),user_id uuid not null references public.profiles(id),
 team_id uuid not null,x numeric not null,y numeric not null,updated_at timestamptz not null,primary key(round_id,user_id));
revoke all on private.puzzle_cursors from public,anon,authenticated;
create function public.move_puzzle_cursor(p_match uuid,p_round uuid,p_x numeric,p_y numeric) returns void language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();team uuid;r public.match_rounds;t timestamptz:=clock_timestamp();begin
 if p_x is null or p_y is null or not(p_x between 0 and 1 and p_y between 0 and 1) then raise exception 'invalid_cursor';end if;
 if not private.match_member(p_match,u) then raise exception 'not_member' using errcode='42501';end if;
 perform 1 from public.matches where id=p_match for update;
 select * into strict r from public.match_rounds where id=p_round and match_id=p_match;
 if r.status<>'active' or t<r.starts_at or t>=r.deadline or not exists(select 1 from public.content_items where id=r.content_id and kind='collaborative_puzzle') then raise exception 'round_not_accepting';end if;
 select team_id into strict team from public.match_participants where match_id=p_match and user_id=u and eligible;
 insert into private.puzzle_cursors(round_id,user_id,team_id,x,y,updated_at) values(p_round,u,team,p_x,p_y,t)
 on conflict(round_id,user_id) do update set x=excluded.x,y=excluded.y,updated_at=t where private.puzzle_cursors.updated_at<t-interval '100 milliseconds';
 if found then update public.matches set version=version+1 where id=p_match;end if;
end $$;
create function private.team_view(p_match uuid,p_user uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare s private.team_match_state;m public.matches;p public.match_participants;r public.match_rounds;c public.content_items;
 d private.team_drafts;draft jsonb;board jsonb;sorting private.sort_states;stream text[];item text;answer jsonb;
begin
 select * into s from private.team_match_state where match_id=p_match;
 if not found then return '{}'::jsonb;end if;
 select * into strict m from public.matches where id=p_match;
 select * into strict p from public.match_participants where match_id=p_match and user_id=p_user;
 select * into d from private.team_drafts where match_id=p_match and question=s.question;
 if found and ((m.mode='duo' and s.stage=1) or (m.mode='squad' and s.stage=2)) and m.status not in ('results','closed') then
  draft:=to_jsonb(d)||jsonb_build_object('themes',(select coalesce(jsonb_agg(theme order by theme),'[]') from (
   select distinct ci.payload->>'theme' as theme from public.content_items ci join private.content_answers a on a.content_id=ci.id
   where ci.kind='question_round' and ci.status='APPROVED' and ci.locale=s.locale
    and not exists(select 1 from public.match_rounds mr where mr.match_id=p_match and mr.content_id=ci.id)) themes));
 end if;
 select * into r from public.match_rounds where match_id=p_match and status='active' order by ordinal limit 1;
 if found then
  select * into strict c from public.content_items where id=r.content_id;
  if c.kind='collaborative_puzzle' then
   select a.answer into strict answer from private.content_answers a where content_id=r.content_id;
   board:=jsonb_build_object('kind',c.kind,
    'pieces',(select jsonb_agg(value-'slot'-'row'-'column') from jsonb_array_elements(answer->'pieces')),
    'slots',(select jsonb_agg(jsonb_build_object('id',value->>'slot','polygon',value->>'polygon')) from jsonb_array_elements(answer->'pieces')),
    'placements',(select coalesce(jsonb_agg(jsonb_build_object('piece_id',piece_id,'slot_id',slot_id,'user_id',user_id)),'[]') from private.puzzle_placements where round_id=r.id and team_id=p.team_id),
    'cursors',(select coalesce(jsonb_agg(to_jsonb(pc)),'[]') from private.puzzle_cursors pc where round_id=r.id and team_id=p.team_id and updated_at>clock_timestamp()-interval '2 seconds'),
    'attempts',(select coalesce(jsonb_agg(jsonb_build_object('user_id',ga.user_id,'action',ga.action,'correct',ga.result->'correct')),'[]') from private.game_actions ga where ga.round_id=r.id and ga.team_id=p.team_id and ga.received_at>clock_timestamp()-interval '1500 milliseconds'));
  elsif c.kind='word_scramble' then
   board:=jsonb_build_object('kind',c.kind,'answered',exists(select 1 from private.game_actions ga where ga.round_id=r.id and ga.team_id=p.team_id and ga.result->>'correct'='true'));
  elsif c.kind in ('precision_tap','speed_sort') then
   perform private.prepare_relay_round(r.id);
   if c.kind='precision_tap' then
    board:=jsonb_build_object('kind',c.kind,'config',c.payload,'players',(select jsonb_agg(to_jsonb(ps)) from private.precision_states ps join public.match_participants mp on mp.match_id=p_match and mp.user_id=ps.user_id where ps.round_id=r.id and mp.team_id=p.team_id));
   else
    select * into strict sorting from private.sort_states where round_id=r.id and team_id=p.team_id;
    select items into strict stream from private.sort_streams where round_id=r.id;
    item:=stream[(sorting.item_index%cardinality(stream))+1];
    board:=jsonb_build_object('kind',c.kind,'index',sorting.item_index,'streak',sorting.streak,
     'label',(select payload->>'label' from public.content_items where id=item),
     'active_user',(select user_id from public.match_participants where match_id=p_match and team_id=p.team_id and seat=sorting.item_index%4),
     'buckets',(select jsonb_agg(bucket order by bucket) from (select distinct a.answer->>'bucket' as bucket from private.content_answers a where a.content_id=any(stream)) buckets));
   end if;
  end if;
 end if;
 return jsonb_build_object('team_state',to_jsonb(s),'draft',draft,'board',board,
  'team_rankings',(select coalesce(jsonb_agg(to_jsonb(tr) order by stage,rank),'[]') from private.team_rankings tr where match_id=p_match),
  'teams',(select coalesce(jsonb_agg(jsonb_build_object('id',t.id,'name',t.name)),'[]') from public.teams t where t.room_id=m.room_id));
end $$;
alter function public.match_snapshot(uuid) rename to match_snapshot_base;
alter function public.match_snapshot_base(uuid) set schema private;
revoke all on function private.match_snapshot_base(uuid) from public,anon,authenticated;
create function public.match_snapshot(p_match uuid) returns jsonb language plpgsql security definer set search_path='' as $$
declare u uuid:=private.actor();base jsonb;begin
 base:=private.match_snapshot_base(p_match);
 return base||private.team_view(p_match,u);
end $$;
revoke all on function private.team_view(uuid,uuid) from public,anon,authenticated;
revoke all on function public.match_snapshot(uuid),public.move_puzzle_cursor(uuid,uuid,numeric,numeric) from public,anon;
grant execute on function public.match_snapshot(uuid),public.move_puzzle_cursor(uuid,uuid,numeric,numeric) to authenticated;
commit;
