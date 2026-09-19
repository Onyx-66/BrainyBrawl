# 15 --- Leaderboards, Analytics, Testing, Performance & Release

## Objective

Finish the production-quality layer after the core game is stable.

## Leaderboards

Per mode: - 1v1 - Solo - Duo - Squad - Offline

Filters: - Global / Friends-only - Weekly / All-Time

Always show the player's own rank even if outside the visible top range.

Core metric: - 1v1: win % - Solo: highest score - Duo: highest score,
win %, Top-5% rate - Squad: highest score, win %, Top-3% rate - Offline:
highest score, score ratio

Do not calculate competitive rankings solely from client-submitted
values.

## Analytics

Instrument only useful product events, such as: - app launch; - auth
success/failure; - mode selection; - matchmaking join/leave; - match
start/end; - phase completion; - disconnect/reconnect; - purchase
attempt/success/failure; - content errors; - crash/performance
diagnostics.

Do not log passwords, auth tokens, or unnecessary personal data.

## Performance

Target: - smooth Compose rendering; - bounded realtime event rate; -
efficient image loading/caching; - avoid loading a full content database
into memory; - cancel timers/flows when screens are gone; - avoid
recomposition hotspots; - profile with realistic 40-player room event
loads.

## Testing

-   unit tests for domain rules;
-   integration tests for Supabase;
-   UI tests for navigation and core interactions;
-   reconnect tests;
-   localization/RTL tests;
-   purchase verification tests;
-   RLS/security tests;
-   offline tests.

## Release

Before Google Play release: - production environment separation; -
signing configuration; - privacy/data-safety review; - account deletion
flow; - terms/privacy links; - IAP verification; - crash reporting; -
store assets; - age-rating declarations; - closed testing track; -
release checklist.

## Open items

The GDD leaves exact IAP tiers, ads/Battle Pass, content
sourcing/moderation pipeline, anti-cheat details, disconnect handling,
and final store marketing copy open. Track them as explicit decisions
before launch.

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
