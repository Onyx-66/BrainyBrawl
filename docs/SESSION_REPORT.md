# Brainy Brawl — September 19 implementation report

The Android game and authorized hosted backend are implemented and buildable; release gates below remain. Work stayed on the existing master branch and preserved pre-existing user documentation. No Google Play submission is claimed.

## Architecture and completed behavior

One Android module, Kotlin/Compose feature UI, independent domain/content rules and repository boundaries, encrypted account/session storage and typed Supabase snapshots. Existing working implementation was extended, not replaced with a local multiplayer simulation.

- Dark/light themes, supplied launcher/transparent branding, distinct mode icons, bento modes/account/room actions, username/level/photo header and settings icon. Login/create-account segmented form follows the selected theme. Google/Discord are deliberately disabled/deferred by the owner; email/password registration, login, recovery/change-password interfaces and PKCE integration remain available.
- Device accounts register/login without a backend. Per-account offline scores and photos persist securely. Friend/invitation requests can be queued offline and explicitly delivered after connecting the matching server account. Profile/friends/search/previews/block/report flows are implemented; unrestricted chat is absent.
- A real Mr.onyx server account with private admin membership was provisioned. The installed normal app signed into it, loaded the username/level and opened Store/Leaderboards correctly. Device sign-in gates now explain why a server connection is needed instead of claiming the user is simply logged out.
- Private profile-photo picker/upload, EXIF orientation correction, bounded decoding, 512-pixel JPEG output and source metadata removal. Header shows the account photo or its initial. Fixed an ActivityResultRegistryOwner loss caused by the localized context, which previously crashed the profile picker screen.
- Ledger-authoritative Gold/Gems/Flames, idempotent purchase/loadout RPCs, level 1 plus one level per ten lifetime earned Flames. Store catalog remains empty pending commercial rules. Zero-boost starts now work; two-item selections still require owned approved boosts. Flames cannot be purchased.
- Home/navigation, leaders by mode/period/friends, language dropdown with flags, English/French/Arabic resources, Tajawal Arabic typography, RTL, preset localized reactions and bounded diagnostics.

## Modes and mini-games

| Mode | Implemented schedule |
| --- | --- |
| 1v1 | 15 Question Rounds and five Image Guess rounds, authoritative first-correct question scoring, results, dice tiebreak and winner Flame |
| Duo | 180-second 12×8 / 96-piece collaborative puzzle, fifteen theme drafts, five scrambles; every team continues, small rooms cycle present chooser ranks, every correct designated team +1, two winning Flames |
| Squad | Four 20-second Precision Tap turns, 90-second Speed Sort relay, twenty server-random theme/answerer drafts, team results and four winning Flames |
| Solo Online | 2–20 players; 20s simultaneous Precision Tap, 90s individual common-stream Speed Sort, fifteen questions (10s read +20s answer, every correct player +1), no eliminations, cumulative leader, server dice for tied leaders, exactly one winner Flame |
| Offline | Immediate five-choice/typed Question Round, 45 seconds; five-round Image Guess with ten choices, exactly four confirmations and 30 seconds; per-account best scores, no Flames |

All seven mini-games have implemented domain/server/UI paths: Question Round, Image Guess, Precision Tap, Collaborative Puzzle, Word Scramble, Speed Sort and the 20-slot Roll the Dice. Correct-only hidden Image Guess points implement the owner's decision. Server receipts conceal question correctness/weights until the deadline. Competitive results, clocks, eligibility and rewards are never accepted from client claims.

## Backend/security and content

The owner approved the shared/production project setup and separately approved the follow-up scope. All 30 migrations and 3,861 records were deployed successfully. Hosted inspection confirms zero public tables without RLS and rooms/matches/invites/reactions in the Realtime publication. Private answer keys, membership checks, strict payloads, row locks, action/reward idempotency and service-only administration remain enforced. Client admin membership cannot access service-only deletion operations. Only public URL/key enter Android; credentials remain ignored and outside source artifacts.

Eight UTF-8 XML packs contain 23,558 unique record/option/piece IDs. Question content includes 1,200 original numeracy/logic concepts plus 25 reviewed trivia concepts, each in English/French/Arabic (3,675 question records). Remaining records cover all mini-games/reactions. Formula-based answers are independently recomputed in tests. Typed questions and scrambles accept explicit cross-language aliases with Latin/Arabic normalization and Arabic/Persian digits; arbitrary machine translation at answer time is not used. Fifty-question locale chunks prevent parsing the whole bank on the first offline screen. Runtime XLSX parsing is absent.

## Verification actually performed

| Check | Result |
| --- | --- |
| JVM tests | 63 passed, zero failures/errors/skips |
| Emulator instrumentation | 27 passed on API 37 at 1080 × 2340; isolated QA package preserves normal account data |
| Python tests | 24 passed |
| Production XML validation | Eight files / 3,861 records / 23,558 IDs passed |
| SQL/RLS/game flows | 41 groups passed with real PostgreSQL semantics in PGlite, including full 20-team Duo, Squad and 20-player Solo |
| Edge bounded request parser | Passed |
| Debug build + lint | Passed; zero lint errors, 39 warnings |
| Optimized release APK | Version 1.0.2 built successfully, unsigned; AAB was verified on the preceding revision |
| Native packaging | ZIP and all packaged ELF load segments passed 16 KB alignment checks |
| Hosted verification | Email login, own profile/level/admin, store, leaderboard, anonymous denial and service-only restrictions passed |
| Actual Android hosted navigation | Mr.onyx login, header, Store and Leaderboards verified |

