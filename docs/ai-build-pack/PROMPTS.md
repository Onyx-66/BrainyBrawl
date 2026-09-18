# Brainy Brawl --- AI-Agent Build Prompts v3

## Mission

Build Brainy Brawl so the final Android app is functionally faithful to
the GDD/build pack **and visually converges on**:

`mockups/UI_MASTER_REFERENCE.png`

The mockup is a visual target, not a source for invented gameplay rules.

## Visual reference contract --- mandatory

The project includes a visual master reference at:
`mockups/UI_MASTER_REFERENCE.png`

### What the reference controls

Use the reference as the primary **visual target** for: - overall screen
composition; - information hierarchy; - button placement patterns; -
navigation placement; - card proportions; - spacing rhythm; - dark blue
panel treatment; - saturated accent colors; - rounded corners; - bold,
friendly typography; - icon treatment; - HUD placement; - player/team
cards; - timers/progress indicators; - modal/popup treatment; -
victory/defeat/reward presentation.

The reference targets a **Samsung Galaxy A56-style 1080 × 2340 px, 20:9
portrait frame**. Compose layouts responsively, but use that frame for
visual review and screenshot baselines.

### What the reference does NOT control

The reference is not allowed to override: - GDD gameplay rules; - exact
round counts/timers/scoring; - server-authoritative behavior; -
security/RLS requirements; - localization requirements; - accessibility
requirements; - unresolved product decisions.

The GDD-derived instruction files remain authoritative for behavior. If
the mockup and a written product rule conflict, implement the written
product rule and preserve the mockup's visual language where possible.

### Visual implementation rules

-   Do not replace the reference with a generic Material 3 look.
-   Material 3 is an implementation foundation only; Brainy Brawl's
    custom design tokens must drive the visible result.
-   Do not introduce arbitrary new colors per screen. Use semantic
    design tokens.
-   Do not invent new navigation patterns when an equivalent pattern
    exists in the reference.
-   Keep primary actions visually prominent, secondary actions distinct,
    and destructive actions red.
-   Keep gameplay controls thumb-reachable and visually stable across
    rounds.
-   Use the same component for the same semantic purpose across screens.
-   Use real assets where supplied; do not create fake logos, fake store
    products, or fake player data as production content.
-   Visual placeholders are acceptable in development only when clearly
    marked and replaceable by stable asset/content IDs.
-   Every screen should be reviewable at 1080 × 2340 without clipping,
    overlap, or unreadable text.
-   Test long French and Arabic strings; RTL must mirror layout without
    breaking the reference hierarchy.

### Reference screen inventory

The master board is a visual target for these families: 1. Splash /
launch 2. Login 3. Sign up 4. Home / Landing 5. Profile 6. Friends 7.
Store 8. Settings 9. Mode selection 10. Loadout selection 11. Lobby 12.
1v1 Question Round 13. 1v1 Image Guess 14. Duo 96-piece puzzle 15. Squad
Precision Tap 16. Squad Speed Sort 17. Theme selection/draft 18. Solo
Online search/game shell 19. Results / Victory / Defeat 20. Matchmaking
/ Countdown 21. Correct / Wrong answer states 22. Reaction overlay 23.
Reconnecting 24. Flame reward 25. Shared design-system components

When implementing a screen not explicitly pictured, infer only the
**visual language and component grammar**, not new product behavior.

## Mandatory global agent contract

Every agent MUST:

1.  Inspect the current repository before editing.
2.  Read every instruction file named by the prompt.
3.  Read `17_DECISION_REGISTER.md`.
4.  For UI work, inspect `mockups/UI_MASTER_REFERENCE.png` before
    coding.
5.  Treat written GDD-derived requirements as authoritative for
    behavior.
6.  Treat the mockup as authoritative for visual language/layout where
    behavior does not conflict.
7.  Never silently invent an unresolved product rule.
8.  If a required product decision is missing, mark it `OPEN_DECISION`,
    stop at that decision boundary, and do not guess.
9.  Make the smallest coherent change required.
10. Preserve existing architecture and working behavior.
11. Do not add libraries/services without a documented technical reason.
12. Never commit secrets or privileged Supabase credentials.
13. Never place a Supabase `service_role`/privileged key in Android
    code, resources, logs, Git, or APK.
14. Never trust client-provided competitive scores, winners, deadlines,
    Flame awards, balances, or leaderboard values.
