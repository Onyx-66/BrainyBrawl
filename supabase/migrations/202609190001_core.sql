-- Brainy Brawl: default-deny core contract. Apply only to a fresh/local project first.
begin;
create schema if not exists private;
revoke all on schema private from public, anon, authenticated;
create type public.game_mode as enum ('duel','duo','squad','solo');
create type public.currency_kind as enum ('gold','gems','flames');
create type public.match_status as enum ('countdown','active','results','closed');
create sequence private.player_number_seq minvalue 10000000 maxvalue 99999999 no cycle;

create table public.profiles (
 id uuid primary key references auth.users(id) on delete cascade,
 player_number bigint not null unique default nextval('private.player_number_seq'),
 username text not null check (username ~ '^[A-Za-z0-9_]{3,24}$'),
 avatar_id text, frame_id text, banner_id text,
 created_at timestamptz not null default now()
);
create unique index profiles_username_unique on public.profiles(lower(username));
create table private.enforcement (
 user_id uuid primary key references public.profiles(id) on delete cascade,
 suspended_until timestamptz, disabled boolean not null default false, reason text
);
create table public.connected_accounts (
 user_id uuid not null references public.profiles(id) on delete cascade,
 provider text not null check (provider in ('email','google','discord')),
 primary key(user_id,provider)
);
create table public.friendships (
 requester uuid not null references public.profiles(id) on delete cascade,
 recipient uuid not null references public.profiles(id) on delete cascade,
 status text not null default 'pending' check(status in ('pending','accepted')),
 created_at timestamptz not null default now(),
 primary key(requester,recipient), check(requester <> recipient)
);
create unique index friendships_pair_unique on public.friendships(least(requester,recipient),greatest(requester,recipient));
create index friendships_recipient on public.friendships(recipient);
create table public.blocks (
 blocker uuid not null references public.profiles(id) on delete cascade,
 blocked uuid not null references public.profiles(id) on delete cascade,
 created_at timestamptz not null default now(), primary key(blocker,blocked), check(blocker <> blocked)
);
create index blocks_target on public.blocks(blocked);
create table public.reports (
 id uuid primary key default gen_random_uuid(),
 reporter uuid not null references public.profiles(id) on delete cascade,
 target uuid not null references public.profiles(id) on delete cascade,
 category text not null check(category in ('harassment','cheating','profile','spam','other')),
 details text not null default '' check(length(details)<=2000),
 event_id uuid, created_at timestamptz not null default now(), check(reporter <> target)
);
create index reports_reporter on public.reports(reporter);
create table public.rooms (
 id uuid primary key default gen_random_uuid(),
 host_id uuid not null references public.profiles(id),
 mode public.game_mode not null,
 matchmaking boolean not null default false,
 status text not null default 'lobby' check(status in ('lobby','playing','closed')),
 version bigint not null default 1,
 created_at timestamptz not null default now()
);
create index rooms_public_pool on public.rooms(mode,created_at) where matchmaking and status='lobby';
create table public.teams (
 id uuid primary key default gen_random_uuid(), room_id uuid not null references public.rooms(id) on delete cascade,
 name text not null check(length(name) between 1 and 40), position integer not null check(position>=0),
 unique(room_id,position), unique(id,room_id)
);
create table public.room_members (
 room_id uuid not null references public.rooms(id) on delete cascade,
 user_id uuid not null references public.profiles(id) on delete cascade,
 team_id uuid, seat integer not null default 0 check(seat between 0 and 3),
 ready boolean not null default false,
 joined_at timestamptz not null default now(), last_seen timestamptz not null default now(),
 primary key(room_id,user_id),
 foreign key(team_id,room_id) references public.teams(id,room_id), unique(team_id,seat)
);
create index room_members_user on public.room_members(user_id);
create table public.room_invites (
 id uuid primary key default gen_random_uuid(), room_id uuid not null references public.rooms(id) on delete cascade,
 sender uuid not null references public.profiles(id) on delete cascade,
 recipient uuid not null references public.profiles(id) on delete cascade,
 expires_at timestamptz not null default now()+interval '10 minutes',
 unique(room_id,recipient)
);
create index room_invites_recipient on public.room_invites(recipient);

