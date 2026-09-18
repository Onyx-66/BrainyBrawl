# 18 --- Mockups & Visual Reference

## Purpose

This document makes the supplied UI master board usable by coding agents
without confusing visual reference with product behavior.

## Reference file

`mockups/UI_MASTER_REFERENCE.png`

Target device frame: - Samsung Galaxy A56 reference - 1080 × 2340 px -
20:9 portrait

## How agents must use it

For any UI task: 1. Open the reference before implementation. 2.
Identify the closest screen in the board. 3. Reuse the same component
grammar, hierarchy, spacing rhythm, colors, button semantics, and
navigation patterns. 4. Implement the written GDD rule exactly for
behavior. 5. Compare the result at the target aspect ratio. 6. Fix
visual differences that are not intentional. 7. If no reference screen
exists, infer only the established visual grammar; do not invent product
behavior.

## Screen families represented

-   Splash
-   Login
-   Sign Up
-   Home/Landing
-   Profile
-   Friends
-   Store
-   Settings
-   Mode selection
-   Loadout
-   Lobby
-   1v1 Question Round
-   1v1 Image Guess
-   Duo Puzzle
-   Precision Tap
-   Speed Sort
-   Theme selection
-   Solo Online shell
-   Results
-   Matchmaking
-   Countdown
-   Correct/Wrong states
-   Reactions
-   Reconnecting
-   Flame reward
-   Design-system components

## Layout rules

-   Portrait-first.
-   Keep primary actions obvious and reachable.
-   Use large rounded cards and compact metadata.
-   Maintain a consistent top bar/resource pattern.
-   Maintain bottom navigation where applicable.
-   Use full-width bottom CTAs for major confirmation actions.
-   Keep gameplay controls inside stable regions so the UI does not jump
    between rounds.
-   Avoid dense desktop-style layouts.
-   Never let content overlap the system gesture area or
    camera/safe-area region.

## Visual truth vs product truth

The board contains illustrative example text, player names, scores,
prices, questions, images, and mode labels. Those examples are **not**
production content or product rules.

Do not copy illustrative values into production code.

Use: - GDD for behavior; - XLSX/database for content; - backend for
authoritative state; - this board for visual appearance.

## Change control

If a developer believes the visual board should be changed: - do not
silently diverge; - document the reason; - update the mockup and/or this
document deliberately; - record the decision in
`17_DECISION_REGISTER.md` if it affects a product or system decision.
