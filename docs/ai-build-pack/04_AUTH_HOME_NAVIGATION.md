# 04 --- Authentication, Launch, Home & Navigation

## Objective

Implement the app entry experience and primary navigation.

## Required flows

### Launch

-   Animated launch scene.
-   Asset loading/progress indication.
-   Recover an existing authenticated session.
-   Route to Home or Auth.

### Auth

-   Username/email/password registration.
-   Password minimum 8 characters.
-   Email verification handling.
-   Login/logout.
-   Google connection.
-   Discord connection.
-   Recover/change password.

### Home

Landing page contains: - Store - Account/Profile - Friends - Start
Game - Settings

The persistent top area should expose Gold, Gems, and Flames where
appropriate.

### Navigation

Use typed route arguments or strongly validated navigation state. Never
pass sensitive data in navigation arguments.

## Error handling

Handle: - invalid credentials; - expired session; - verification
required; - network unavailable; - OAuth cancellation; - duplicate
username; - username validation; - server errors.

## Security

-   Auth state comes from Supabase.
-   Never store raw passwords.
-   Use secure platform storage for any local auth/session material.
-   Never expose privileged Supabase credentials.

## Deliverables

Fully navigable auth/home shell with fake content allowed temporarily,
but no hard-coded final game content.

## Verification

Test fresh install, returning session, logout, failed login, offline
launch, and navigation back-stack behavior.

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