15. Keep competitive authority on the server.
16. Keep domain logic independent of Compose.
17. Keep raw Supabase Realtime subscriptions out of Composables.
18. Use localization keys for all user-facing strings.
19. Preserve Light/Dark and RTL support.
20. Add loading, empty, error, retry, and reconnect states where
    applicable.
21. Add/update tests for business rules and failure cases.
22. Never weaken/delete tests to make a build pass.
23. Do not automatically deploy/publish or alter production data.
24. Do not make unrelated product/design changes.
25. After UI work, compare affected screens at 1080 × 2340 and fix
    avoidable visual drift.

## Required report after every prompt

Return: - Implemented - Files changed - Tests executed + results -
Build/lint result - Visual QA result (for UI work) - Database
migrations/functions changed - Security considerations - Known
limitations / `OPEN_DECISION`s - Exact next dependency

## Human approval gates

Stop for human approval before: - resolving an `OPEN_DECISION`; -
changing a confirmed GDD rule; - adding a paid third-party service; -
destructive shared/production DB changes; - release signing changes; -
production deployment/publication; - breaking public API changes.

------------------------------------------------------------------------

# Phase 0 --- Architecture and foundation

## Prompt 01 --- Establish the engineering contract

Read: - `00_README.md` - `01_ARCHITECTURE.md` -
`17_DECISION_REGISTER.md` - `18_MOCKUPS_REFERENCE.md`

Inspect the real repository.

Produce/update an implementation map covering: - module/package
boundaries; - Compose/UI/domain/data separation; - Supabase
responsibilities; - server-authoritative responsibilities; - Realtime
ownership; - offline/cache; - content/XLSX pipeline; - security; -
testing; - dependency order; - visual-reference integration.

Cross-check documents for contradictions. Preserve `OPEN_DECISION`s.

Do not implement gameplay or invent product rules.

**Acceptance:** architecture is documented, visual reference is
explicitly part of UI implementation, and unresolved product decisions
remain unresolved.

------------------------------------------------------------------------

## Prompt 02 --- Build the Android foundation

Read `02_PROJECT_FOUNDATION.md` and Prompt 01 output.

Implement only the Android foundation: - Kotlin/Compose; - approved
dependency setup; - package structure; - environment/config; - baseline
tests; - lint/static analysis; - CI; - safe Supabase client boundary if
required.

Do not implement feature screens, gameplay, store behavior, or
multiplayer.

**Acceptance:** debug build, tests, and lint pass; no secrets are
committed.

------------------------------------------------------------------------

## Prompt 03 --- Build the Brainy Brawl design system

Read: - `03_DESIGN_SYSTEM_UI.md` - `18_MOCKUPS_REFERENCE.md`

Implement the complete reusable design system.

The visual result must match the master reference: - deep navy
background; - dark blue panels/cards; - saturated purple/blue/cyan
accents; - green positive actions; - red destructive actions; -
yellow/gold rewards; - rounded cards/buttons; - bold friendly
typography; - compact mobile-game HUD; - consistent iconography; -
bottom navigation and top resource patterns.

Implement component states and a showcase screen.

Verify Light/Dark and RTL.

**Acceptance:** screenshots at 1080 × 2340 visibly follow the master
board's component grammar and no generic Material styling leaks through
as the dominant look.

------------------------------------------------------------------------

# Phase 1 --- Backend contract

## Prompt 04 --- Design Supabase schema

Read `16_SUPABASE_BACKEND_SECURITY.md`, `17_DECISION_REGISTER.md`, and
relevant mode docs.

Create normalized migrations for confirmed requirements: -
profiles/player IDs; - friends/blocks/reports; - rooms/lobbies; -
matches/phases; - submissions/score events; -
currencies/inventory/cosmetics; - content references; -
leaderboards/statistics; - audit/security fields.

Use constraints, foreign keys, indexes, and appropriate enums.

Do not invent unresolved mode schedules, IAP pricing, Battle Pass rules,
ads economy, or production licensing.

**Acceptance:** migrations apply to a fresh development DB and
unresolved decisions remain explicit.

------------------------------------------------------------------------

## Prompt 05 --- Implement RLS and authorization tests

Read `16_SUPABASE_BACKEND_SECURITY.md`.

Implement default-deny RLS and tests proving users cannot: - read/write
other users' private data; - submit for another player; - alter
authoritative score events; - award Flames; - alter
balances/inventory; - rewrite completed results.

**Acceptance:** unauthorized operations fail in automated tests.

------------------------------------------------------------------------

