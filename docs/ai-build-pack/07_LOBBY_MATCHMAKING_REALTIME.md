# 07 --- Rooms, Matchmaking & Realtime Multiplayer

## Objective

Implement room lifecycle and realtime synchronization for up to 40
players in team modes.

## Modes/room sizes

-   1v1: 2
-   Solo Online: up to 20
-   Duo: up to 40 / 20 teams
-   Squad: up to 40 / 10 teams of 4
-   Offline: no room/networking

## Room defaults

Solo Online, Duo, and Squad rooms start Private. A Matchmaking toggle
opens empty slots to public matchmaking. Private rooms must not enter
the public pool until opened. 1v1 supports public quick match and direct
friend invite.

## Lobby behavior

Implement: - create room; - join by invite/code if selected by product
design; - friend invite; - leave; - host migration or explicit host-loss
behavior; - team assignment; - team naming; - ready state; - player
presence; - match start authorization.

Duo/Squad team names default to `X's Team` using Player 1's username if
no custom name is supplied.

## Realtime

Use Supabase Realtime for: - room membership; - ready states; - team
changes; - live gameplay events where appropriate.

Do not broadcast large/high-frequency state blindly. Prefer compact
event messages and server-stored authoritative state for important
transitions.

## Disconnect/reconnect

Design and test: - temporary network loss; - app backgrounding; -
reconnect; - player replacement where allowed; - abandoned room; - match
already advanced while client was offline.

## Deliverables

Lobby UI + repository + realtime event layer + server
policies/functions + automated state-machine tests.

## Verification

Test 2, 20, and 40-player simulated rooms where possible. Measure event
frequency and payload size.

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
