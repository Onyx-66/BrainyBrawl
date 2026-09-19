# 08 --- Core Game Engine, Phases, Timers & Scoring

## Objective

Build a reusable deterministic match engine shared by all modes.

## Core concepts

Create domain models for: - Match - Phase - Round - Participant - Team -
ContentItem - Submission - ScoreEvent - MatchResult - Tiebreaker -
Reward

## Server-authoritative timing

The client must calculate display countdown from an authoritative server
timestamp/deadline. Never accept a client timestamp as proof of answer
speed.

## State machine

Implement explicit transitions and reject invalid transitions.

## Scoring

### 1v1

Phase 1: 15 questions. Question visible alone for 10s, then 5 options
and 20s timer. First correct answer gets +1. Phase 2: 5 image rounds. 10
possible answers, hidden point values, exactly 4 selections within 30s.
Reveal correct/wrong + explanation. Winner receives 1 Flame.

### Offline

Question and image-guess formats. No Flame. Update all-time best and
session score-ratio stats.

### Duo

Phase 1: 96-piece collaborative puzzle, 120s, +1 per correct placement,
rank all teams, tiebreak as specified. Phase 2: 15 theme-draft
questions. Phase 3: bottom 4 eliminated; top 6 Word Scramble, 5 words,
15s each. Winner receives 1 Flame each.

### Squad

Phase 1: Precision Tap relay, four 20s turns. Phase 2: shared Speed Sort
relay, 90s. Phase 3: 20 theme-draft questions. Top Squad members each
receive 1 Flame.

## Tiebreak

Use Roll the Dice whenever entities remain tied. Higher roll ranks
above; reroll on a tie.

## Anti-cheat

All competitive score events must be validated server-side. The server
should verify: - phase active; - player eligible; - submission not
duplicated; - deadline; - content ID belongs to match; - answer
correctness; - maximum achievable score.

## Deliverables

Reusable engine independent from Compose, with extensive unit tests.

## Verification

Property-based or table-driven tests for scoring, tie handling,
duplicate submissions, late submissions, reconnects, and phase
transitions.

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