## Prompt 06 --- Implement authoritative match functions

Implement server-side functions for: - match start; - phase
transitions; - submission validation; - authoritative deadlines; -
scoring; - tiebreaks; - finalization; - Flame awards.

Require idempotency, replay protection, server timestamps, and
transactional updates.

**Acceptance:** duplicate/reordered requests cannot duplicate rewards or
rewrite results.

------------------------------------------------------------------------

## Prompt 07 --- Build content import validation

Read `13_CONTENT_PIPELINE_XLSX.md` and inspect
`Brainy_Brawl_Content.xlsx`.

Build validation/import tooling for: - duplicate IDs; - wrong answer
counts; - invalid correct-answer flags; - broken references; -
unsupported locales; - malformed placeholders; - invalid mini-game
configs; - unapproved production content.

Runtime code must not hard-code production questions/reactions/mini-game
content.

**Acceptance:** invalid fixtures fail with actionable diagnostics; valid
content imports.

------------------------------------------------------------------------

# Phase 2 --- App shell and social

## Prompt 08 --- Implement authentication

Read `04_AUTH_HOME_NAVIGATION.md`.

Implement: - email/password; - verification; - recovery/change
password; - logout; - session restoration; - Google OAuth; - Discord
OAuth; - secure Android callback/deep-link handling.

Use safe configuration boundaries.

**Acceptance:** auth lifecycle is robust and no privileged credential
reaches the client.

------------------------------------------------------------------------

## Prompt 09 --- Implement home/navigation to match the master board

Read `04_AUTH_HOME_NAVIGATION.md`, `03_DESIGN_SYSTEM_UI.md`, and
`18_MOCKUPS_REFERENCE.md`.

Build: - launch/loading; - landing/home; - navigation graph; -
placeholder destinations for Store, Profile, Friends, Start Game,
Settings.

Use the master board as the visual target for: - top resource area; -
cards; - CTA placement; - bottom navigation; - spacing; - color
semantics.

Do not invent new product sections.

**Acceptance:** 1080 × 2340 screenshot is visually consistent with the
master board.

------------------------------------------------------------------------

## Prompt 10 --- Implement profile/friends/safety

Read `05_PROFILE_FRIENDS_SAFETY.md`.

Implement: - profile; - permanent 8-digit Player ID; - friend
requests/search; - friend list; - block/report; - preset quick
reactions.

No free-text chat.

Match the reference's player cards, tabs, buttons, avatars, and
navigation grammar.

**Acceptance:** behavior and visual structure are correct; blocked
interactions are server-enforced.

------------------------------------------------------------------------

# Phase 3 --- Economy and multiplayer shell

## Prompt 11 --- Implement economy/store

Read `06_STORE_ECONOMY.md`.

Implement: - Gold; - Gems; - Flames; - inventory; - cosmetics; - Flame
Vault; - two-item loadout.

Match the reference store/card/currency visual language.

Do not invent IAP prices, Battle Pass, or ads rules.

**Acceptance:** client cannot modify balances or buy Flames.

------------------------------------------------------------------------

## Prompt 12 --- Implement rooms/lobbies

Read `07_LOBBY_MATCHMAKING_REALTIME.md`.

Implement: - private-by-default rooms; - create/join/leave; -
matchmaking toggle; - public 1v1 quick match; - friend invite; -
Duo/Squad team formation; - team names; - ready states; - presence; -
reconnect membership.

Match the reference lobby and loadout composition.

**Acceptance:** reconnect does not duplicate players.

------------------------------------------------------------------------

## Prompt 13 --- Implement typed Realtime event layer

Build a typed/versioned event layer: - compact payloads; - event
IDs/versioning; - duplicate handling; - unknown-event handling; -
authoritative snapshots; - repository-owned subscriptions; - Flow to
ViewModels.

Do not subscribe directly from Composables.

**Acceptance:** missed/duplicate events recover cleanly.

------------------------------------------------------------------------

# Phase 4 --- Game engine and mini-games

## Prompt 14 --- Implement platform-independent game engine

Read `08_GAME_ENGINE_SCORING.md`.

Implement: - match state machine; - phase IDs; - deadlines; -
submissions; - scoring; - transitions; - results; - tiebreak
abstraction; - reconnect/recovery.

No Compose dependency.

**Acceptance:** extensive domain tests pass without Android UI.

------------------------------------------------------------------------

## Prompt 15 --- Implement Question Round

Read `12_MINIGAMES.md` and relevant mode docs.

