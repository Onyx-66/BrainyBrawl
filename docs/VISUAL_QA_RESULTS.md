# Visual QA evidence

Reference: `mockups/UI_MASTER_REFERENCE.png`, with the checklist in
`docs/ai-build-pack/19_VISUAL_QA_CHECKLIST.md`. Target emulator viewport:
1080 x 2340 portrait (Android 17/API 37). Captures are local-only under
`.local/visual-qa/` and excluded from the source patch archive.

## Inspected screens

- Authentication and Arabic settings: navy surfaces, rounded controls, readable
  hierarchy, language direction and navigation were inspected earlier in this session.
- Collaborative Puzzle component: the 12 x 8 board, irregular boundaries, rotated
  thumbnails, gold selection and purple actions were inspected in `puzzle-ui.png`.
- English Precision Tap HUD: `hud-squad_initial-en.png`; four compact teammate
  cards, gold progress, navy ring, green target/action and readable play area.
- French Speed Sort HUD at 130% text: `hud-squad_sort-fr.png`; instructions wrap,
  player cards and both category actions remain reachable without clipping.
- Arabic Duo draft HUD at 130% text: `hud-duo_draft-ar.png`; mirrored cards,
  right-aligned text, designated-answerer and waiting state remain readable.

The latter three are production composables rendering actual SQL test snapshots.
Fixture usernames, categories and theme labels are synthetic test data; no test
repository is packaged into the production APK. These captures do not verify
remote Realtime delivery or production-localized gameplay content.

## Corrections made

Image viewing now has a separate dismiss target. Puzzle selection/rotation and
HUD spacing were corrected. Action mini-games now use a time-remaining label
instead of the multiple-choice answer prompt; Precision Tap displays the active
20-second relay turn timer. Result screens stop their high-frequency timer.
Packaged artwork state is published on the Android main looper after background
decoding. The final English HUD was recaptured and inspected after timer corrections;
the turn countdown and Tap action remain visible. The navigation content handles keyboard insets, and account changes clear the
previous player's match selection and subscription.

## Remaining visual/release checks

This is partial visual QA, not a blanket completion of the checklist. Full
signed-in navigation, store/catalog, live match transitions, real reaction traffic,
all content variants, physical Galaxy A56, API 26, accessibility services and
measured frame/memory performance still require integration/device verification.
The isolated HUD captures do not prove the enclosing app shell's system insets.
Approved production content and configured test accounts are required for the
complete end-to-end visual pass. No mockup sample prices or players were promoted
into production data.

The Flame resource chip was corrected from green to gold. This one-line token
change compiled during the successful airplane-mode smoke verification; its
signed-in profile rendering has not been captured against a live backend.

## Reference redesign requested after manual testing

Auth, home, modes and Arabic offline screens were recaptured at 1080 x 2340 and
inspected after the redesign. The home uses four equal-height colored tiles and
an original brain hero; compact filled account fields leave all providers and
Play offline visible. Poppins and blue/cyan navigation follow the reference.
The overflowing leaderboard tile label and English question direction inside
Arabic UI were corrected and recaptured. See `UI_REDESIGN.md` and asset credits.
