# 05 — Profile, Friends, Blocking, Reporting & Quick Chat

## Objective
Implement social features with the GDD's safety-first launch scope.

## Profile
Support:
- permanent 8-digit Player ID;
- username;
- email;
- connected accounts;
- change password;
- delete account after email verification;
- owned power-ups;
- avatars/frames/banners.

Player ID is generated server-side and is permanent/non-editable.

## Friends
Support:
- friend list;
- search by username, email, Player ID;
- profile preview;
- achievements;
- cosmetics owned;
- mode stats;
- friend request lifecycle;
- accept/decline/remove.

## Blocking
Blocked users:
- disappear from Friends List;
- cannot invite each other;
- profile/stats hidden;
- quick-chat/reactions hidden;
- no notification is sent to the blocked user.

## Reporting
Categories:
- Harassment/Abusive Language
- Cheating/Exploiting
- Inappropriate Username/Profile Content
- Spam/Advertising
- Other + free text

Reporting does not automatically block. Prompt the reporter to optionally block.

Reported messages are hidden client-side for the reporter pending review.

## Chat
Launch with preset quick replies and reaction icons only. No open free-text player chat.

## Backend
Use RLS so users can read/write only the records permitted by friendship/block/report rules. Moderation actions must be server-side/admin controlled.

## Deliverables
Profile, friend list, add/search, friend card, block/report flows, quick-chat/reaction selector, and data-layer tests.

## Verification
Test blocked-user visibility, invites, quick-chat suppression, report submission, and account deletion flow.
