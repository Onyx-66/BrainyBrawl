## Latest UI handoff — 1.0.6

Games cards/icons, active-only bottom labels, resource header shortcuts and the supplied 10 avatars/20 frames are complete. APK: `deliverables/BrainyBrawl-1.0.6.apk`. Verification: 76 JVM / 40 emulator / 24 Python tests passed; 0 lint errors (74 warnings). Android Studio opened successfully with a fresh local cache, and the app is running on emulator-5554. See `docs/GAMES_HEADER_UPDATE.md` for implementation and validation details.

# Brainy Brawl — September 20 implementation report

The Android game and authorized hosted backend are implemented and buildable; release gates below remain. Work stayed on the existing master branch and preserved pre-existing user documentation. No Google Play submission is claimed.

## Architecture and completed behavior

One Android module, Kotlin/Compose feature UI, independent domain/content rules and repository boundaries, encrypted account/session storage and typed Supabase snapshots. Existing working implementation was extended, not replaced with a local multiplayer simulation.

- Dark/light themes, supplied launcher/transparent branding, distinct mode icons, bento modes/account/room actions, username/level/photo header and settings icon. Login/create-account segmented form follows the selected theme. Google/Discord are deliberately disabled/deferred by the owner; email/password registration, login, recovery/change-password interfaces and PKCE integration remain available.
- One account flow registers/logs in online or offline. Accounts created offline automatically attempt real Supabase authentication when internet returns; existing legacy accounts require password re-entry once. Verified email logins also cache an offline verifier. Per-account offline scores and photos persist securely. Friend/invitation requests can be queued offline and explicitly delivered after connecting the matching server account. Profile/friends/search/previews/block/report flows are implemented; unrestricted chat is absent.
- A real Mr.onyx server account with private admin membership was provisioned. The installed normal app signed into it, loaded the username/level and opened Store/Leaderboards correctly. Connection panels report internet, verification or reauthentication requirements instead of imposing a permanent device-account restriction.
- Profile now uses twelve avatar slots and twelve square-frame slots, shared by the header and player card. The gallery picker was removed at owner request. Appearance preferences synchronize to user-owned Auth metadata and selected avatar images upload as bounded 512-pixel JPEGs to private Storage. Legacy photo sanitation remains tested. Native previews work until the final pack is added.
- Ledger-authoritative Gold/Gems/Flames, idempotent purchase/loadout RPCs, level 1 plus one level per ten lifetime earned Flames. Store catalog remains empty pending commercial rules. Zero-boost starts now work; two-item selections still require owned approved boosts. Flames cannot be purchased.
- Home/navigation, leaders by mode/period/friends, language dropdown with flags, English/French/Arabic resources, Tajawal Arabic typography, RTL, preset localized reactions and bounded diagnostics.

## Modes and mini-games

| Mode | Implemented schedule |
| --- | --- |
| 1v1 | 15 Question Rounds and five Image Guess rounds, authoritative first-correct question scoring, results, dice tiebreak and winner Flame |
| Duo | 180-second 12×8 / 96-piece collaborative puzzle, fifteen theme drafts, five scrambles; every team continues, small rooms cycle present chooser ranks, every correct designated team +1, two winning Flames |
| Squad | Four 20-second Precision Tap turns, 90-second Speed Sort relay, twenty server-random theme/answerer drafts, team results and four winning Flames |
| Solo Online | 2–20 players; 20s simultaneous Precision Tap, 90s individual common-stream Speed Sort, fifteen questions (10s read +20s answer, every correct player +1), no eliminations, cumulative leader, server dice for tied leaders, exactly one winner Flame |
| Offline | A chooser opens immediate five-choice/typed Question Round (45 seconds), Image Guess (ten choices, four selections, 30 seconds) or a 96-piece Puzzle (180 seconds); per-account practice bests, no Flames |

