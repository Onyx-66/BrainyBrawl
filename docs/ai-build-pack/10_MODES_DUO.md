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

-   All Duos continue (user correction, 2026-09-19).
-   No bottom-four elimination or top-six restriction.
-   5 Word Scramble words.
-   15s per word.
-   First correct submission gets full points.
-   Later correct submissions receive reduced points.
-   Rank finalists.

Winner Duo members each receive 1 Flame.

## Deliverables

Team lobby, synchronized puzzle, theme draft, answerer selection,
all-team continuation, Word Scramble finale, ranking, reward.

## Verification

Simulate reconnects and delayed clients. The server must remain
authoritative for placements, timers, answers, and rewards.

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

## User-approved draft scoring (2026-09-19)

After the 15-second answerer-selection window, designated teammates have
20 seconds to answer. Every team with a correct designated answer earns +1;
this is not a first-correct-only contest.

## RESOLVED ? Smaller Duo draft rooms (2026-09-19)

The user chose cycling through present ranked teams when a scheduled rank is
absent. Preserve all 15 questions. Map scheduled rank R to
`((R - 1) mod team_count) + 1`; the Player 1 / higher-scoring / Player 2
answerer schedule stays unchanged.