-- Public content never contains answers/point weights. Offline development bundles
-- are separate. Only an approved importer may populate the private answer keys.
create table public.content_items (
 id text primary key, kind text not null check(kind in ('question_round','image_guess','collaborative_puzzle','precision_tap','roll_the_dice','word_scramble','speed_sort','reactions')),
 locale text not null check(locale in ('en','fr','ar','global')),
 schema_version integer not null check(schema_version=1), content_version integer not null check(content_version>0),
 status text not null check(status in ('DEV_SAMPLE','DRAFT','REVIEW','APPROVED','RETIRED')),
 payload jsonb not null check(jsonb_typeof(payload)='object')
);
create index content_selection on public.content_items(kind,locale) where status='APPROVED';
create table private.content_answers (
 content_id text primary key references public.content_items(id) on delete cascade,
 answer jsonb not null check(jsonb_typeof(answer)='object')
);
create table public.matches (
 id uuid primary key default gen_random_uuid(), room_id uuid not null unique references public.rooms(id),
 mode public.game_mode not null, status public.match_status not null default 'countdown',
 version bigint not null default 1, active_round integer not null default 0,
 created_at timestamptz not null default now(), completed_at timestamptz,
 check((status in ('results','closed')) = (completed_at is not null))
);
create table public.match_participants (
 match_id uuid not null references public.matches(id), user_id uuid not null references public.profiles(id),
 team_id uuid, seat integer not null check(seat between 0 and 3), score integer not null default 0,
 eligible boolean not null default true, primary key(match_id,user_id)
);
create index match_participants_user on public.match_participants(user_id);
create table public.match_phases (
 id uuid primary key default gen_random_uuid(), match_id uuid not null references public.matches(id),
 ordinal integer not null check(ordinal>=0), kind text not null,
 status text not null check(status in ('pending','active','results')),
 starts_at timestamptz, deadline timestamptz,
 unique(match_id,ordinal), check(deadline>=starts_at)
);
create table public.match_rounds (
 id uuid primary key default gen_random_uuid(), match_id uuid not null references public.matches(id),
 phase_id uuid not null references public.match_phases(id), ordinal integer not null check(ordinal>=0),
 content_id text not null references public.content_items(id),
 status text not null default 'pending' check(status in ('pending','active','results')),
 starts_at timestamptz, answer_opens_at timestamptz, deadline timestamptz,
 first_correct uuid references public.profiles(id), reveal jsonb,
 unique(match_id,ordinal), check(starts_at<=answer_opens_at and answer_opens_at<deadline)
);
create table private.submissions (
 id uuid primary key default gen_random_uuid(), match_id uuid not null references public.matches(id),
 round_id uuid not null references public.match_rounds(id), user_id uuid not null references public.profiles(id),
 action jsonb not null, received_at timestamptz not null default clock_timestamp(),
 idempotency_key uuid not null, unique(user_id,idempotency_key), unique(round_id,user_id)
);
create table public.score_events (
 id uuid primary key default gen_random_uuid(), match_id uuid not null references public.matches(id),
 round_id uuid not null references public.match_rounds(id), user_id uuid not null references public.profiles(id),
 delta integer not null check(delta>=0), created_at timestamptz not null default now(), unique(round_id,user_id)
);
create table public.match_results (
 match_id uuid not null references public.matches(id), user_id uuid not null references public.profiles(id),
 rank integer not null check(rank>0), score integer not null check(score>=0),
 winner boolean not null, created_at timestamptz not null default now(), primary key(match_id,user_id)
);
create table private.tie_rolls (
 match_id uuid not null references public.matches(id), user_id uuid not null references public.profiles(id),
 attempt integer not null check(attempt>0), roll integer not null check(roll between 1 and 20),
 primary key(match_id,user_id,attempt)
);

