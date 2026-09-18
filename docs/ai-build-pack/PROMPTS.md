# Brainy Brawl — Professional AI-Agent Build Prompts

## How to use
Run **one prompt at a time**. Do not paste the entire file into an agent.

Each prompt assumes the previous prompt's acceptance criteria passed. If an agent reports a blocker, stop and resolve it before continuing.

Recommended agent roles:
- **Architect agent:** architecture/schema/review.
- **Android agent:** Kotlin/Compose implementation.
- **Backend agent:** Supabase SQL/RLS/Edge Functions.
- **QA agent:** tests/security/performance.
- **Content agent:** XLSX validation/import/content QA.
- **UI agent:** design system/visual consistency.

Every agent must return:
1. files changed;
2. tests executed;
3. build/lint status;
4. database migrations/functions changed, if any;
5. known risks;
6. exact next dependency.

---

## Phase 0 — Product and architecture

### Prompt 01 — Establish the engineering contract
You are the lead software architect for Brainy Brawl. Read `00_README.md` and `01_ARCHITECTURE.md`, inspect the repository, and produce an implementation map for the existing codebase. Do not write gameplay code. Identify package boundaries, data flow, backend responsibilities, realtime responsibilities, offline responsibilities, and security boundaries. Explicitly flag any GDD `OPEN_DECISION` items. Deliver an architecture note and a dependency-ordered task list. Acceptance: no unexplained architectural conflicts remain.

### Prompt 02 — Create the Android project foundation
Read `02_PROJECT_FOUNDATION.md`. Inspect the repository first. Implement only the Android/Kotlin/Compose foundation, dependency configuration, environment handling, package structure, tests, lint, and CI. Do not implement auth, gameplay, store, or networking behavior beyond minimal infrastructure. Run the build and tests. Acceptance: clean debug build + tests + lint.

### Prompt 03 — Build the Compose design system
Read `03_DESIGN_SYSTEM_UI.md` and inspect the two supplied UI references. Implement tokens and reusable components only. Create a component showcase screen. Support light/dark themes and a basic RTL demonstration. Do not build product screens. Acceptance: showcase renders all components without duplicated theme-specific code.

---

## Phase 1 — Backend contract

### Prompt 04 — Design Supabase schema
You are the backend architect. Read `16_SUPABASE_BACKEND_SECURITY.md`. Create normalized PostgreSQL tables, foreign keys, constraints, indexes, enums, and migrations. Preserve stable content IDs. Do not build Android screens. Acceptance: migrations apply cleanly to a fresh database.

### Prompt 05 — Implement RLS and authorization tests
Implement the RLS rules from `16_SUPABASE_BACKEND_SECURITY.md`. Create test users/fixtures and prove unauthorized access is denied. Pay special attention to profiles, friendships, blocks, rooms, matches, submissions, score events, currencies, inventory, and leaderboards. Acceptance: security tests demonstrate default-deny and explicit allowed paths.

### Prompt 06 — Implement authoritative match functions
Implement server-side functions for match start, phase transitions, submission validation, scoring, tiebreaks, and Flame awards. Never trust client timestamps or client scores. Make operations idempotent. Acceptance: repeated requests cannot duplicate points or rewards.

### Prompt 07 — Build content import validation
Read `13_CONTENT_PIPELINE_XLSX.md` and inspect `Brainy_Brawl_Content.xlsx`. Build a validator/importer that rejects duplicate IDs, invalid answer counts, invalid correct-answer flags, invalid references, unsupported locales, and unapproved content. Acceptance: bad fixture files fail with actionable errors; valid fixture imports successfully.

---

## Phase 2 — App shell and social

### Prompt 08 — Implement auth and session lifecycle
Read `04_AUTH_HOME_NAVIGATION.md`. Implement Supabase auth, email/password, verification, password change/recovery, logout, Google, and Discord connection flows using secure patterns. Do not implement social/game screens yet. Acceptance: auth survives process death/restart and handles failures gracefully.

