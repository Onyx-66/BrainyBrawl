# Implementation session

Starting branch: `master`, HEAD `502bd53`. The initial project is a single
`com.brainybrawl.app` Compose application with a greeting and two template tests.
Existing uncommitted build-pack edits, root/content READMEs, reference workbook,
and deletion of the old pack README belong to the user and are preserved.
All 22 build-pack Markdown files, both other READMEs, the master board, Gradle,
resources, tests, and reference workbook were inspected before implementation.
The two workbooks are byte-identical. Assets and Supabase directories are empty.

## Sequence and evidence

Phases follow `ai-build-pack/PROMPTS.md`. This document records actual completion
and limitations, not planned work as completed work.

- 01: repository/documentation/visual/content audit complete.
- 02–35: in progress; verification evidence will be recorded as work proceeds.

## Additional OPEN_DECISIONs discovered in the audit

- DUO_QUALIFICATION RESOLVED by the user (2026-09-19): all Duos continue.
  Bottom-four elimination/top-six qualification was a documentation error.
  Every Duo participates in Word Scramble.
- IMAGE_SCORE: workbook marks nine choices wrong yet gives each positive points.
  Summing all selected points versus only correct choices is unspecified.
- DRAFT_SCORING RESOLVED: 20-second answers after 15-second selection, every correct
  team +1, and persisted random Squad assignments. Chooser disconnect policy remains open.
- SPEED_STREAK: timeout resets a bonus, but no bonus amount is defined. Confirmed
  correct +1/wrong 0 can be implemented independently.
- POWER_UPS: exactly two are required, but effects, initial grants, and catalog
  are absent. No invented competitive boosts or starting balances.
- PRECISION_CONFIG: workbook third-streak bonus is 1; written requirement is 2.
  Written rule wins. Workbook has no absolute rotation speed/hot-zone width.
- MEDIA: referenced image/puzzle files are absent, with no licensing metadata.
  Samples must stay DEV_SAMPLE and cannot become approved production content.
- SCRAMBLE_POINTS: workbook sample values 10/6 are development configuration,
  not approved production economy/gameplay policy.

Existing Solo schedule, IAP tiers, Battle Pass/ads, moderation thresholds,
production sourcing/legal/store assets remain open in the decision register.

## Verification environment

Initial sandbox build could not download Gradle (network permission denied).
Escalated Gradle build authorized; baseline verification is running.
No deployment or production backend changes have been made.

## Verified progress - foundation through authentication

- Phase 02: centralized dependencies, validated public configuration, CI skeleton,
  typed outcomes, secure manifest, application container. Debug APK/unit/lint pass.
- Phase 03: navy/light token themes, typography, buttons/panels/player cards,
  answer selection, timer, image-viewer and showcase components. Full screen visual
  QA and the remaining component variants are still pending.
- Phases 04?05: three transactional schema/operations/engine migrations; default
  deny client writes, RLS and private answer storage. 16 executable database checks
  pass in isolated PGlite, including a 40-player/20-team Duo lobby.
- Phase 06: 1v1 authoritative schedule, submission acceptance, deadline recovery,
  private pending scores, results, twenty-slot server roulette and exactly-once
  Flame award implemented/tested. Other mode orchestration remains pending.
- Phase 07: eight XML files, 87 records and original procedural SVG development
  diagrams; stable workbook IDs preserved. 14 Python negative/positive validation
  tests pass. Kotlin typed parser/repository tests and asset packaging pass.
  Production import/export still needs completion; none of the samples is approved.
- Phases 08?09: email/register/recovery/change-password and Google/Discord SDK
  integration; encrypted Keystore sessions and PKCE verifier; callback routing;
  auth/home/mode/settings/profile shell. No live Supabase credentials are present,
  so live provider/network verification remains unavailable. Offline entry stays
  available without fabricated authentication or balances.
- Phases 10?35: not complete; feature implementation and regression continue.

