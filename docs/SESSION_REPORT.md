# Brainy Brawl implementation handover

Status: substantial implementation and local regression complete; the entire
35-phase product is not declared complete. Remaining product/environment gates
are listed below. Nothing has been deployed or submitted to Google Play.

## Implementation and architecture

The existing single Android application module and master branch were preserved.
Kotlin/Compose UI lives in feature packages; game/content rules, repositories,
configuration, encrypted auth storage, localization and design components have
separate responsibilities. Supabase owns competitive transitions and rewards.
No service-role secret, signing key or machine-specific configuration is included.

Auth covers email/password, registration, recovery/change-password and Google/
Discord PKCE integration. Verified recovery callbacks open the password form.
Profiles show permanent player number, owned cosmetics, private email/provider
identities and mode statistics. Friends include search, requests, previews,
blocking and reporting. Account deletion remains gated on the retention decision.
Store/Vault purchases use authoritative balances and persistent idempotency keys;
loadouts require exactly two owned boosts. Achievement/daily award rules and
catalogs are absent, so those awards remain unimplemented. No IAP pricing or boost effects were
invented. Flames remain skill-earned. Home/navigation, leaderboards, settings,
quick reactions and bounded local diagnostics are implemented.

## Mode and mini-game status

| Mode | Implemented | Remaining qualification |
| --- | --- | --- |
| 1v1 | Server schedule, questions/images, scoring, roulette, results and Flames; Android snapshot UI | Approved image policy/content and live backend test |
| Duo | Puzzle, 15 drafts, five scrambles, rankings and two winning Flames | Approved production content/loadouts and live 40-client test |
| Squad | Four 20s Precision Tap turns, 90s sort, 20 random drafts, rankings and four winning Flames | Approved production content/loadouts and live concurrency test |
| Offline | Timed XML questions, answers/results, persisted local best/ratio | Image Guess scoring decision; no invented Offline sort schedule |
| Solo Online | Room/presence shell and reusable game components | Final phase/score schedule explicitly OPEN_DECISION |

Question Round, Image Guess, Collaborative Puzzle, Precision Tap, Word Scramble,
Speed Sort and the twenty-slot roulette have domain/server/UI implementations
appropriate to the above modes. Correct/wrong answers and image weights are
rendered only from closed-round server reveals. No fake local competitive state
replaces the backend.

User decisions are applied: every Duo continues; smaller rooms cycle present
chooser ranks for all 15 questions; Squad chooser and teammate assignments are
persisted server-random draws; each correct designated team earns +1 during the
20-second answer window after the 15-second selection window.

## Database, security and multiplayer

Twenty-three transactional migrations implement schema, default-deny RLS, private
answer/action data, social/economy RPCs, room recovery, authoritative game modes,
leaderboards, reaction safety, private account details and catalog localization.
Only explicitly granted user-scoped functions mutate competitive data. Server
locks, receipt IDs, deadlines and unique reward events prevent duplicate scoring
and rewards. New schedules sample time after acquiring locks. No client-provided
score, balance, winner or Flame value is trusted.

Typed schema/version/time gates reject stale snapshots. Realtime notifications
are coalesced, polling recovers missed events, subscriptions have bounded cleanup,
terminal failures retry with bounded backoff, and logout/account replacement cancels observation.
Public Duo/Squad/Solo searches join only opened rooms with capacity and a recent
host; private, blocked, full and wrong-mode rooms are excluded. Retries restore
existing membership. Searches never implicitly create public team-mode rooms.
Countdowns use a monotonic server-clock estimate. Draft chooser disconnect/forfeit
policy is unresolved; persisted assignments wait for reconnect. Hosted WebSocket,
network partitions and true multi-connection load are not verified by PGlite.

The Edge gateway verifies user auth, uses an RPC allowlist and streams bounded,
strict UTF-8 request bodies. It passed Deno type-checking but was not deployed.

## Content, localization and visual QA