All seven mini-games have implemented domain/server/UI paths: Question Round, Image Guess, Precision Tap, Collaborative Puzzle, Word Scramble, Speed Sort and the 20-slot Roll the Dice. Correct-only hidden Image Guess points implement the owner's decision. Server receipts conceal question correctness/weights until the deadline. Competitive results, clocks, eligibility and rewards are never accepted from client claims.

## Backend/security and content

The owner approved the shared/production project setup and separately approved the follow-up scope. All 30 migrations and 3,861 records were deployed successfully. Hosted inspection confirms zero public tables without RLS and rooms/matches/invites/reactions in the Realtime publication. Private answer keys, membership checks, strict payloads, row locks, action/reward idempotency and service-only administration remain enforced. Client admin membership cannot access service-only deletion operations. Only public URL/key enter Android; credentials remain ignored and outside source artifacts.

Eight UTF-8 XML packs contain 23,558 unique record/option/piece IDs. Question content includes 1,200 original numeracy/logic concepts plus 25 reviewed trivia concepts, each in English/French/Arabic (3,675 question records). Remaining records cover all mini-games/reactions. Formula-based answers are independently recomputed in tests. Typed questions and scrambles accept explicit cross-language aliases with Latin/Arabic normalization and Arabic/Persian digits; arbitrary machine translation at answer time is not used. Fifty-question locale chunks prevent parsing the whole bank on the first offline screen. Runtime XLSX parsing is absent.

## Verification actually performed

| Check | Result |
| --- | --- |
| JVM tests | 76 passed, zero failures/errors/skips |
| Emulator instrumentation | 36 passed on API 37 at 1080 × 2340; three profile checks rerun after final contrast adjustments; isolated QA package preserves normal account data |
| Python tests | 24 passed |
| Production XML validation | Eight files / 3,861 records / 23,558 IDs passed |
| SQL/RLS/game flows | 41 groups passed with real PostgreSQL semantics in PGlite, including full 20-team Duo, Squad and 20-player Solo |
| Edge bounded request parser | Passed |
| Debug build + lint | Passed; zero lint errors, 68 warnings |
| Optimized release APK | Version 1.0.4 built successfully, unsigned; AAB was verified on an earlier revision |
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

## Version 1.0.3 account, navigation and practice update

Online mode cards now lead to their own Create private room / Join a room page. Offline is a peer mode card with Questions, Image Guess and the new all-96-piece solo Puzzle. Profile has a framed player card, gold level badge, separated email/sign-in methods and paired stats. Google/Discord buttons are compact native logo/name buttons; recovery text has readable theme backing. The opaque language menu matches its anchor width, verified within two pixels. Shared scene backgrounds remain on all pages.

Account creation uses one online/offline-capable flow. A device-bound encrypted, owner-checked pending credential envelope permits automatic connection after offline creation; it is erased after successful authentication or logout and rejected after seven days. Normal offline records use PBKDF2 verifiers. Password changes require the configured server and refresh the offline verifier after success. Explicit backup/device-transfer exclusions protect authentication storage. No client-side role, Flame or competitive-score trust was introduced. See `ACCOUNT_MODES_UPDATE.md` for behavior and security details.

Hosted Auth settings were read, not changed: email registration is enabled, email confirmation is required, and Google/Discord remain disabled under the owner's deferral. The app removes the account-type restriction; it cannot treat internet access as proof of verified identity. Registration can practice immediately and connect after confirmation. No production migration is needed for this UI/local-practice update.

The full 29-test emulator run passed, followed by 11 affected checks after final password/navigation changes. All 72 JVM and 24 Python tests pass; eight XML packs validate. The first popup screenshot capture selected two Compose roots; the helper was corrected to capture the actual menu, and the original width assertion plus all tests pass. No assertion was weakened. Lint has zero errors and 52 warnings (dependency updates, unused resources, icon fallback and existing style/preferences suggestions). Current screenshots are under `.local/visual-qa/account-modes`.

