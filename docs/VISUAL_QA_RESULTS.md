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

## Final September 19 revision

Reviewed local captures in `.local/visual-qa/files/`: `redesign-auth-dark.png`, `redesign-auth-light.png`, `redesign-modes.png`, `profile-bento.png`, `redesign-offline-ar.png`, `redesign-offline-image.png`, `redesign-settings-ar.png` and `redesign-settings-fr.png`. Auth now respects the theme (the forced white card was removed at owner request); provider tiles are deliberately disabled with localized explanatory text. Profile actions use bento tiles and a photo/initial header. Arabic uses Tajawal, mirrors layout and shows immediately selectable answers and a 45-second timer. Offline images render packaged artwork, ten choices and a 30-second timer. SQL-derived Solo precision HUD also passed the visible/enabled action assertion.

The actual normal app signed into the hosted account, rendered its username/level/balances and opened Store and Leaderboards without erroneous login gates. An empty catalog and no completed-match ranking are honest backend states. Final normal home evidence is `.local/visual-qa/live-home.png`. UI hierarchy was inspected for the two authenticated pages. These checks supplement the earlier mockup comparison; they do not certify every live multiplayer transition or physical-device accessibility/performance.

## Version 1.0.1 scene and puzzle artwork

Inspected `.local/visual-qa/assets/splash.png`, `puzzle.png`, `modes.png`, `home.png`, `home-light.png` and `live-home.png` at the same 1080×2340 emulator target. The owner-supplied splash scene fills the screen behind the transparent logo and readable loading panel. Home shows the supplied cave scene at 75% opacity, with themed header backing, a readable white/shadow heading, bento cards and distinct mode badges. Visual review caught black inherited text on the transparent dark home; explicit Scaffold content colors and readable text backing fixed it. Both themes were recaptured; the added heading regression assertion passes.

The new puzzle board matches the written 12×8 grid (rather than counting cells in the illustrative screenshot), shows the complete lake image at 60% opacity and displays unaltered square thumbnail art. Instrumentation verifies three guide pixels and the full-opacity first placed tile. The local PNG reconstruction test verifies every source pixel across all 192 pieces. Existing published irregular puzzles remain supported only for older matches/content compatibility; new Duo starts select grid records and 180 seconds.

The normal application was installed in place and launched with the existing Mr.onyx account, new currency art, readable status/header and supplied home background. Full physical-device and live simultaneous-client testing remain the release gates already recorded above.

## Version 1.0.2 shared background clarification

Removed the home-route restriction and the opaque destination gradients. The same selected scene stays behind authentication, modes, profile, friends, settings, store, rooms and gameplay. Header and cards retain theme-aware surfaces. Navigation captures now assert the shared scene is present; six affected navigation/account checks passed. Screenshot review found muted light-theme auth descriptions needed backing, so they now have compact themed surfaces. The five navigation cases were rerun after that adjustment. This UI-only change needs no further production deployment.

The final shared-background revision passed the full 27-test emulator suite, 63 JVM tests, debug/optimized-release builds and lint (zero errors, 39 warnings). APK 1.0.2 passed signature, private-credential exclusion, artwork integrity and native alignment checks, then was installed in place and launched on the normal emulator.

## Version 1.0.3 account and mode navigation

Reviewed `.local/visual-qa/account-modes/` captures of compact provider buttons/recovery text, the dedicated Duo room-action page, offline Puzzle and the framed player card with its inset email/sign-in methods. The language popup now has an opaque navy surface; a Compose bounds assertion confirms it matches the anchor within two pixels. French and Arabic selection/RTL checks pass. Both dark/light auth captures pass the surface luminance assertion. Background presence remains asserted across captured app destinations.

The isolated API 37 suite passed all 29 tests. Following final password serialization/backup exclusion and connect-screen navigation changes, the 11 affected encryption/auth/navigation checks passed again. The popup screenshot helper was corrected to select the menu rather than both window roots; the layout assertion was preserved. Local practice Puzzle renders all 96 pieces and the 180-second timer, with correct/duplicate/late placement rules verified independently. No live multiplayer, provider integration or physical-device certification is inferred from these captures.

The normal 1.0.3 app was then installed in place with the existing Mr.onyx session. Its hosted profile, level, balances, player number and email/method panel rendered correctly; the actual `live-profile.png` capture was inspected. No new production accounts, rooms, scores or migrations were created for this check.
