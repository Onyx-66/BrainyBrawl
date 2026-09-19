# 05 --- Profile, Friends, Blocking, Reporting & Quick Chat

## Objective

Implement social features with the GDD's safety-first launch scope.

## Profile

Support: - permanent 8-digit Player ID; - username; - email; - connected
accounts; - change password; - delete account after email
verification; - owned power-ups; - avatars/frames/banners.

Player ID is generated server-side and is permanent/non-editable.

## Friends

Support: - friend list; - search by username, email, Player ID; -
profile preview; - achievements; - cosmetics owned; - mode stats; -
friend request lifecycle; - accept/decline/remove.

## Blocking

Blocked users: - disappear from Friends List; - cannot invite each
other; - profile/stats hidden; - quick-chat/reactions hidden; - no
notification is sent to the blocked user.

## Reporting

Categories: - Harassment/Abusive Language - Cheating/Exploiting -
Inappropriate Username/Profile Content - Spam/Advertising - Other + free
text

Reporting does not automatically block. Prompt the reporter to
optionally block.

Reported messages are hidden client-side for the reporter pending
review.

## Chat

Launch with preset quick replies and reaction icons only. No open
free-text player chat.

## Backend

Use RLS so users can read/write only the records permitted by
friendship/block/report rules. Moderation actions must be
server-side/admin controlled.

## Deliverables

Profile, friend list, add/search, friend card, block/report flows,
quick-chat/reaction selector, and data-layer tests.

## Verification

Test blocked-user visibility, invites, quick-chat suppression, report
submission, and account deletion flow.

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
