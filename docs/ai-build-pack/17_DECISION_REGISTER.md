# 17 --- Decision Register

This file prevents AI agents from silently inventing product rules.

## Confirmed by GDD

-   Platform: Android/Google Play.
-   Modes: 1v1, Solo Online, Duo, Squad, Offline.
-   Room sizes: 2 / 20 / 40 / 40 / 1.
-   Private-by-default for Solo Online/Duo/Squad.
-   Matchmaking toggle opens empty slots.
-   1v1 public quick match + friend invite.
-   Flames are skill-earned only.
-   Launch chat is preset quick replies/emotes, not free text.
-   English/French/Arabic at launch.
-   Arabic requires RTL engineering.
-   Kit A is the primary UI system.
-   Image Viewer behavior is defined.
-   Exact IAP tiers/Battle Pass/ad earning remain open.
-   Full content sourcing/moderation pipeline remains open.
-   Anti-cheat details remain open, though server-authoritative
    validation is recommended.
-   Disconnect/reconnect behavior remains an engineering item.

## Native-stack decisions made for this build pack

These are implementation choices requested by the user, not statements
from the GDD: - Android Studio. - Kotlin. - Jetpack Compose. - Supabase
Auth/Postgres/Realtime/Storage/Edge Functions. - Google Play Billing for
Gems. - Room/local cache where useful.

## Must not be invented by agents

-   Final Solo Online phase schedule.
-   Exact IAP pricing.
-   Battle Pass rules.
-   Advertising economy.
-   Final production trivia sourcing policy.
-   Legal/licensing status of third-party images.
-   Moderation thresholds.
-   Competitive ranking formulas beyond those explicitly defined.

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
