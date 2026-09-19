# PROMPTS --- Brainy Brawl Complete One-Session Codex Build

This is an ordered implementation plan for **one Codex session**. Codex
must execute all phases continuously against the supplied current
Android project root.

## Global rules

-   Inspect the actual repository first; never assume it is a clean
    template.
-   Read all instruction `.md` files in `docs/ai-build-pack/` before
    broad implementation.
-   Read `17_DECISION_REGISTER.md`.
-   Inspect `mockups/UI_MASTER_REFERENCE.png` before UI work.
-   GDD-derived written product/gameplay requirements are authoritative.
-   The mockup is authoritative for visual language/layout where it does
    not conflict with written behavior.
-   Never silently invent unresolved product rules; record
    `OPEN_DECISION`.
-   Kotlin + Jetpack Compose.
-   Supabase backend.
-   Domain/game rules independent of Compose.
-   Realtime subscriptions owned by data/repository layer, not
    Composables.
-   Server-authoritative competitive state, scoring, deadlines, winners,
    Flames, balances, and leaderboard writes.
-   Never expose Supabase service-role/privileged credentials.
-   Never commit secrets.
-   No free-text chat at launch.
-   All user-facing strings use localization.
-   Preserve Light/Dark and Arabic RTL.
-   Test loading/empty/error/reconnect states.
-   Never weaken/delete tests to make a build green.
-   Do not publish/deploy production.
-   Do not make unrelated rewrites.
-   Runtime/source content is XML, one file per game/mini-game type.
-   No XLSX parsing in Android runtime.
-   Do not create a giant all-games XML.
-   Verify major UI screens at 1080 × 2340 / 20:9 against the master
    board.

## Required content files

``` text
content/
  question_round.xml
  image_guess.xml
  collaborative_puzzle.xml
  precision_tap.xml
  roll_the_dice.xml
  word_scramble.xml
  speed_sort.xml
  reactions.xml
```

## Ordered phases

### 01 --- Repository/architecture audit

Read all docs, inspect the current project, reconcile actual code with
the architecture and decision register. Do not invent product rules.

### 02 --- Android foundation

Implement/repair Kotlin/Compose foundation, dependencies, package/module
boundaries, environment config, tests, lint, and CI as appropriate.

### 03 --- Design system

Implement the visual system to converge on the master board: navy
surfaces, saturated accents, semantic green/red/gold actions, rounded
cards/buttons, bold friendly typography, top resource bar, bottom
navigation, player cards, timers, progress, gameplay controls, dialogs,
result states.

### 04 --- Supabase schema

Create versioned migrations for confirmed profiles, social,
rooms/lobbies, matches/phases, submissions/events, economy,
inventory/cosmetics, content references, leaderboards, and
audit/security structures.

### 05 --- RLS/security

Default-deny RLS and tests. Prevent unauthorized access, score/result
manipulation, reward/Flame manipulation, balance manipulation, and
rewriting completed results.

### 06 --- Authoritative server functions

Implement confirmed match start, phase transitions, deadlines,
submission validation, scoring, tiebreaks, finalization, and Flame
awards with idempotency/replay protection.

### 07 --- XML content pipeline

Implement XML validation/import/repository. Convert useful workbook seed
data into the separate XML files while preserving stable IDs. No Excel
runtime dependency.

### 08 --- Authentication

Email/password, verification, recovery, session restore, Google/Discord
OAuth, secure Android callbacks/deep links.

### 09 --- Home/navigation

Launch, auth transitions, Home/Landing, Start Game, Modes, Store,
Friends, Profile, Settings, navigation. Match master-board visual
hierarchy and placement.

### 10 --- Profile/friends/safety

Profile, permanent 8-digit Player ID, friends/requests, block/report,
preset quick reactions. No free-text chat.

### 11 --- Economy/store

Gold, Gems, Flames, inventory, cosmetics, Flame Vault, two-item loadout.
Client cannot mint Flames/balances. Do not invent unresolved IAP/Battle
Pass/ad rules.

### 12 --- Rooms/lobbies

Private-by-default rooms, join/leave, matchmaking toggle, public 1v1
quick match, friend invites, Duo/Squad teams, names, ready state,
presence, reconnect membership. Match master-board lobby/loadout
visuals.

