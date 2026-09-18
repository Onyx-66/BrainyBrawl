# 09 — 1v1 and Offline Modes

## Objective
Implement the complete 1v1 and Offline experiences on top of the shared game engine.

## 1v1
Entry:
- public quick match;
- direct friend invite;
- room create/join.

### Phase 1
- 15 rounds.
- Question alone for 10s.
- 5 answer options appear.
- 20s timer.
- First correct answer +1.

### Phase 2
- 5 image rounds.
- Theme + specification.
- Image.
- 10 answer choices with hidden point values.
- Exactly 4 choices.
- 30s.
- Reveal correctness and explanation.

### Result
Compare totals, declare winner, award exactly 1 Flame to the winner.

## Offline
No networking.
Player chooses:
- Answer the Questions
- Guess from Image

Use the same content formats and single-player pacing.
No Flame.
Update all-time best and session score-ratio statistics.

## Content
All questions, options, explanations, image metadata, and scoring values must come from the XLSX/imported content database, not Kotlin constants.

## Deliverables
Screens, ViewModels/use cases, game engine integration, offline content loader/cache, result screen, tests.

## Verification
Test timer expiry, answer submission, duplicate tap, no-answer round, image multi-select exactly-4 rule, and offline mode with airplane mode enabled.
