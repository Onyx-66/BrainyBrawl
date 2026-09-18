# 08 — Core Game Engine, Phases, Timers & Scoring

## Objective
Build a reusable deterministic match engine shared by all modes.

## Core concepts
Create domain models for:
- Match
- Phase
- Round
- Participant
- Team
- ContentItem
- Submission
- ScoreEvent
- MatchResult
- Tiebreaker
- Reward

## Server-authoritative timing
The client must calculate display countdown from an authoritative server timestamp/deadline. Never accept a client timestamp as proof of answer speed.

## State machine
Implement explicit transitions and reject invalid transitions.

## Scoring
### 1v1
Phase 1: 15 questions. Question visible alone for 10s, then 5 options and 20s timer. First correct answer gets +1.
Phase 2: 5 image rounds. 10 possible answers, hidden point values, exactly 4 selections within 30s. Reveal correct/wrong + explanation.
Winner receives 1 Flame.

### Offline
Question and image-guess formats. No Flame. Update all-time best and session score-ratio stats.

### Duo
Phase 1: 96-piece collaborative puzzle, 120s, +1 per correct placement, rank all teams, tiebreak as specified.
Phase 2: 15 theme-draft questions.
Phase 3: bottom 4 eliminated; top 6 Word Scramble, 5 words, 15s each. Winner receives 1 Flame each.

### Squad
Phase 1: Precision Tap relay, four 20s turns.
Phase 2: shared Speed Sort relay, 90s.
Phase 3: 20 theme-draft questions.
Top Squad members each receive 1 Flame.

## Tiebreak
Use Roll the Dice whenever entities remain tied. Higher roll ranks above; reroll on a tie.

## Anti-cheat
All competitive score events must be validated server-side. The server should verify:
- phase active;
- player eligible;
- submission not duplicated;
- deadline;
- content ID belongs to match;
- answer correctness;
- maximum achievable score.

## Deliverables
Reusable engine independent from Compose, with extensive unit tests.

## Verification
Property-based or table-driven tests for scoring, tie handling, duplicate submissions, late submissions, reconnects, and phase transitions.
