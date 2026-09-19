# Production database change review

Target: `lmkwzccjerhkqigifvii.supabase.co`, identified by the owner as shared/production. Read-only inspection on September 19, 2026 found no tables in `public` or `private`, zero Auth users, and the existing `supabase_realtime` publication. The owner explicitly approved this setup in the conversation. Migrations 001–027, 3,600 translated questions and the requested administrator account were subsequently deployed and email authentication/RLS checks passed.

## Requested approval scope

1. Apply the 27 versioned migrations listed below. They create the Brainy Brawl tables, indexes, default-deny RLS, authenticated RPCs, private answer keys, authoritative game/economy functions, account-deletion request queue, and private profile-photo bucket. Existing Supabase Auth/Storage infrastructure is retained; no database reset or existing-player deletion is proposed.
2. Import 3,600 approved question records: 1,200 distinct numeracy/logic concepts, each translated into English, French and Arabic. Display fields and private correct answers are inserted separately. Remaining DEV_SAMPLE packs are excluded.
3. Create the requested Mr.onyx Auth account from ignored `.env` credentials and assign its private administrator membership through the service-only RPC. This does not give the Android client service-role access.
4. Verify schema/RLS and real email/password sign-in against the installed application. These steps do not publish the Android app or configure Google/Discord provider secrets.

Migrations are applied in order, within individual transactions, with a checksummed history. Identical content imports are recorded and skipped on retry. A failure stops deployment; previously committed migrations are retained for forward recovery, not removed by a destructive automatic rollback. The public/private schemas were empty at inspection, so these changes create a new game data model. Auth signup/profile and private Storage policies become active for future accounts.

## Exact migrations

- `supabase/migrations/202609190001_core.sql`
- `supabase/migrations/202609190002_operations.sql`
- `supabase/migrations/202609190003_duel_engine.sql`
- `supabase/migrations/202609190004_player_snapshots.sql`
- `supabase/migrations/202609190005_store_snapshot.sql`
- `supabase/migrations/202609190006_room_recovery.sql`
- `supabase/migrations/202609190007_match_visibility.sql`
- `supabase/migrations/202609190008_leaderboards.sql`
- `supabase/migrations/202609190009_squad_draft_draws.sql`
- `supabase/migrations/202609190010_team_actions.sql`
- `supabase/migrations/202609190011_reactions.sql`
- `supabase/migrations/202609190012_snapshot_read_stability.sql`
- `supabase/migrations/202609190013_relay_actions.sql`
- `supabase/migrations/202609190014_team_orchestration.sql`
- `supabase/migrations/202609190015_team_start_draft.sql`
- `supabase/migrations/202609190016_team_question_scoring.sql`
- `supabase/migrations/202609190017_team_snapshots.sql`
- `supabase/migrations/202609190018_action_hardening.sql`
- `supabase/migrations/202609190019_profile_details.sql`
- `supabase/migrations/202609190020_friend_preview.sql`
- `supabase/migrations/202609190021_catalog_labels.sql`
- `supabase/migrations/202609190022_duel_timing_hardening.sql`
- `supabase/migrations/202609190023_public_room_join.sql`
- `supabase/migrations/202609190024_levels_admin.sql`
- `supabase/migrations/202609190025_multilingual_answers.sql`
- `supabase/migrations/202609190026_profile_photos.sql`
- `supabase/migrations/202609190027_account_requests.sql`

## Verification and remaining limitations

All 27 migrations passed 38 local PGlite verification groups, including RLS, scoring, member authorization, multiplayer phase progression, translated answers, photo ownership and deletion-request restrictions. The content validator and independent arithmetic tests pass. This does not prove hosted Realtime concurrency.

Production deployment alone cannot resolve the missing Image Guess scoring choice, approved two-boost loadout catalog/effects, Solo schedule or legal/retention policies. Google/Discord are disabled in the current Auth configuration and need provider setup. Competitive modes must not be advertised as fully ready until their approved playable content and live match tests are complete.

Reviewable generated files are in `.local/backend/schema.sql`, `approved-content.sql` and `PLAN.md`. They contain no credentials. Source migrations and the content generator/exporter are committed project files. The database password, public/secret API keys and requested administrator password remain in ignored local configuration; only the public URL/key enter Android.

## Follow-up game completion, September 19, 2026

The owner resolved Image Guess as correct-only hidden points, authorized launch matches without boosts, and delegated the Solo schedule to engineering. Follow-up migration 028 permits empty loadouts while validating two-item ownership. Migration 029 implements the documented Solo schedule and reuses authenticated action/snapshot contracts. Neither resets the database nor changes existing player scores.

An incremental content bundle adds 255 reviewed records to the existing 3,600: seed trivia and all seven other localized launch content types. Original XML/assets, explicit translation aliases and independent arithmetic/content tests were reviewed. Existing published IDs are verified unchanged before insertion. The complete local backend suite passes 41 groups, including 20-player Solo, full Duo/Squad progression and exactly-once rewards. Hosted concurrent-device verification remains separate.

## Verified deployment outcome

The owner explicitly approved the follow-up after automatic approval review required a scope-specific confirmation. Migrations 028 and 029 and all 255 incremental content records were successfully applied. Read-only hosted verification confirms 29 migration checksums, 3,855 approved records, zero public tables without RLS, and four Realtime publication tables. Mr.onyx email sign-in, private admin membership and authenticated read RPCs pass. No Play submission, scheduler installation, Edge deployment, invented commercial catalog or production test-player score injection occurred.
