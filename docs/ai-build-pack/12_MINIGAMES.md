# 12 --- Mini-Games

## Objective

Implement each mini-game as an isolated, testable game component with
content/configuration supplied by the XLSX/database.

## Question Round

-   Text question.
-   5 answers.
-   20s answer timer.
-   Used in 1v1 Phase 1 and Offline.

## Image Guess

-   Image + theme + specification.
-   10 answer choices.
-   Each choice has hidden point value.
-   Player selects exactly 4.
-   30s.
-   Reveal correct/wrong and explanation.
-   Used in 1v1 Phase 2 and Offline.

## Collaborative Puzzle

-   96 pieces.
-   Exact 12×8 grid of equal square pieces (owner update, September 19, 2026).
-   Left/right split.
-   180s.
-   +1 correct placement.
-   Live teammate cursor/attempt visibility.
-   Source image at 60% opacity beneath the grid; 96 exact PNG cuts.
-   Crisp full-color art when correctly placed.

## Precision Tap

-   Rotating marker around ring.
-   Hot-zone arc.
-   Tap when marker crosses hot zone.
-   Success +1.
-   Consecutive streak bonus: 2 in row +1 extra, 3 in row +2 extra, etc.
-   Miss resets streak.
-   Rotation speed and hot-zone width increase as streak grows.
-   Use a configurable deterministic seed for test mode.

## Roll the Dice

-   Used as tiebreak.
-   Each side rolls.
-   Higher wins.
-   Re-roll ties.
-   For competitive play, roll result must be generated/validated
    server-side.

## Word Scramble

-   Shuffle letters of a trivia answer.
-   10--15s.
-   First correct gets full points.
-   Later correct submissions receive reduced points.
-   Used in Duo Phase 3.

## Speed Sort

-   Stream of items.
-   2+ category buckets.
-   Drag item to bucket.
-   Correct +1.
-   Wrong 0.
-   Timeout breaks streak.
-   Used Solo/Offline and Squad Phase 2.

## Engineering rules

Each mini-game exposes: - `start(config)` - `render(state)` -
`submit(action)` - `advance(now)` - `finish()` - deterministic scoring -
serialization of state for reconnect/replay where needed

Do not put backend access directly inside mini-game UI.

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
