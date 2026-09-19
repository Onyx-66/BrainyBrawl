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

The GDD defines Solo Online as up to 20 players in a free-for-all room,
host-started. The exact Solo Online phase/score schedule is not fully
specified beyond its inclusion of Precision Tap and Speed Sort in the
mini-game section.

Therefore: - implement room/presence infrastructure now; - implement the
reusable Precision Tap and Speed Sort components; - mark the final Solo
Online match schedule as `OPEN_DECISION` rather than inventing it.

## Deliverables

Squad lobby, relay scheduling, shared boards, theme draft, results, and
a Solo Online shell that can accept the final rules without architecture
changes.

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