### Prompt 09 — Implement home/navigation
Build the launch/loading flow, landing page, navigation graph, and placeholder destinations for Store, Profile, Friends, Start Game, and Settings. Use the design system and localization keys. Acceptance: navigation is stable, back behavior is correct, and no user-facing string is hard-coded.

### Prompt 10 — Implement profile/friends/safety
Read `05_PROFILE_FRIENDS_SAFETY.md`. Implement profile, permanent Player ID display, friend search/requests, block/report, and preset quick-chat/reaction data flows. Do not add open text chat. Acceptance: blocked users disappear from all specified surfaces and cannot interact through invites/reactions.

---

## Phase 3 — Economy and multiplayer shell

### Prompt 11 — Implement currencies/inventory/store
Read `06_STORE_ECONOMY.md`. Implement Gold, Gems, Flames, inventory, cosmetics, Flame Vault, and two-item match loadout. Use server-authoritative mutations. Implement IAP only after the virtual economy is stable. Acceptance: balances cannot be modified by arbitrary client requests.

### Prompt 12 — Implement rooms/lobbies
Read `07_LOBBY_MATCHMAKING_REALTIME.md`. Implement room creation/join/leave, private default, matchmaking toggle, friend invites, team formation, team naming, ready states, and presence. Acceptance: reconnecting clients recover correct membership without duplicating players.

### Prompt 13 — Implement realtime event layer
Build a typed realtime event protocol for lobby and gameplay. Minimize payloads. Add event versioning and unknown-event handling. Do not scatter raw Supabase Realtime subscriptions across Composables. Acceptance: one repository layer owns subscriptions and exposes Flow to ViewModels.

---

## Phase 4 — Game engine and content

### Prompt 14 — Implement domain game engine
Read `08_GAME_ENGINE_SCORING.md`. Build the platform-independent state machine, timers/deadlines, scoring, submissions, phase transitions, results, and tiebreak abstractions. Write exhaustive unit tests. Acceptance: engine tests pass without Android UI.

### Prompt 15 — Implement Question Round
Implement the content-driven Question Round exactly as specified. Load by content ID. Render question, reveal answer choices after the 10-second question-only period, enforce 20-second timer, and submit only one valid answer. Acceptance: timing and scoring are server-authoritative online and deterministic offline.

### Prompt 16 — Implement Image Guess Round
Implement the Image Guess format: image/theme/specification, 10 options with hidden point values, exactly 4 selections, 30 seconds, and reveal explanation. Use the Image Viewer component. Acceptance: impossible selection states are prevented and score is derived from authoritative content.

### Prompt 17 — Implement Precision Tap
Implement the ring/marker/hot-zone game. Keep rendering and input responsive while scoring remains deterministic and authoritative. Implement streak bonuses and increasing difficulty. Add deterministic test seeds. Acceptance: unit tests cover hit/miss/streak/difficulty and UI tests cover rapid tapping.

### Prompt 18 — Implement Collaborative Puzzle
Implement the 96-piece Duo puzzle with irregular variable-sized blocks, left/right player assignment, snap-to-slot interaction, correct-placement scoring, and live teammate cursor/attempt visualization. Synchronize compact events and recover from reconnects. Acceptance: two simulated clients converge on the same board state.

### Prompt 19 — Implement Word Scramble
Implement shuffled trivia answers, 10–15 second timers, first-correct full score, later-correct reduced score, duplicate-submission protection, and content-driven configuration. Acceptance: ordering is determined by server acceptance time, not device clock.

### Prompt 20 — Implement Speed Sort
Implement stream generation, category buckets, drag/drop, +1 correct, 0 wrong, timeout streak reset, and shared relay support. Keep the item/category definitions in content data. Acceptance: offline and online modes use the same domain rules.

---

## Phase 5 — Game modes

### Prompt 21 — Build 1v1 end-to-end
Read `09_MODES_1V1_OFFLINE.md`. Connect lobby -> match -> Phase 1 -> Phase 2 -> results -> Flame reward. Include public quick match and direct friend invite. Acceptance: two clients can complete a full match with identical authoritative results.

