# Brainy Brawl --- AI Build Pack v3

## Purpose

This pack is the implementation contract for the native Android game
**Brainy Brawl**, built with Android Studio, Kotlin, Jetpack Compose,
and Supabase.

It combines: - the GDD-derived product requirements; - modular
engineering instructions; - the structured content workbook; - a visual
master reference for the intended UI; - a staged AI-agent execution
plan.

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

## Source-of-truth hierarchy

Use this hierarchy exactly:

1.  **GDD-derived written requirements** --- authoritative for product
    behavior and game rules.
2.  **Feature instruction files in this folder** --- authoritative
    implementation constraints and clarifications.
3.  **Decision Register** --- confirmed decisions are binding;
    `OPEN_DECISION` items are unresolved.
4.  **`mockups/UI_MASTER_REFERENCE.png`** --- authoritative visual
    target for look, layout language, component hierarchy, and screen
    composition where it does not conflict with written requirements.
5.  **`Brainy_Brawl_Content.xlsx`** --- authoritative structured content
    source for IDs/records; production approval/licensing still applies.
6.  **AI prompt** --- orchestration instructions only; it never
    overrides the sources above.

## Non-negotiable engineering rules

-   Kotlin + Jetpack Compose.
-   Supabase for backend services.
-   No Unity/Firebase architecture.
-   Competitive state, scoring, winners, rewards, and authoritative
    deadlines are server-controlled.
-   Never place privileged Supabase credentials in the Android client.
-   Never hard-code production trivia/game content that belongs in the
    workbook/database.
-   Never hard-code user-facing strings; use localization keys.
-   Do not add free-text chat at launch.
-   Preserve Light/Dark support and Arabic RTL.
-   Do not invent unresolved product decisions.
-   Do not make destructive production changes automatically.
-   Do not weaken tests to obtain a green build.

## AI-agent execution

Run exactly one prompt at a time from `PROMPTS.md`.

Before editing, an agent must inspect: - the current repository; - the
prompt-specific instruction file(s); - `17_DECISION_REGISTER.md`; -
`mockups/UI_MASTER_REFERENCE.png` for UI work.

After editing, the agent must: - build/test/lint; - compare affected UI
to the master reference at 1080 × 2340; - report changed files; - report
tests/build/lint; - report migrations/functions; - report security
considerations; - report known limitations and `OPEN_DECISION`s; -
identify the exact next dependency.

Commit each accepted prompt as a separate Git commit on `master`.

## Definition of Done

A feature is not complete because a screen renders. It must have: -
correct visual hierarchy; - loading/empty/error states; - accessibility
semantics; - localization; - Light/Dark behavior; - RTL behavior where
applicable; - tests for domain rules; - server validation for
competitive actions; - no leaked secrets; - no hard-coded production
content; - visual verification against the master reference for affected
screens.