Implement the documented Question Round exactly: - content-driven; - 15
rounds for 1v1; - question-only period of 10 seconds; - 5 answer
options; - 20-second answer period; - first valid correct answer gets +1
in 1v1.

Use the reference for visual placement of question, answer cards, timer,
round counter, score/player HUD.

Do not hard-code questions.

**Acceptance:** server-authoritative online timing/scoring and
deterministic offline behavior.

------------------------------------------------------------------------

## Prompt 16 --- Implement Image Guess

Implement: - image + theme + specification; - 10 possible answers; -
hidden point values; - exactly 4 selections; - 30-second limit; -
reveal/explanation.

Match reference composition: image card, answer grid, round counter,
timer, selected state, reveal state.

**Acceptance:** impossible selection states are prevented; scoring uses
authoritative content.

------------------------------------------------------------------------

## Prompt 17 --- Implement Precision Tap

Implement rotating target/hot-zone interaction, successful hits, streak
bonus, and increasing difficulty.

Match the reference's circular target composition and bottom
timer/progress treatment.

Add deterministic test seeds.

**Acceptance:** domain tests cover hit/miss/streak/difficulty; input
remains responsive.

------------------------------------------------------------------------

## Prompt 18 --- Implement Collaborative Puzzle

Implement Duo's 96-piece conceptual 12×8 puzzle with
irregular/variable-sized blocks, left/right assignment, snapping,
correct placement, live teammate cursor/attempt visualization, compact
sync, and reconnect recovery.

Use the reference board as the visual target for puzzle HUD and tile
presentation, but do not turn it into uniform square tiles if that
conflicts with the written requirement.

**Acceptance:** simulated clients converge on the same authoritative
board state.

------------------------------------------------------------------------

## Prompt 19 --- Implement Word Scramble

Implement content-driven shuffled answers, configurable timer,
first-correct full score, later-correct reduced score, duplicate
protection, and server acceptance ordering.

Match the reference's compact round HUD and answer area.

**Acceptance:** device clocks cannot decide answer order.

------------------------------------------------------------------------

## Prompt 20 --- Implement Speed Sort

Implement stream generation, category buckets, drag/drop, correct +1,
wrong 0, timeout streak reset, and relay support.

Match the reference's category cards and bottom action/timer area.

Definitions remain in content data.

**Acceptance:** offline and online domain rules match.

------------------------------------------------------------------------

# Phase 5 --- Complete game modes

## Prompt 21 --- Build 1v1 end-to-end

Read `09_MODES_1V1_OFFLINE.md`.

Connect: - public quick match; - friend invite; - lobby; - Question
Round; - Image Guess; - results; - Flame reward.

Match the master board's 1v1 mode card, loadout, lobby, gameplay, and
results visual patterns.

**Acceptance:** two clients complete a full match with identical
authoritative results.

------------------------------------------------------------------------

## Prompt 22 --- Build Offline end-to-end

Implement offline Question and Image Guess with local approved
content/cache, no networking, no Flame, all-time best, and session
score-ratio logging.

Match online visual language while clearly communicating Offline.

**Acceptance:** networking can be disabled and the mode still works.

------------------------------------------------------------------------

## Prompt 23 --- Build Duo end-to-end

Read `10_MODES_DUO.md`.

Connect: - collaborative puzzle; - rank calculation; - theme draft; -
rank-dependent turns; - bottom-four elimination; - top-six Word Scramble
finale; - ranking; - Flame rewards.

Match the reference's team/player/puzzle/draft/result visual grammar.

**Acceptance:** all rank-dependent rules are server-enforced.

------------------------------------------------------------------------

## Prompt 24 --- Build Squad end-to-end

Read `11_MODES_SQUAD_SOLO.md`.

Connect: - Precision Tap relay; - Speed Sort relay; - 20-question theme
draft; - final ranking; - Flame reward.

Match the reference's game-mode cards and mini-game compositions.

**Acceptance:** team score is reproducible from authoritative events.

------------------------------------------------------------------------

## Prompt 25 --- Build Solo Online shell and decision gate

Build the Solo Online shell and reusable mini-game integration.

Do not invent the final Solo Online schedule if the GDD does not specify
it.

Mark the missing schedule `OPEN_DECISION`.

Match the reference's Solo search/game-shell visual language.

**Acceptance:** final schedule can be added through configuration
without rewriting the engine.

------------------------------------------------------------------------

# Phase 6 --- Product surfaces