Installable version 1.0.3: `deliverables/BrainyBrawl-1.0.3.apk`, 30,248,642 bytes, version code 4, SHA-256 `1086eb172fcde30119fc97c4080a507af8eab43382aed07bba93aeb13c1c7e54`. Debug and optimized unsigned release assembly pass. The installable development-signed APK passes v2 signature, ZIP CRC, private-credential exclusion, both packaged scenes, all 192 exact puzzle tiles and four native ELF/ZIP 16 KB alignment checks. It was installed in place and relaunched on emulator-5554 with Mr.onyx still signed in; the new actual hosted player card and email/method panel were visually inspected. Physical-device installation remains for the owner's manual test.

## Version 1.0.4 profile customization

The player-card caption and gallery upload control are gone. Avatar and frame share a square canvas across the header/card/pickers. Level and Gold/Gems/Flames appear together as icon/value chips. Friends and the accepted-friend count live inside the card; Avatars and Frames are adjacent purple buttons. Each selector offers twelve slots, with native previews until the final artwork is placed in `assets/avatars/avatar_01.png`–`avatar_12.png` and `assets/frames/frame_01.png`–`frame_12.png`. Each folder includes an exact handoff README; rebuilding includes added PNGs automatically.

Account now groups Email plus edit icon, actual linked-platform details, unlinked-provider controls, Change password/Delete account, and an icon/sign-out action. Email changes follow Supabase confirmation and preserve the matching offline identity after verification. Provider linking uses the current account; deferred providers remain disabled. The old local-account explanation is removed from Profile. Appearance notices clear after three seconds.

Selected avatar JPEGs and appearance IDs synchronize through the existing private bucket/RPC and authenticated Auth API. Offline selections persist and follow the matching account when it connects. Requests pin the initiating session; failed/late writes cannot overwrite another local account. All IDs are constrained to the twelve packaged slots. No competitive values, premium ownership, RLS policies or schema migrations were changed. Legacy gallery data is preserved without a gallery UI. Navigation/action icons now use 22 native Lucide vectors from the Morphicons gallery, with source hashes and ISC/MIT notices.

Verification: 76 JVM tests and the full 36-test emulator suite passed; three profile tests were rerun after contrast corrections. Tests cover appearance storage/retry/restoration/adoption, account-switch isolation, square geometry, bounded JPEGs, malicious IDs, verified email cache changes, existing-account provider linking, selectors, transient feedback, deletion cancellation and Arabic layout. Lint has zero errors and 68 warnings, mainly unused resources/dependency updates/style suggestions. Final build, APK checks and the installed normal app are recorded below. See `PROFILE_CUSTOMIZATION_UPDATE.md` for the detailed handoff.

Final 1.0.4 artifact: `deliverables/BrainyBrawl-1.0.4.apk`, 30,338,838 bytes, version code 5, SHA-256 `2eb2b90703aef415598388e3a464655a35ca24b82e50db95db4aea733d799ed4`. The development-signed APK passes v2 signature, CRC, private-credential exclusion, art integrity and native/ZIP alignment checks. The complete final normal build passes 76 JVM tests, debug/optimized release assembly and lint (zero errors, 68 warnings). The 36-test full emulator run was followed by seven appearance/profile checks and three final dialog/profile checks. The PNG handoff validator confirms all 24 slots currently use native previews.

The normal app was installed in place with Mr.onyx preserved. An actual avatar Save succeeded through the app; a separate read-only database check confirmed explicit avatar 1/frame 1 metadata and a private 9,610-byte JPEG. No schema/RLS/provider settings were changed. The final navy picker was visually inspected and its first/last row labels have equal full height. The old save notice is absent after its three-second duration. The app remains open on Profile for manual testing.

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
- Installable normal debug APK: `deliverables/BrainyBrawl-1.0.4.apk` (also `app/build/outputs/apk/debug/app-debug.apk`). Unsigned engineering AAB: `app/build/outputs/bundle/release/app-release.aab`.

The requested local Git commit is recorded separately in the final response; nothing is pushed.
