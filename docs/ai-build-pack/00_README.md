# Brainy Brawl --- AI Build Pack

## Purpose

This pack converts the Brainy Brawl GDD v0.4 into a modular
implementation plan for a native Android app built with **Android
Studio + Kotlin + Jetpack Compose + Supabase**.

The source GDD defines the game as an Android/Google Play real-time
multiplayer trivia and party game with 1v1, Solo Online, Duo, Squad, and
Offline modes, plus cosmetics, currencies, friends, leaderboards,
quick-chat, localization, and multiple mini-games.

## Important architecture decision

The GDD recommends Unity + Photon + Firebase in its technology section.
This pack intentionally **does not follow that recommendation**, because
the requested implementation target is Android Studio + Supabase.

Proposed native stack: - Kotlin - Jetpack Compose - Material 3, with a
custom Brainy Brawl design system - Kotlin Coroutines + Flow -
Navigation Compose - Supabase Auth - Supabase Postgres - Supabase
Realtime - Supabase Storage - Supabase Edge Functions where server-side
validation is required - Google Sign-In and Discord OAuth through
Supabase-supported authentication flows - Room for local/offline
content/cache/state where useful - Google Play Billing for Gems/IAP -
WorkManager for non-real-time background jobs - Instrumented/unit
tests + backend SQL/RLS tests

## How to use these files

Do not give all files to one coding agent at once.

Recommended order: 1. Read `01_ARCHITECTURE.md`. 2. Execute
`02_PROJECT_FOUNDATION.md`. 3. Build and verify one vertical slice at a
time. 4. Give the relevant instruction file to the coding agent only
when its dependencies are complete. 5. Use `PROMPTS.md` as the
orchestration sequence. Each prompt is deliberately scoped to one
professional deliverable. 6. Use `Brainy_Brawl_Content.xlsx` as the
source of game content. Runtime code should consume IDs and records
rather than hard-code questions, answers, reactions, puzzle definitions,
or mini-game content.

## Source-of-truth hierarchy

1.  This GDD-derived pack for product rules.
2.  Supabase schema/API contract for backend behavior.
3.  Kotlin domain models for client contracts.
4.  XLSX content database for questions/game content.
5.  UI references supplied with the project for visual direction.

When the GDD leaves an item open, implementation agents must mark it as
`OPEN_DECISION` and avoid silently inventing a product rule.

## AI-agent operating rule

Every agent must: - inspect existing code before editing; - preserve
working behavior; - make the smallest coherent change; - avoid replacing
architecture without an explicit reason; - compile/test its change; -
report changed files, tests run, known limitations, and follow-up
dependencies; - never hard-code production secrets; - never trust
client-provided scores or timers for competitive results.

## Definition of Done

A feature is not complete merely because the screen renders. It must
have: - loading/empty/error states; - accessibility labels; -
localization keys; - dark/light theme behavior; - state restoration
where relevant; - offline behavior where relevant; - analytics hooks
where appropriate; - tests for business rules; - server-side validation
for competitive actions; - no leaked secrets; - no hard-coded content
that belongs in the XLSX/database.

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
