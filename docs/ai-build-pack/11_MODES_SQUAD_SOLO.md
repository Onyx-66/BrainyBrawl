# 11 --- Squad & Solo Online Modes

## Objective

Implement Squad and Solo Online while reusing the shared engine and
mini-games.

## Squad

-   Up to 40 players.
-   10 teams of 4.
-   Private by default; matchmaking toggle.
-   Default team name `X's Team`.

### Phase 1 --- Precision Tap Relay

Each of 4 teammates gets one 20-second turn. Individual scores sum into
team score. Rank all Squads.

### Phase 2 --- Speed Sort Relay

-   One shared board per Squad.
-   90-second window.
-   Players take turns / act when live.
-   Drag items into category buckets.
-   Correct +1.
-   Wrong 0.
-   Timeout breaks streak bonus.
-   Re-rank teams.

### Phase 3 --- Theme Draft

-   20 questions.
-   Four-person adaptation of Duo theme-draft.
-   One designated answerer per question.
-   Selection window 15s.
-   Server-random theme-picking Squad and answering teammate per Squad for each
    question (user correction, 2026-09-19). Persist the draw across reconnects.

Top Squad members each receive 1 Flame.

## Solo Online

Owner delegated the final rule decision to engineering on 2026-09-19.

- 2–20 individual players; private by default, host starts when everybody is ready.
- Three-second synchronized countdown.
- Precision Tap: all players act simultaneously for 20 seconds. Existing streak scoring applies.
- Speed Sort: 90 seconds. Everyone receives the same shuffled sequence but maintains their own index; correct +1, wrong 0 and streak reset.
- Question Round: 15 distinct questions, each with 10 seconds to read then 20 seconds to answer. Every correct player earns +1. Accepted multilingual aliases apply.
- No eliminations; cumulative individual scores determine standings.
- A unique leader wins. Tied leaders use server-owned 20-slot dice; only players tied at the highest roll reroll until one winner remains. Other equal scores share their competition rank.
- Winner earns exactly one Flame. Offline practice earns none. Boosts are optional at launch.
- Deadlines remain authoritative during disconnect; reconnect restores current state without extending time. Missed questions earn zero.

## Deliverables

Squad relay/draft and Solo individual schedules, interactive boards, server scoring,
reconnect recovery, results, and idempotent Flame rewards.

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
