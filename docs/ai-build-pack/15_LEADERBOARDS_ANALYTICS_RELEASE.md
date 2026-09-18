# 15 — Leaderboards, Analytics, Testing, Performance & Release

## Objective
Finish the production-quality layer after the core game is stable.

## Leaderboards
Per mode:
- 1v1
- Solo
- Duo
- Squad
- Offline

Filters:
- Global / Friends-only
- Weekly / All-Time

Always show the player's own rank even if outside the visible top range.

Core metric:
- 1v1: win %
- Solo: highest score
- Duo: highest score, win %, Top-5% rate
- Squad: highest score, win %, Top-3% rate
- Offline: highest score, score ratio

Do not calculate competitive rankings solely from client-submitted values.

## Analytics
Instrument only useful product events, such as:
- app launch;
- auth success/failure;
- mode selection;
- matchmaking join/leave;
- match start/end;
- phase completion;
- disconnect/reconnect;
- purchase attempt/success/failure;
- content errors;
- crash/performance diagnostics.

Do not log passwords, auth tokens, or unnecessary personal data.

## Performance
Target:
- smooth Compose rendering;
- bounded realtime event rate;
- efficient image loading/caching;
- avoid loading a full content database into memory;
- cancel timers/flows when screens are gone;
- avoid recomposition hotspots;
- profile with realistic 40-player room event loads.

## Testing
- unit tests for domain rules;
- integration tests for Supabase;
- UI tests for navigation and core interactions;
- reconnect tests;
- localization/RTL tests;
- purchase verification tests;
- RLS/security tests;
- offline tests.

## Release
Before Google Play release:
- production environment separation;
- signing configuration;
- privacy/data-safety review;
- account deletion flow;
- terms/privacy links;
- IAP verification;
- crash reporting;
- store assets;
- age-rating declarations;
- closed testing track;
- release checklist.

## Open items
The GDD leaves exact IAP tiers, ads/Battle Pass, content sourcing/moderation pipeline, anti-cheat details, disconnect handling, and final store marketing copy open. Track them as explicit decisions before launch.