create table public.cosmetics (
 id text primary key, kind text not null check(kind in ('avatar','frame','banner','boost')),
 label_key text not null, asset_ref text, legendary boolean not null default false,
 approved boolean not null default false
);
create table public.store_items (
 id text primary key, cosmetic_id text not null references public.cosmetics(id),
 currency public.currency_kind not null, price bigint not null check(price>0),
 vault boolean not null default false, enabled boolean not null default false,
 check((vault and currency='flames') or (not vault and currency<>'flames'))
);
create table public.currency_ledger (
 id uuid primary key default gen_random_uuid(), user_id uuid not null references public.profiles(id),
 currency public.currency_kind not null, delta bigint not null check(delta<>0),
 reason text not null, source_event text not null, idempotency_key text not null unique,
 created_at timestamptz not null default now(),
 check(currency<>'flames' or delta<0 or reason='match_win')
);
create index ledger_user_currency on public.currency_ledger(user_id,currency);
create table public.inventory (
 user_id uuid not null references public.profiles(id), cosmetic_id text not null references public.cosmetics(id),
 acquired_at timestamptz not null default now(), primary key(user_id,cosmetic_id)
);
create table public.loadouts (
 user_id uuid not null references public.profiles(id), slot integer not null check(slot in (1,2)),
 cosmetic_id text not null, primary key(user_id,slot), unique(user_id,cosmetic_id),
 foreign key(user_id,cosmetic_id) references public.inventory(user_id,cosmetic_id)
);
create table public.purchases (
 id uuid primary key default gen_random_uuid(), user_id uuid not null references public.profiles(id),
 item_id text not null references public.store_items(id), idempotency_key uuid not null,
 created_at timestamptz not null default now(), unique(user_id,idempotency_key)
);
create table private.iap_receipts (
 token_hash text primary key, user_id uuid not null references public.profiles(id),
 product_id text not null, verified_at timestamptz not null, credited_at timestamptz
);
create table public.leaderboard_snapshots (
 mode text not null check(mode in ('duel','duo','squad','solo')),
 period text not null check(period in ('weekly','all_time')), period_start date not null,
 user_id uuid not null references public.profiles(id), metric text not null,
 value numeric not null, rank integer not null check(rank>0),
 primary key(mode,period,period_start,user_id,metric)
);
create index leaderboard_rank on public.leaderboard_snapshots(mode,period,period_start,metric,rank);
create table public.reactions (
 id text primary key, localization_key text not null, context text not null,
 approved boolean not null default false
);
create table public.reaction_events (
 id uuid primary key default gen_random_uuid(), room_id uuid not null references public.rooms(id),
 user_id uuid not null references public.profiles(id), reaction_id text not null references public.reactions(id),
 created_at timestamptz not null default clock_timestamp()
);
create index reactions_room_time on public.reaction_events(room_id,created_at);
create table public.localization_entries (
 key text not null, locale text not null check(locale in ('en','fr','ar')), value text not null,
 primary key(key,locale)
);
create table private.audit_events (
 id bigint generated always as identity primary key, actor uuid, action text not null,
 entity_id uuid, created_at timestamptz not null default now(), metadata jsonb not null default '{}'
);

-- Security-definer helpers are boolean-only and never expose hidden block records.
create function private.blocked(a uuid,b uuid) returns boolean language sql stable security definer
set search_path='' as $$ select exists(select 1 from public.blocks where (blocker=a and blocked=b) or (blocker=b and blocked=a)) $$;
create function private.room_member(r uuid,u uuid) returns boolean language sql stable security definer
set search_path='' as $$ select exists(select 1 from public.room_members where room_id=r and user_id=u) $$;
create function private.match_member(m uuid,u uuid) returns boolean language sql stable security definer
set search_path='' as $$ select exists(select 1 from public.match_participants where match_id=m and user_id=u) $$;
create function private.visible_profile(target uuid) returns boolean language sql stable security definer
set search_path='' as $$ select auth.uid()=target or (not private.blocked(auth.uid(),target) and (
 exists(select 1 from public.friendships where status='accepted' and
 ((requester=auth.uid() and recipient=target) or (recipient=auth.uid() and requester=target))) or
 exists(select 1 from public.room_members a join public.room_members b on a.room_id=b.room_id
 where a.user_id=auth.uid() and b.user_id=target))) $$;