### Prompt 22 — Build Offline end-to-end
Implement Offline Question and Image Guess paths with local content/cache, no networking, no Flame, all-time best, and session score-ratio logging. Acceptance: the mode works with networking disabled.

### Prompt 23 — Build Duo end-to-end
Read `10_MODES_DUO.md`. Implement puzzle -> theme draft -> elimination -> Word Scramble finale -> final ranking -> Flame rewards. Acceptance: all specified rank-dependent turn rules are enforced by the server.

### Prompt 24 — Build Squad end-to-end
Read `11_MODES_SQUAD_SOLO.md`. Implement Precision Tap relay -> Speed Sort relay -> 20-question theme draft -> final ranking -> Flame reward. Acceptance: each teammate gets the required contribution and the team score is reproducible from score events.

### Prompt 25 — Build Solo Online shell and decision gate
Implement Solo Online room flow and reusable mini-game integration without inventing an unspecified final match schedule. Mark missing rules as `OPEN_DECISION`. Acceptance: architecture supports adding the final schedule through configuration rather than a rewrite.

---

## Phase 6 — Product surfaces

### Prompt 26 — Implement leaderboards
Implement Global/Friends-only and Weekly/All-Time filters for all specified modes. Always display the current player's rank even outside the visible range. Acceptance: rank calculations are backend-authoritative and pagination does not hide the user's own rank.

### Prompt 27 — Implement settings/localization
Implement Music, Sound, Vibration, Language, Theme, Contact Us, Terms, and Privacy Policy. Add English/French/Arabic and RTL. Acceptance: every user-facing string is localized and Arabic layouts are manually reviewed.

### Prompt 28 — Implement reactions
Implement post-event reaction popups from the GDD's trigger categories. Use localization keys and avoid blocking gameplay. Acceptance: popups auto-dismiss around the specified duration and never steal gameplay input.

---

## Phase 7 — QA and release

### Prompt 29 — Security audit
Act as a hostile QA/security engineer. Inspect client code, Supabase RLS, Edge Functions, Realtime channels, purchase flows, score submission, Flame awards, and account deletion. Attempt unauthorized reads/writes and replay attacks. Deliver a prioritized vulnerability report and fixes. Acceptance: no client can award itself currency or alter competitive results.

### Prompt 30 — Multiplayer reliability audit
Simulate disconnect, reconnect, duplicate packets/events, app backgrounding, delayed clients, host departure, and server/client clock drift. Fix state convergence problems. Acceptance: match state remains authoritative and recoverable.

### Prompt 31 — Performance audit
Profile cold launch, navigation, image loading, Compose recomposition, realtime traffic, puzzle rendering, and 40-player room simulations. Fix measurable bottlenecks without changing game rules. Acceptance: documented before/after metrics and no regression in gameplay correctness.

### Prompt 32 — Content QA
Validate the XLSX, imported database, all answer counts, explanations, image metadata, localization keys, reaction strings, and asset references. Remove/flag questionable or unlicensed media/content. Acceptance: production build references only approved content.

### Prompt 33 — Full regression
Run unit, integration, UI, RLS, multiplayer, offline, localization, purchase, and reconnect suites. Fix failures rather than weakening tests. Acceptance: release candidate has a reproducible green test report.

### Prompt 34 — Google Play release preparation
Prepare release signing, build configuration, privacy/data safety inputs, account deletion, terms/privacy links, IAP verification, crash reporting, store assets, age-rating declarations, and staged release checklist. Do not publish automatically. Acceptance: release candidate is ready for human approval.

### Prompt 35 — Final architecture/code review
You are the principal engineer. Review the complete repository against all instruction files. Identify dead code, duplicated logic, hidden client authority, hard-coded content, missing localization, unsafe RLS, performance hazards, and architectural drift. Make only justified fixes. Deliver a final engineering report and list of remaining explicit product decisions.