Android test dependencies were upgraded after Espresso 3.5.1 failed on Android 17
with a missing InputManager method. The unchanged navigation test then passed.
Gradle asset integration was corrected to the AGP Variant API after AGP rejected
Provider-based SourceSet registration. No checks were suppressed to pass.

The Windows sandbox ACL helper stopped starting commands/edits. Project edits and
verification currently run with explicit elevated tool approval; system ACLs were
not changed. The reference-size emulator is running headlessly. Captures so far
were launcher screens after instrumentation cleanup, and are not visual-QA passes.

## Continued progress - social, economy, lobbies

- Profile/social snapshots, profile naming, actual balances/stats, friend search and
  request actions, blocking/reporting, and owned cosmetic equip controls are wired.
  Account deletion, connected-account management and detailed mode stats still
  need implementation; no production moderation thresholds are invented.
- Store catalog/Flame Vault, purchase confirmation, persistent idempotency keys and
  exactly-two owned loadout selection are implemented. There are no invented IAP
  tiers, prices, initial balances, boosts or cosmetic catalog records.
- Lobby UI/repository includes private rooms, quick match, invitation joining,
  teams, ready state, matchmaking toggle, host controls and restore-current-room.
- Realtime uses typed schema-versioned snapshots, room-version invalidation,
  coalescing, five-second recovery polling, ten-second heartbeats, lifecycle-owned
  collection and subscription cleanup. A monotonic server clock is in place.
  Actual WebSocket/network/concurrency integration remains unverified.
- Backend tests now pass 19 groups, including image selection/scoring concealment
  and profile aggregate checks. Python content tests now pass 16 cases, including
  public-payload answer-key separation. Approved-only SQL export rejects the
  development-only bundle without writing an output file.
- Instrumentation passes the launch-to-modes path and two Keystore encryption/
  corruption tests. A genuine graph-initialization race was found and fixed.
- The running unconfigured-auth screen was inspected at 1080x2340; palette,
  contrast and insets are consistent. This is not a full visual QA pass.

Game screens, offline play, most mini-games/mode orchestration, translations,
leaderboards, full regression/release work and final artifacts remain outstanding.

## User correction - Squad draft randomness (2026-09-19)

For each Squad question, the server randomly chooses the theme-picking Squad and
one eligible answering teammate per Squad. The draw is persisted for the question
and cannot change on retry/reconnect. This replaces rank-based Squad draft turns.

## Continued progress - game domain and offline questions

Pure game rules now cover question windows, image selection with an explicit policy,
Precision Tap, puzzle placement ownership, scramble normalization, sort relay,
roulette ties, Duo continuation and random Squad draws. These are reference/domain
rules; they do not imply the corresponding competitive server modes are complete.
Offline questions load XML through the repository, use monotonic 10s/20s windows,
show feedback/results and persist local best/ratio statistics. Unit tests, lint,
debug APK and emulator navigation tests passed. Offline Image Guess remains gated
by the unanswered hidden-point scoring decision. Match visibility hardening now
removes direct content-bank access and first-correct disclosure and adds timed
content and own submission recovery to snapshots; verification is ongoing.

## Continued progress - authoritative UI, localization, rankings and team primitives

- The duel UI consumes typed server snapshots, masks answer options during the
  reading window, submits only action IDs and recovers own accepted submissions.
  It renders timed questions/images, server totals, rankings and tiebreak rolls.
  It has not been exercised against a live Supabase project or approved content.
- Packaged SVG rendering uses AndroidSVG 1.4 with bounded static-only validation;
  external references, scripts, DTDs and oversized files are rejected by tests.
- English/French/Arabic UI resources have matching keys and placeholders. Language,
  theme, audio and vibration preferences persist. Arabic uses RTL and isolates
  inserted values. Audio/vibration playback integration is still outstanding.
- Seven emulator tests passed after correcting an Arabic numeral-system test
  assumption. Android 17 formats this Arabic locale with Latin digits; the test
  now checks the platform result and bidi isolation exactly. The actual Arabic
  settings screen was inspected at 1080x2340 with no clipped labels. Full screen,
  theme, text-scale and game HUD visual QA is still incomplete.
