# 02 --- Android Studio Project Foundation

## Objective

Create the native Android foundation and build pipeline before
implementing game features.

## Tasks

1.  Create the Android Studio project using Kotlin.
2.  Configure a stable minimum/target SDK strategy appropriate for the
    current Google Play requirements.
3.  Enable Jetpack Compose and Material 3.
4.  Configure dependency versions centrally.
5.  Add Navigation Compose, Coroutines/Flow, Supabase client libraries,
    serialization, Room, and test dependencies.
6.  Create build variants for `debug`, `staging`, and `release` if
    practical.
7.  Add environment configuration without committing secrets.
8.  Create the package boundaries defined in `01_ARCHITECTURE.md`.
9.  Add a root-level error/result convention.
10. Add a simple launch screen and placeholder navigation graph.
11. Add baseline unit and instrumentation tests.
12. Add static analysis/lint configuration.
13. Add CI that compiles and runs tests on every change.

## Constraints

-   No Firebase.
-   No Unity.
-   No Supabase service-role key in client code.
-   No hard-coded production URLs or credentials.
-   Do not implement gameplay yet.

## Deliverables

-   Compiling Android project.
-   Dependency manifest/version catalog.
-   Environment/config strategy.
-   Initial package structure.
-   CI/build instructions.
-   Smoke test proving the app launches.

## Verification

Run debug build, unit tests, lint, and an emulator launch test.

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
