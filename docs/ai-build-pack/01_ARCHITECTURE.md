# 01 --- Architecture & Engineering Contract

## Objective

Create a maintainable native Android architecture for Brainy Brawl
without turning the project into a monolith.

## Target stack

-   Kotlin
-   Jetpack Compose
-   Material 3
-   MVVM + clean feature boundaries
-   Coroutines/Flow
-   Navigation Compose
-   Supabase Auth/Postgres/Realtime/Storage
-   Edge Functions for privileged validation
-   Room for local cache/offline content
-   Google Play Billing for Gems
-   WorkManager for deferred sync

## Suggested module structure

Start as one application module if needed for speed, but enforce package
boundaries that can later become Gradle modules:

`core/` - design system - navigation - networking - database/cache -
localization - analytics - common result/error types - security
utilities

`feature/auth` `feature/home` `feature/profile` `feature/friends`
`feature/store` `feature/leaderboards` `feature/lobby` `feature/game`
`feature/offline` `feature/settings`

`game/` - engine/state machine - scoring - timers - content loading -
1v1 - solo - duo - squad - mini-games

## Domain principles

The UI observes immutable UI state. ViewModels coordinate use cases.
Repositories hide Supabase/Room implementation details. Domain rules
must be testable without Android UI.

Do not put scoring rules inside Composables.

## Match state machine

Represent a match with explicit states, for example:
`LOBBY -> COUNTDOWN -> PHASE_ACTIVE -> PHASE_RESULTS -> NEXT_PHASE -> MATCH_RESULTS -> CLOSED`

Each phase must have: - phase ID - start timestamp - authoritative
deadline - content IDs - eligible players/teams - submitted actions -
scoring result - phase status

## Server authority

For competitive modes, the server must be authoritative for: - match
membership; - phase transitions; - deadlines; - answer acceptance
time; - scoring; - winners; - Flame awards; - leaderboard writes.

The client may animate a predicted result, but the server result wins.

## Data flow

`Compose UI -> ViewModel -> UseCase -> Repository -> Supabase/Room`

Realtime:
`Supabase Realtime -> Repository -> Flow -> ViewModel -> Compose`

## Required quality gates

Before moving to the next feature: - `./gradlew test` -
`./gradlew lint` - debug APK builds successfully - no plaintext Supabase
service-role key in the APK - RLS policies exist for all user-owned
tables - loading/error states are covered - all user-facing strings use
localization keys

## Mandatory visual reference

Use `mockups/UI_MASTER_REFERENCE.png` as the visual target for the
finished Brainy Brawl UI.

Reference frame: Samsung Galaxy A56 style, 1080 × 2340 px, 20:9
portrait.

Match the reference's: - deep navy background and dark blue surfaces; -
saturated purple/blue/cyan accents; - green positive/ready/correct
actions; - red destructive/wrong/cancel actions; - yellow/gold reward
and Flame emphasis; - rounded cards/buttons; - bold friendly
typography; - top resource area; - bottom navigation patterns; -
player/team cards; - gameplay HUD; - timers/progress; - results/reward
states; - compact mobile-game spacing and hierarchy.

The mockup is a visual reference, not permission to invent gameplay
rules or copy illustrative names, prices, questions, scores, or other
sample values into production.

If written GDD requirements conflict with the mockup, written
product/gameplay behavior wins; preserve the mockup's visual language
around that behavior.

For UI work, capture/check major screens at 1080 × 2340 and correct
accidental visual drift. Do not let generic Material 3 styling replace
the Brainy Brawl visual identity.

## Codex single-session rule

This documentation is designed for one Codex session. `PROMPTS.md` is an
ordered implementation plan, not a set of separate agent handoffs.
Execute phases continuously unless a genuine human approval gate is
reached.

## XML content rule

Runtime/source content belongs in individual XML files under the
repository-root `content/` directory, one file per game/mini-game type.
The reference workbook is not a runtime dependency.
