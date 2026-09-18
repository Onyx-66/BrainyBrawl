# 08 --- Core Game Engine, Phases, Timers & Scoring

## Objective

Build a reusable deterministic match engine shared by all modes.

## Core concepts

Create domain models for: - Match - Phase - Round - Participant - Team -
ContentItem - Submission - ScoreEvent - MatchResult - Tiebreaker -
Reward

## Server-authoritative timing

The client must calculate display countdown from an authoritative server
timestamp/deadline. Never accept a client timestamp as proof of answer
speed.

## State machine

Implement explicit transitions and reject invalid transitions.

## Scoring

### 1v1

Phase 1: 15 questions. Question visible alone for 10s, then 5 options
and 20s timer. First correct answer gets +1. Phase 2: 5 image rounds. 10
possible answers, hidden point values, exactly 4 selections within 30s.
Reveal correct/wrong + explanation. Winner receives 1 Flame.

### Offline

Question and image-guess formats. No Flame. Update all-time best and
session score-ratio stats.

### Duo

Phase 1: 96-piece collaborative puzzle, 120s, +1 per correct placement,
rank all teams, tiebreak as specified. Phase 2: 15 theme-draft
questions. Phase 3: bottom 4 eliminated; top 6 Word Scramble, 5 words,
15s each. Winner receives 1 Flame each.

### Squad

Phase 1: Precision Tap relay, four 20s turns. Phase 2: shared Speed Sort
relay, 90s. Phase 3: 20 theme-draft questions. Top Squad members each
receive 1 Flame.

## Tiebreak

Use Roll the Dice whenever entities remain tied. Higher roll ranks
above; reroll on a tie.

## Anti-cheat

All competitive score events must be validated server-side. The server
should verify: - phase active; - player eligible; - submission not
duplicated; - deadline; - content ID belongs to match; - answer
correctness; - maximum achievable score.

## Deliverables

Reusable engine independent from Compose, with extensive unit tests.

## Verification

Property-based or table-driven tests for scoring, tie handling,
duplicate submissions, late submissions, reconnects, and phase
transitions.

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