- Leaderboard RPC/UI uses finalized server results with weekly/all-time and
  friends filters, including own rank beyond the returned page. Offline rankings
  remain local; no untrusted offline scores are uploaded.
- Squad draft random choices are persisted per question, server-generated and
  tested against eligible teammates. Full Squad orchestration is still pending.
- Duo puzzle/scramble handlers now validate ownership, deadlines, replay keys and
  first/later scoring. They are tested server primitives, not a complete playable
  Duo flow. Duo/Squad draft answers use the user-approved 20-second, +1-for-every-
  correct-team rule in domain tests; server orchestration still needs integration.
- Preset reaction catalog/sending/reporting is wired to the lobby. Reaction export
  populates catalog/localizations only for approved content. No free-text chat.
  Tests verify throttling and suppression after reports/blocks.
- Backend verification passed 25 groups through migration 11; migration 12 removes
  unchanged snapshot writes to prevent Realtime refresh loops and is being tested.
- Account deletion, connected accounts, complete team modes/mini-game UI, Solo
  schedule, production content/IAP, full multiplayer reliability/performance and
  release/final artifacts remain unfinished. Earlier pending summaries above are
  historical; no production deployment or live backend test has been performed.


## Continued verification: full team flow and contract coverage

Duo now runs the puzzle, all 15 draft questions and five scrambles without
eliminating teams. Small rooms cycle present chooser ranks. Squad runs four
Precision Tap turns, Speed Sort and 20 persisted random drafts. Every correct
team earns +1 after the approved 15-second selection / 20-second answer window.
Server transactions own progression, deadlines, score events, rankings, roulette
and one Flame per winning teammate. Local PGlite verification passes 30 groups,
including a full 40-player Duo schedule and finalization replay checks.

Android now renders team boards, rotating puzzle pieces, partner cursors,
Precision Tap, draggable Speed Sort, Word Scramble, draft selection and team
standings from typed snapshots. Real SQL snapshot fixtures decode in Kotlin;
unit tests, debug build and lint passed after integration. The latest emulator
regression is pending after restarting a disconnected emulator. These results
do not establish live Supabase/WebSocket or multi-connection reliability.

Migration 18 rejects null replay substitutions, samples new deadlines after
locks, and chooses Speed Sort content only from themes with two or more buckets.
The Edge gateway bounds streamed request bytes, rejects malformed UTF-8 and
invalid object shapes, and exposes the team RPC allowlist. Its body-validation
unit checks pass; deployed Deno execution remains unverified.

Account deletion, connected accounts, audio/haptic feedback, complete visual QA,
release checks and final changed-file artifacts still need work. Solo schedule,
Image Guess scoring policy, monetization policy, approved production content,
backend/provider configuration and deployment remain human/external gates.


## Latest continuation: social, reliability, release preparation

- Profile snapshots now include self-only email, verified Auth identity providers
  and server-derived per-mode statistics. Accepted-friend preview RPC/UI omits
  email, providers and balances, rejects blocked/restricted profiles, and clears
  on logout/navigation. Backend tests pass 32 groups through migration 21.
- Catalog/profile/loadout snapshots include localized item names with English
  master fallback. Missing names disable storefront purchases in the UI.
- Local-only diagnostics keep a bounded event-type history without credentials,
  player identifiers, free text or external transmission. No production crash
  collector/analytics vendor is configured.
- Gameplay preferences control audio/haptic feedback. Live HUD/reaction timers
  stop while backgrounded; offline updates pause off-screen and stop after saving
  results. Realtime terminal failures retry with bounded backoff; unit tests cover
  subscription cancellation, retry and logout. Roulette tie resolution is iterative.
- Preset reactions also appear during matches. Catalog loading no longer races
  the lobby action busy flag. The twenty-slot roulette displays the server roll.
