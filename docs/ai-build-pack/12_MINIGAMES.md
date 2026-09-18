# 12 — Mini-Games

## Objective
Implement each mini-game as an isolated, testable game component with content/configuration supplied by the XLSX/database.

## Question Round
- Text question.
- 5 answers.
- 20s answer timer.
- Used in 1v1 Phase 1 and Offline.

## Image Guess
- Image + theme + specification.
- 10 answer choices.
- Each choice has hidden point value.
- Player selects exactly 4.
- 30s.
- Reveal correct/wrong and explanation.
- Used in 1v1 Phase 2 and Offline.

## Collaborative Puzzle
- 96 pieces.
- Conceptual 12×8 layout.
- Left/right split.
- 120s.
- +1 correct placement.
- Live teammate cursor/attempt visibility.
- Irregular variable-sized blocks.
- Crisp full-color art when correctly placed.

## Precision Tap
- Rotating marker around ring.
- Hot-zone arc.
- Tap when marker crosses hot zone.
- Success +1.
- Consecutive streak bonus: 2 in row +1 extra, 3 in row +2 extra, etc.
- Miss resets streak.
- Rotation speed and hot-zone width increase as streak grows.
- Use a configurable deterministic seed for test mode.

## Roll the Dice
- Used as tiebreak.
- Each side rolls.
- Higher wins.
- Re-roll ties.
- For competitive play, roll result must be generated/validated server-side.

## Word Scramble
- Shuffle letters of a trivia answer.
- 10–15s.
- First correct gets full points.
- Later correct submissions receive reduced points.
- Used in Duo Phase 3.

## Speed Sort
- Stream of items.
- 2+ category buckets.
- Drag item to bucket.
- Correct +1.
- Wrong 0.
- Timeout breaks streak.
- Used Solo/Offline and Squad Phase 2.

## Engineering rules
Each mini-game exposes:
- `start(config)`
- `render(state)`
- `submit(action)`
- `advance(now)`
- `finish()`
- deterministic scoring
- serialization of state for reconnect/replay where needed

Do not put backend access directly inside mini-game UI.
