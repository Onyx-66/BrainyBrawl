# Brainy Brawl backend

Local migrations are versioned and transactional. Nothing in this repository has
been deployed. Use a disposable Supabase project/CLI stack first. Never run the
verification fixtures against a shared database.

## Verification

`npm ci --prefix tools/backend-tests --ignore-scripts`

`npm test --prefix tools/backend-tests`

The suite creates an in-memory PostgreSQL database with PGlite, synthetic auth
users, and real roles/RLS. It verifies the migrations and SQL functions, including
40-player room membership, blocking, purchases, 1v1, full 20-team Duo and Squad
phase flows, idempotent Flames, localized reactions, private account data and
friend-only previews.
It does **not** prove network Realtime delivery or concurrent multi-connection
behavior. Those need the local Supabase integration suite and emulator clients.

## Authority

All client writes go through explicitly granted RPCs. Competitive submissions and
answer keys live in `private`; public content must contain only safe presentation
fields. Question/Image Guess scores are published after a round closes; validated puzzle,
Precision Tap, sort and scramble actions publish live score deltas. Submission deadlines come
from the database clock. Clients cannot set scores, balances, winners, or Flames.
The game-action Edge Function forwards the verified user's JWT; it does not use
service-role credentials or compute scores outside the database transaction.

Schedule `advance_due_matches()` with a server-only scheduler in a configured
Supabase environment. Snapshot requests also recover missed transitions using
absolute deadlines. Clients cannot execute the scheduler entry point.

## Content and unresolved rules

Only approved, validated content can start competitive matches. No production
content is shipped yet. Image Guess needs an explicitly approved scoring policy;
there is no default interpretation of wrong-choice point values. Solo remains gated until its phase schedule is approved. Duo and Squad have
complete server phase schedules, but still require approved content and loadouts. All Duos continue per the user's correction.

No IAP credit endpoint is exposed until Google Play verification and an approved
product mapping exist. No client can mint Gems or Flames. No starting currency,
boost inventory or effects are invented.

## Configuration

Android uses `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY`, provided through the
environment or user-level Gradle properties. Edge Functions use platform-injected
`SUPABASE_URL` / `SUPABASE_ANON_KEY`. Never place service-role keys in Android.
Google/Discord provider credentials and permitted callback URLs must be configured
in the Supabase dashboard by an authorized project owner. Production deployment
requires explicit human approval.


Run `npx --yes deno@2.5.6 check supabase/functions/game-action/index.ts` to type-check
the pinned Edge gateway. `edge-body.test.mjs` checks stream bounds and strict UTF-8.
Backend fixture regeneration is opt-in: set `UPDATE_SERVER_FIXTURES=1` before
running the SQL test suite. Fixtures are synthetic and exist only in JVM tests.
No fixture data is imported into a shared backend or the Android production APK.

Password recovery also requires the exact allowed redirect
`brainybrawl://auth/callback?flow=recovery`; a successfully exchanged PKCE code
then opens the password form. The flow parameter only controls navigation and
does not bypass code/session verification.
