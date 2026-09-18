# 10 — Duo Mode

## Objective
Implement the 40-player / 20-team Duo mode exactly as specified.

## Lobby
- Up to 40 players.
- 2 slots per team.
- Private by default.
- Matchmaking toggle can open empty slots.
- Default team name `X's Team`.

## Phase 1 — Collaborative Puzzle
- 96 pieces.
- 12×8 conceptual board.
- Split left/right halves between teammates.
- 120 seconds.
- +1 per correct placement.
- Rank all Duos.
- Teammate cursor/placement attempts are live.
- Irregular variable-sized blocks, not merely uniform squares.
- Incorrect/unplaced slots remain visually distinct.

## Tiebreak
Use the specified 20-slot roulette/tiebreak behavior if two Duos are tied.

## Phase 2 — Theme Draft
15 questions:
- Q1–5: Duos #1–5 choose theme; Player 1 answers.
- Q6–10: Duos #6–10 choose theme; higher-scoring player answers.
- Q11–15: Duos #1–5 choose again; Player 2 answers.
- After theme selection, show a 15-second prompt.
- Exactly one answerer is selected within that window.

## Phase 3 — Buzzer Showdown
- Bottom 4 Duos eliminated.
- Top 6 continue.
- 5 Word Scramble words.
- 15s per word.
- First correct submission gets full points.
- Later correct submissions receive reduced points.
- Rank finalists.

Winner Duo members each receive 1 Flame.

## Deliverables
Team lobby, synchronized puzzle, theme draft, answerer selection, elimination, Word Scramble finale, ranking, reward.

## Verification
Simulate reconnects and delayed clients. The server must remain authoritative for placements, timers, answers, and rewards.