### 13 --- Typed Realtime

Versioned typed events, authoritative snapshots, duplicate handling,
unknown-event handling, repository-owned subscriptions, ViewModel state,
reconnect recovery.

### 14 --- Game engine

Platform-independent match/phase state machine, deadlines, submissions,
scoring, transitions, finalization, recovery.

### 15 --- Question Round

Implement documented 1v1 rules: 15 rounds, question-only 10s, five
options, 20s answer window, first valid correct +1. Load content from
XML.

### 16 --- Image Guess

Image/theme/specification, 10 answer choices, hidden point values,
exactly 4 selections, 30s, reveal/explanation. Match master-board
composition.

### 17 --- Precision Tap

Rotating target/hot-zone, hit scoring, streak bonus, increasing
difficulty, deterministic tests, reference visual treatment.

### 18 --- Collaborative Puzzle

Duo 96-piece/12×8 conceptual puzzle, irregular/variable blocks,
left/right assignments, snapping/correct placement, live teammate
cursor/attempt visualization, compact synchronization, reconnect
recovery.

### 19 --- Word Scramble

Content-driven scramble, timer, accepted-answer ordering, duplicate
protection, documented scoring.

### 20 --- Speed Sort

Item stream, category buckets, drag/drop, correct +1, wrong 0, timeout
streak reset, relay support.

### 21 --- 1v1 end-to-end

Public quick match/friend invite, lobby, Question Round, Image Guess,
results, Flame reward.

### 22 --- Offline

Offline Question Round and Image Guess using local XML, no networking,
no Flame, all-time best/session score-ratio logging.

### 23 --- Duo

Puzzle, rank calculation, theme draft, rank-dependent turns, bottom-four
elimination, top-six Word Scramble finale, ranking, Flame rewards.

### 24 --- Squad

Precision Tap relay, Speed Sort relay, 20-question theme draft, ranking,
Flame reward.

### 25 --- Solo Online

Build the documented shell/reusable integration. If the final Solo
schedule is unspecified, record `OPEN_DECISION`; do not invent it.

### 26 --- Leaderboards

Global/Friends-only and Weekly/All-Time views for documented modes, with
current-user rank visibility.

### 27 --- Settings/localization/RTL

Documented settings, English/French/Arabic, RTL, theme/audio/vibration,
contact/legal destinations.

### 28 --- Reactions

Localized preset reactions/overlays without blocking gameplay.

### 29 --- Security audit

Audit Android, RLS, functions, Realtime, scoring, Flames, balances,
purchase verification, account deletion.

### 30 --- Multiplayer reliability

Disconnect/reconnect, duplicate events, delays, backgrounding, clock
drift, missed events, transition recovery.

### 31 --- Performance + visual fidelity

Profile launch/navigation/recomposition/image loading/Realtime/puzzle
rendering/memory. Capture major screens at 1080 × 2340 and compare to
the master board.

### 32 --- Content QA

Validate every XML file, IDs, counts, references, localization, asset
references, schema versions, and approval metadata.

### 33 --- Full regression

Unit, integration, UI, security/RLS, multiplayer, offline,
localization/RTL, reconnect, and content tests.

### 34 --- Release preparation

Prepare release configuration, privacy/data-safety inputs, account
deletion, legal links, configured purchase verification, crash
reporting, store assets, age-rating inputs, staged rollout checklist. Do
not publish.

### 35 --- Final review

Review complete implementation against all docs, decision register, XML
contract, and master UI board. Fix justified defects without silently
changing product rules.

## End-of-session artifact

Create `CHANGED_FILES_MANIFEST.md`.

Then create:

`BrainyBrawl_CHANGED_FILES.zip`

The ZIP must contain **only added/modified files relative to the
starting repository**, plus the manifest, preserving project-relative
paths.

Do not include: - unchanged files; - `.git/`; - `.gradle/`; - build
outputs; - IDE caches; - `local.properties`; - keystores/signing keys; -
secrets; - machine-specific files.

List deleted files in the manifest so the user can reconcile deletions.

## Final report

State: - implementation summary; - build/test/lint status; - Supabase
changes; - XML content files; - UI/visual QA status; - security
status; - remaining `OPEN_DECISION`s/limitations; - exact ZIP path.
