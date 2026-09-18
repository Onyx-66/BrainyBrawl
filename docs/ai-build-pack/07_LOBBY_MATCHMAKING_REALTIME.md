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
