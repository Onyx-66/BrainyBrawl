# 11 — Squad & Solo Online Modes

## Objective
Implement Squad and Solo Online while reusing the shared engine and mini-games.

## Squad
- Up to 40 players.
- 10 teams of 4.
- Private by default; matchmaking toggle.
- Default team name `X's Team`.

### Phase 1 — Precision Tap Relay
Each of 4 teammates gets one 20-second turn.
Individual scores sum into team score.
Rank all Squads.

### Phase 2 — Speed Sort Relay
- One shared board per Squad.
- 90-second window.
- Players take turns / act when live.
- Drag items into category buckets.
- Correct +1.
- Wrong 0.
- Timeout breaks streak bonus.
- Re-rank teams.

### Phase 3 — Theme Draft
- 20 questions.
- Four-person adaptation of Duo theme-draft.
- One designated answerer per question.
- Selection window 15s.
- Turn order by rank.

Top Squad members each receive 1 Flame.

## Solo Online
The GDD defines Solo Online as up to 20 players in a free-for-all room, host-started. The exact Solo Online phase/score schedule is not fully specified beyond its inclusion of Precision Tap and Speed Sort in the mini-game section.

Therefore:
- implement room/presence infrastructure now;
- implement the reusable Precision Tap and Speed Sort components;
- mark the final Solo Online match schedule as `OPEN_DECISION` rather than inventing it.

## Deliverables
Squad lobby, relay scheduling, shared boards, theme draft, results, and a Solo Online shell that can accept the final rules without architecture changes.