Eight separate UTF-8 XML files contain 87 development records with 474 unique
record/option/piece IDs. The repository/parser and deterministic migration,
validation and approved-only SQL export separate display fields from private
answers. Runtime XLSX parsing is absent. Procedural packaged SVG samples are
bounded/static-only validated. Production validation intentionally rejects the
DEV_SAMPLE bundle; no content was silently approved.

English/French/Arabic UI resources have matching keys/placeholders. Arabic RTL,
locale-aware numbers, bidi isolation and persisted settings are implemented.
Language splitting is disabled so app-bundle installs can switch languages.
FR/AR production gameplay content is not supplied; unavailable locales do not
silently become English questions.

Actual auth, Arabic settings and puzzle component screenshots were inspected at
1080 x 2340. Fixes include compact HUD spacing, piece rotation, clear selected
state, green/red feedback, gold rewards and separate image-viewer dismiss areas.
SQL-fixture HUDs were also inspected in English, French at 130% text and Arabic
RTL at 130% text. `VISUAL_QA_RESULTS.md` records coverage and limitations.
Full live authenticated navigation, physical-device and performance QA remains
unverified. The reference image was treated as visual guidance, not source data.

## Verification actually executed

| Check | Result |
| --- | --- |
| Gradle JVM unit tests | 50 passed; zero failures/errors/skips |
| Emulator instrumentation, Android 17/API 37 | 19 passed; zero failures/errors/skips |
| Offline smoke test with airplane mode on and Wi-Fi off | Two tests passed; prior radio settings restored |
| Debug assembly | Passed |
| Optimized unsigned release assembly / R8 / release vital lint | Passed |
| Debug lint | Zero errors/fatal findings; 34 warnings |
| SQL/RLS/game flow verification through migration 23 | 33 groups passed in PGlite |
| Python content tests | 18 passed |
| XML development validation | Eight files / 87 records / 474 unique IDs passed |
| Production content validation | Correctly rejected unapproved DEV_SAMPLE records |
| Edge request-body tests | Passed bounds, UTF-8, object-shape and cancellation checks |
| Deno 2.5.6 Edge type-check | Passed |
| npm audit of tools/backend-tests dependencies | Zero reported vulnerabilities |

The reference UI/account-flow follow-up is recorded in `UI_REDESIGN.md`.
Offline navigation also passed with airplane mode on and Wi-Fi off.

Lint warnings concern available dependency updates, template resources and KTX/
SharedPreferences style suggestions. No checks were disabled to hide failures.
The full Android test suite exposed and led to fixes for locale-dependent test
setup, image-viewer dismissal, recovery navigation and worker-thread artwork state
publication. Account-switch cancellation also has an explicit regression test. The suite is not a claim
of live provider, billing, shared-backend or production-security certification.

## Required human/environment inputs

1. Answer the pending account-deletion retention question: anonymized completed
   match/purchase/audit records versus erasing the player's records. Email-verified
   destructive cleanup must match that contract before implementation is enabled.
2. Resolve Image Guess hidden-point treatment and the final Solo schedule.
3. Approve production/localized content, image licensing, tuning, scramble values,
   power-up effects/catalog/grants, achievement/daily award rules, monetization and
   chooser disconnect policy.
4. Configure a disposable Supabase project and Auth providers for live integration;
   supply public client configuration securely. Docker/local Supabase was absent.
5. Provide legal pages, signing material outside Git, privacy/data-safety decisions,
   production crash/analytics policy, billing verification and store assets.
6. Explicitly authorize production deployment/submission when release gates pass.

## Artifacts and reproducibility

- `CHANGED_FILES_MANIFEST.md`: additions/modifications/deletions and SHA-256 hashes.
- `BrainyBrawl_CHANGED_FILES.zip`: only session-added/modified source files and the
  manifest, compared with the saved session-start hash inventory.
- `tools/package_changed_files.py --baseline <session-baseline.json>` regenerates
  and validates archive membership and hashes.

The initial user's uncommitted edits and pre-existing README deletion were
preserved. Unchanged user documents/workbooks/mockups, Git metadata, dependencies,
build outputs, local.properties, secrets, keystores and temporary captures are
excluded. A local implementation commit was requested by the user; Git history records
its revision. No push, database deployment or release was performed.