-- Revoke inherited defaults first. No direct writes by clients anywhere.
do $$ declare t record; begin
 for t in select tablename from pg_tables where schemaname in ('public','private') loop
  -- Private tables get no API grants; public tables receive read grants below.
  null;
 end loop;
end $$;
revoke all on all tables in schema public from anon, authenticated;
revoke all on all tables in schema private from public, anon, authenticated;
revoke all on all sequences in schema private from public, anon, authenticated;
revoke all on all functions in schema private from public, anon, authenticated;
grant usage on schema private to authenticated;
grant execute on function private.blocked(uuid,uuid),private.room_member(uuid,uuid),
 private.match_member(uuid,uuid),private.visible_profile(uuid) to authenticated;
do $$ declare t record; begin
 for t in select tablename from pg_tables where schemaname='public' loop
  execute format('alter table public.%I enable row level security',t.tablename);
 end loop;
end $$;
grant select on all tables in schema public to authenticated;
create policy profile_read on public.profiles for select to authenticated using(private.visible_profile(id));
create policy accounts_own on public.connected_accounts for select to authenticated using(user_id=auth.uid());
create policy friends_own on public.friendships for select to authenticated using((requester=auth.uid() or recipient=auth.uid()) and not private.blocked(requester,recipient));
create policy blocks_own on public.blocks for select to authenticated using(blocker=auth.uid());
create policy reports_own on public.reports for select to authenticated using(reporter=auth.uid());
create policy rooms_member on public.rooms for select to authenticated using(private.room_member(id,auth.uid()));
create policy members_room on public.room_members for select to authenticated using(private.room_member(room_id,auth.uid()));
create policy teams_room on public.teams for select to authenticated using(private.room_member(room_id,auth.uid()));
create policy invites_recipient on public.room_invites for select to authenticated using((recipient=auth.uid() or sender=auth.uid()) and not private.blocked(sender,recipient));
create policy matches_member on public.matches for select to authenticated using(private.match_member(id,auth.uid()));
create policy participants_match on public.match_participants for select to authenticated using(private.match_member(match_id,auth.uid()));
create policy phases_match on public.match_phases for select to authenticated using(private.match_member(match_id,auth.uid()));
-- Future content IDs and answers must not be visible before the relevant round.
create policy rounds_match on public.match_rounds for select to authenticated using(private.match_member(match_id,auth.uid()) and status<>'pending');
create policy scores_match on public.score_events for select to authenticated using(private.match_member(match_id,auth.uid()));
create policy results_match on public.match_results for select to authenticated using(private.match_member(match_id,auth.uid()));
create policy content_approved on public.content_items for select to authenticated using(status='APPROVED');
create policy cosmetics_approved on public.cosmetics for select to authenticated using(approved);
create policy store_enabled on public.store_items for select to authenticated using(enabled and exists(select 1 from public.cosmetics where id=cosmetic_id and approved));
create policy ledger_own on public.currency_ledger for select to authenticated using(user_id=auth.uid());
create policy inventory_own on public.inventory for select to authenticated using(user_id=auth.uid());
create policy loadouts_own on public.loadouts for select to authenticated using(user_id=auth.uid());
create policy purchases_own on public.purchases for select to authenticated using(user_id=auth.uid());
create policy leaderboard_visible on public.leaderboard_snapshots for select to authenticated using(not private.blocked(auth.uid(),user_id));
create policy reactions_approved on public.reactions for select to authenticated using(approved);
create policy reactions_member on public.reaction_events for select to authenticated using(private.room_member(room_id,auth.uid()) and not private.blocked(auth.uid(),user_id) and not exists(select 1 from public.reports where reporter=auth.uid() and event_id=reaction_events.id));
create policy translations_read on public.localization_entries for select to authenticated using(true);

-- All newly added public functions must explicitly opt in to authenticated access.
alter default privileges in schema public revoke execute on functions from public;
alter default privileges in schema private revoke execute on functions from public;
commit;