Lint warnings are dependency/update notices, unused resources, intentional icon fallback and KTX/preferences/style suggestions. No test failures were suppressed. Full emulator regression exposed the localized photo-picker owner crash and led to the fix. A Solo tiebreak SQL scoping error was fixed and retested before deployment.

## Reliability and visual QA

Snapshots use schema/version gates, server time anchored to monotonic client time, coalesced Realtime notifications, polling recovery, lifecycle cancellation and bounded subscription cleanup. Duplicate actions/rewards, late input, wrong membership and stale sort indexes are tested. Snapshot reads recover missed deadlines. Hosted concurrent clients and disconnect/load testing remain unverified; no production synthetic match scores were created.

The master mockup and visual checklist were inspected. Auth in both themes, modes, profile, Arabic questions/flags, offline image art and SQL-derived HUD captures were reviewed at the 1080 × 2340 target. Rounded navy panels, cyan/purple accents, green/red feedback and gold rewards are applied. User-supplied logos and original bounded SVG art are used; illustrative mockup usernames/prices were not imported. See `VISUAL_QA_RESULTS.md` for evidence and limits.

## Version 1.0.2 artwork, puzzle and shared background update

Applied the owner’s supplied scene and mockup artwork selectively: four mode shields, three currency icons, two empty-state illustrations and an additional puzzle image. New Duo puzzles show the full image at 60% opacity beneath a regular 12×8 grid. Both 1536×1024 originals produce 96 exact 128×128 PNG pieces each with the adapted reference cutter; byte-for-byte reconstruction tests pass. Six new localized records and migration 030 were deployed after separate owner approval, preserving all 3,855 previous published records and active deadlines. Hosted checksum/function/content verification passed.

Splash uses a randomly selected packaged scene, transparent logo and loading progress tied to artwork/account initialization. Every app page uses the selected scene from `assets/screen/home` at 75% opacity, including auth, profile, settings, rooms and gameplay. The owner clarified that the folder name does not limit the scene to the Home route. Selection happens once per app launch and remains stable through navigation. Each folder currently contains one image, so visible variation requires additional supplied images. Bitmap decoding and caching are bounded, and missing artwork has a safe fallback.

Visual review covered the splash, grid board, updated badges, both home themes and the installed normal app with Mr.onyx still signed in. A transparent-Scaffold text-color regression found during screenshot review was fixed, then the five affected navigation tests passed again. Pixel assertions verify both opacity values and full-color placed pieces. No tests were disabled. Final debug/optimized-release assembly, JVM tests and lint pass; lint has zero errors and 39 warnings. Debug APK signature, ZIP CRC, all 192 tiles, both scenes, secret exclusion, absence of test fixtures and four native ELF/ZIP 16 KB alignment checks pass.

Installable phone test build: `deliverables/BrainyBrawl-1.0.2.apk`, 30,170,598 bytes, Android 8.0/API 26+, package `com.brainybrawl.app`, version code 3. SHA-256: `594f88414231623d91cdff6560ee588b3d22a406c9e54369966341d45050bec1`. This is development-signed, not a Play release. The normal emulator app was updated without clearing account data and relaunched successfully.

## Remaining decisions and genuine release blockers

- Google/Discord setup is explicitly deferred by the owner; both tiles explain their status.
- Play signing credentials and approved privacy/terms/public deletion pages are absent. Online deletion processing needs retention policy; the queue does not claim completed deletion.
- Hosted unattended match/abandoned-room scheduler is not installed. Android uses authenticated SQL RPCs directly; optional Edge gateway is locally implemented but not deployed.
- Commercial catalog, boost effects/grants, monetization, achievements/daily rewards, moderation/analytics policy and team-draft chooser disconnect/forfeit behavior need approved rules.
- Live multi-device/concurrency, signed physical-device/API 26, TalkBack and measured performance checks remain release work. No blanket Play readiness or complete 35-phase certification is claimed.

Engineering implementation/verification phases progressed through the final audit. Production setup is deployed under approval; release preparation is blocked only by the listed policy/environment/integration gates. `RELEASE_CHECKLIST.md` and `release_preflight.py` record them.

## Artifacts

Repository root: `C:/Users/kossa/AndroidStudioProjects/BrainyBrawl`.

- `CHANGED_FILES_MANIFEST.md`: session-relative added/modified/deleted paths and hashes.
- `BrainyBrawl_CHANGED_FILES.zip`: only files changed from the initial SHA-256 inventory at HEAD 502bd53 plus the manifest. No Git/build outputs, local.properties, .env, signing keys, caches, QA screenshots or machine configuration.
- Local-only evidence: `.local/visual-qa/`, `.local/instrumentation-final.log`, `.local/final-build.log`, `.local/backend/verification.json`.
- Installable normal debug APK: `deliverables/BrainyBrawl-1.0.2.apk` (also `app/build/outputs/apk/debug/app-debug.apk`). Unsigned engineering AAB: `app/build/outputs/bundle/release/app-release.aab`.

The requested local Git commit is recorded separately in the final response; nothing is pushed.
