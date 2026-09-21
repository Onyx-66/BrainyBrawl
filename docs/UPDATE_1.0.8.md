# BrainyBrawl 1.0.8

## Changes

- Home contains the start-game hero, Friends and Leaderboards. Games uses centered mode names, square mode cards and separate Offline/Bluetooth cards. Bluetooth opens four themed mode choices.
- The header uses the shared currency counter, matching profile/settings backgrounds and the supplied icons. Only the active bottom-navigation label is visible.
- Profile keeps an inline username Edit action, stable copyable user ID, a collapsed Account section and full-width password/sign-out actions. Removed delete-account and played/wins summary boxes. Achievements opens six server-verified, one-time reward missions.
- Settings adds keep-screen-on, puzzle guide visibility and a Bluetooth settings shortcut. Store has Currency/Cosmetics tabs, five editable JSON catalogs and supplied cosmetic previews.
- Added 250 questions in three languages, 20 image scenes in three languages and 49 puzzles in three languages. Typed answers accept cross-language aliases, reasonable spelling mistakes and written/digit numbers while rejecting distinct numeric answers.
- Image rounds sample exactly four correct and six incorrect options from full answer pools. Online selections are fixed per round and hidden-answer IDs cannot be submitted.
- Joined placed puzzle pieces lose internal grid lines and share a thin white boundary. The optional guide is 18% opaque. Full-image WebP crops replace separate tile assets for the new pack.
- Preserved 69 PNG originals and created WebP runtime copies: 163,769,839 source bytes to 24,054,246 runtime bytes. Source PNGs and historical tile folders are excluded from the APK. UI icons now live in `assets/icons`.

## Validation

- 85 JVM tests passed.
- 43 Android emulator instrumentation tests passed, covering navigation, Arabic profile/mode layouts, offline content, WebP puzzle rendering and store flows.
- 25 Python content/asset tests passed.
- 44 isolated PostgreSQL/PGlite checks passed, including typo matching, numeric false positives, mission reward idempotency/authorization and locked image answer pools.
- Android lint completed with zero errors; existing warnings remain.
- Hosted authentication, profile, store, leaderboard, six-mission snapshot and access restrictions passed after applying migrations 031–033.

## Operational notes

See `CONTENT_AUTHORING_GUIDE.md`, `BLUETOOTH_PLAY.md` and `assets/store/README.md` for editing and play instructions. The content publisher previews missing IDs, preserves retired history, refreshes aliases and refuses active matches before atomic publication.

Currency pack prices are draft display data; Google Play checkout is disabled until billing and server receipt verification are configured. Free supplied cosmetics remain usable. Missions award server-confirmed online progress; local practice/Bluetooth sessions cannot grant spendable rewards.

Bluetooth permission prompts are implemented, but Android requires user consent at runtime. Physical two-/four-/eight-phone Bluetooth sessions still need device testing; the emulator cannot validate radio links. Bluetooth currently supports friendly quiz matches, not the internet mode's full mixed-minigame sequence.

The delivery APK is a locally signed development build, not a Play Store release signing artifact.
