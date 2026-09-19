begin;
create table private.squad_draft_draws(
 match_id uuid not null references public.matches(id),question integer not null check(question between 1 and 20),
 choosing_team uuid not null,answerers jsonb not null,created_at timestamptz not null default clock_timestamp(),
 primary key(match_id,question)
);
revoke all on private.squad_draft_draws from public,anon,authenticated;
create function private.random_below(p_bound integer) returns integer language plpgsql volatile set search_path='' as $$
declare bits bigint; ceiling bigint; begin
 if p_bound not between 1 and 1000 then raise exception 'invalid_bound'; end if;
 ceiling:=4294967296-(4294967296%p_bound);
 loop
  bits:=('x'||substr(replace(gen_random_uuid()::text,'-',''),1,8))::bit(32)::bigint;
  if bits<ceiling then return (bits%p_bound)::integer; end if;
 end loop;
end $$;
-- Internal engine primitive. Only orchestration may create a draw. Client retries
-- and reconnects read the persisted result; they never provide seeds or reroll it.
create function private.squad_draft_draw(p_match uuid,p_question integer) returns jsonb
language plpgsql security definer set search_path='' as $$
declare m public.matches; draw private.squad_draft_draws; teams uuid[]; players uuid[];
 team uuid; answers jsonb:='{}'; chooser uuid;
begin
 select * into strict m from public.matches where id=p_match for update;
 if m.mode<>'squad' or p_question not between 1 and 20 then raise exception 'invalid_draft'; end if;
 select * into draw from private.squad_draft_draws where match_id=p_match and question=p_question;
 if found then return to_jsonb(draw); end if;
 if exists(select 1 from public.match_participants where match_id=p_match and team_id is null) then raise exception 'invalid_team'; end if;
 select array_agg(distinct team_id order by team_id) into teams from public.match_participants where match_id=p_match and eligible;
 if cardinality(teams) is null then raise exception 'invalid_team'; end if;
 chooser:=teams[private.random_below(cardinality(teams))+1];
 foreach team in array teams loop
  if (select count(*) from public.match_participants where match_id=p_match and team_id=team)<>4 then raise exception 'invalid_team'; end if;
  select array_agg(user_id order by seat,user_id) into players from public.match_participants where match_id=p_match and team_id=team and eligible;
  answers:=answers||jsonb_build_object(team::text,players[private.random_below(cardinality(players))+1]);
 end loop;
 insert into private.squad_draft_draws(match_id,question,choosing_team,answerers) values(p_match,p_question,chooser,answers) returning * into draw;
 return to_jsonb(draw);
end $$;
revoke all on function private.random_below(integer),private.squad_draft_draw(uuid,integer) from public,anon,authenticated;
commit;