- Eleven emulator tests passed before the last image-viewer test was added.
  The actual puzzle component was captured at 1080x2340 and inspected: irregular
  board boundaries, colored artwork thumbnails, selected state and controls are
  readable. This is component QA, not a full live multiplayer screen audit.
- Python content checks pass 18 tests, including approved reaction catalog export
  and refusal to export unapproved content. Production validation correctly fails
  on DEV_SAMPLE records. No content was silently promoted to APPROVED.
- Deno 2.5.6 successfully type-checked the game-action Edge Function; bounded-body
  tests pass. This is local validation, not a deployed function test.
- Debug build/unit/lint and optimized unsigned release assembly passed before the
  latest UI fixes. Full regression is running again. Lint has non-fatal dependency,
  unused-template and KTX-style warnings; language-split delivery was fixed.
- Release signing is environment-only, with no debug-key fallback. Legal links
  render only for configured HTTPS pages. `docs/RELEASE_CHECKLIST.md` identifies
  actual release gates. No signing keys, production legal URLs or deployments exist.
- Account deletion awaits the requested retention decision. Live backend/Auth,
  physical-device/API26, real 40-client networking, comprehensive visual/performance
  QA, production content and monetization verification remain outstanding.


## Verified handover checkpoint

The current Android source passes 49 JVM tests, 14 emulator instrumentation tests,
debug assembly, optimized unsigned release assembly and lint (zero errors,
33 non-fatal warnings). SQL tests pass 32 groups through migration 22; Python
content tests pass 18 cases. Deno type-check and Edge request-body tests pass.
The complete current status, mode qualifications, source artifacts and human gates
are recorded in `SESSION_REPORT.md`. This checkpoint does not declare all 35 phases
or production release complete. Account-deletion retention is awaiting user input;
Image Guess policy, Solo schedule, approved content and live environment inputs
remain unresolved. No production deployment or signing was performed.


## Continuation: public pool and HUD regression

- Added migration 23 and Android public-room entry for Duo/Squad/Solo. Public joins
  preserve private defaults, respect blocks/capacity/mode/host presence, and reuse
  existing membership on retries. SQL verification now passes 33 groups, including
  a full 40-player public Squad room; this is not concurrent hosted load testing.
- Direct account replacement clears the old match selection/subscription. JVM
  verification now passes 50 tests. Result-screen timer polling stops, keyboard
  insets are handled, and mini-games have accurate localized timer labels.
- Three new HUD tests render actual SQL fixture snapshots in English, French
  at 130% text and Arabic RTL at 130% text. Their captures were inspected and
  limitations are recorded in `VISUAL_QA_RESULTS.md`.
- An intermittent full-suite puzzle failure identified artwork state publication
  on a worker thread under the test dispatcher. State publication now explicitly
  uses the Android main looper; all six affected mini-game tests passed afterward.
- The current full regression outcome is maintained in `SESSION_REPORT.md`.
  Historical test counts above describe earlier checkpoints, not the latest state.

Final continuation regression: 50 JVM tests and 17 emulator tests passed with no
failures/errors/skips. Debug and optimized unsigned release builds passed; lint
reported zero errors and 33 warnings. The final English HUD capture was inspected
again after timer changes. The changed-files archive was refreshed against the
original session baseline. Product/environment gates remain; no deployment occurred.

## Offline radio verification and remaining reward rules

Both NavigationSmokeTest cases pass with emulator airplane mode enabled and
Wi-Fi disabled. Original settings (airplane off, Wi-Fi on) were restored and read
back. This proves offline entry/question loading, not a complete fifteen-round
playthrough or the still-gated Image Guess mode. The Flame resource chip now uses
the documented gold. Achievement/daily award definitions are absent from the
specification and are recorded as an additional OPEN_DECISION.

## User-requested UI/account follow-up

See `UI_REDESIGN.md` for the visual redesign, restored account forms/providers and
explicit English content selection that fixes offline startup under Arabic/French.
The user requested a local commit and relaunch after verification. Existing
unrelated documentation changes and deletion remain outside that commit's scope.
