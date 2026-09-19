# 09 --- 1v1 and Offline Modes

## Objective

Implement the complete 1v1 and Offline experiences on top of the shared
game engine.

## 1v1

Entry: - public quick match; - direct friend invite; - room create/join.

### Phase 1

-   15 rounds.
-   Question alone for 10s.
-   5 answer options appear.
-   20s timer.
-   First correct answer +1.

### Phase 2

-   5 image rounds.
-   Theme + specification.
-   Image.
-   10 answer choices with hidden point values.
-   Exactly 4 choices.
-   30s.
-   Reveal correctness and explanation.

### Result

Compare totals, declare winner, award exactly 1 Flame to the winner.

## Offline

No networking. Player chooses: - Answer the Questions - Guess from Image

Use the same content formats and single-player pacing. No Flame. Update
all-time best and session score-ratio statistics.

## Content

All questions, options, explanations, image metadata, and scoring values
must come from the XLSX/imported content database, not Kotlin constants.

## Deliverables

Screens, ViewModels/use cases, game engine integration, offline content
loader/cache, result screen, tests.

## Verification

Test timer expiry, answer submission, duplicate tap, no-answer round,
image multi-select exactly-4 rule, and offline mode with airplane mode
enabled.

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
