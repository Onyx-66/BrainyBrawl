# 10 --- Duo Mode

## Objective

Implement the 40-player / 20-team Duo mode exactly as specified.

## Lobby

-   Up to 40 players.
-   2 slots per team.
-   Private by default.
-   Matchmaking toggle can open empty slots.
-   Default team name `X's Team`.

## Phase 1 --- Collaborative Puzzle

-   96 pieces.
-   12×8 conceptual board.
-   Split left/right halves between teammates.
-   120 seconds.
-   +1 per correct placement.
-   Rank all Duos.
-   Teammate cursor/placement attempts are live.
-   Irregular variable-sized blocks, not merely uniform squares.
-   Incorrect/unplaced slots remain visually distinct.

## Tiebreak

Use the specified 20-slot roulette/tiebreak behavior if two Duos are
tied.

## Phase 2 --- Theme Draft

15 questions: - Q1--5: Duos #1--5 choose theme; Player 1 answers. -
Q6--10: Duos #6--10 choose theme; higher-scoring player answers. -
Q11--15: Duos #1--5 choose again; Player 2 answers. - After theme
selection, show a 15-second prompt. - Exactly one answerer is selected
within that window.

## Phase 3 --- Buzzer Showdown

-   Bottom 4 Duos eliminated.
-   Top 6 continue.
-   5 Word Scramble words.
-   15s per word.
-   First correct submission gets full points.
-   Later correct submissions receive reduced points.
-   Rank finalists.

Winner Duo members each receive 1 Flame.

## Deliverables

Team lobby, synchronized puzzle, theme draft, answerer selection,
elimination, Word Scramble finale, ranking, reward.

## Verification

Simulate reconnects and delayed clients. The server must remain
authoritative for placements, timers, answers, and rewards.

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