## Prompt 26 --- Implement leaderboards

Implement Global/Friends-only and Weekly/All-Time filters for documented
modes.

Match reference leaderboard rows, tabs, avatars, rank styling, and
current-user emphasis.

Always show the user's own rank even outside the visible range.

**Acceptance:** backend-authoritative rankings and correct pagination.

------------------------------------------------------------------------

## Prompt 27 --- Implement settings/localization/RTL

Read `14_LOCALIZATION_RTL.md`.

Implement: - Music; - Sound; - Vibration; - Language; - Theme; - Contact
Us; - Terms; - Privacy Policy; - English/French/Arabic; - RTL.

Match the reference settings list and toggle treatment.

**Acceptance:** all strings localized and Arabic layout mirrors
correctly.

------------------------------------------------------------------------

## Prompt 28 --- Implement reactions

Implement documented reaction triggers and localized reaction strings.

Match the reference's short overlay style.

Reactions must not block gameplay or steal input.

**Acceptance:** overlays auto-dismiss in the documented range and remain
readable in all languages.

------------------------------------------------------------------------

# Phase 7 --- QA and release

## Prompt 29 --- Security audit

Audit Android, RLS, Edge Functions, Realtime, scoring, Flame awards,
balances, purchase verification, and account deletion.

Attempt unauthorized reads/writes, replay, duplicate rewards, score
tampering, and timestamp manipulation.

Fix valid vulnerabilities without weakening tests.

**Acceptance:** no client can award itself currency/Flames or alter
competitive results.

------------------------------------------------------------------------

## Prompt 30 --- Multiplayer reliability audit

Test: - disconnect/reconnect; - duplicate events; - delayed clients; -
backgrounding; - clock drift; - missed events; - transitions during
reconnect; - host departure where applicable.

Fix convergence/recovery issues without changing rules.

**Acceptance:** authoritative state remains recoverable.

------------------------------------------------------------------------

## Prompt 31 --- Performance and visual fidelity audit

Profile: - cold launch; - navigation; - Compose recomposition; - image
loading; - Realtime traffic; - puzzle rendering; - large-room
behavior; - memory.

For every major UI screen, capture 1080 × 2340 screenshots and compare
with the master reference.

Fix measurable performance and visual drift without changing product
rules.

**Acceptance:** before/after metrics are documented and major screens
visually converge on the reference.

------------------------------------------------------------------------

## Prompt 32 --- Content QA

Validate: - XLSX; - imports; - answer counts; - explanations; - image
metadata; - localization; - reactions; - asset references; -
approval/licensing metadata.

Do not invent licensing claims or sources.

**Acceptance:** release build references only approved content.

------------------------------------------------------------------------

## Prompt 33 --- Full regression

Run: - unit; - integration; - UI; - RLS/security; - multiplayer; -
offline; - localization/RTL; - purchase; - reconnect/recovery; - visual
regression where available.

Fix failures rather than weakening tests.

**Acceptance:** reproducible green release-candidate report, or explicit
release blockers.

------------------------------------------------------------------------

## Prompt 34 --- Google Play release preparation

Prepare: - release signing; - build variants; - privacy/data safety
inputs; - account deletion; - Terms/Privacy links; - IAP verification; -
crash reporting; - store assets; - age-rating declarations; - staged
rollout checklist.

Do not publish automatically.

**Acceptance:** release candidate is ready for human review.

------------------------------------------------------------------------

## Prompt 35 --- Final architecture, product, and visual review

Review the complete repository against: - GDD-derived requirements; -
all instruction files; - `17_DECISION_REGISTER.md`; -
`18_MOCKUPS_REFERENCE.md`; - `mockups/UI_MASTER_REFERENCE.png`; -
`Brainy_Brawl_Content.xlsx`.

Check for: - architectural drift; - generic Material UI replacing the
custom visual system; - incorrect button colors; - inconsistent button
placement; - inconsistent cards/navigation/HUD; - hard-coded content; -
hard-coded strings; - client-authoritative competitive logic; - insecure
RLS; - leaked secrets; - missing RTL; - missing loading/error/reconnect
states; - performance hazards; - incorrect gameplay rules.

Make only justified fixes. Do not silently resolve product decisions.

Produce: - implementation status; - visual-fidelity status; - remaining
defects; - remaining `OPEN_DECISION`s; - security status; - test
status; - release blockers.

**Acceptance:** the final app is both functionally faithful to the GDD
and visually consistent with the supplied master reference.
