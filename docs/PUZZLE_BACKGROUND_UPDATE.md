# Puzzle and scene artwork update

Owner request: exact 12 columns by 8 rows, a complete image at 60% opacity beneath the grid, full-opacity pieces, a 180-second puzzle, randomized splash/home scene images, and an installable Android APK.

The supplied Python cutter was read as reference code, and its exact validation/export algorithm is adapted into `tools/puzzle_image_cutter.py` without the desktop GUI. It rejects incompatible dimensions and existing output folders. Two original 1536×1024 sources each produce 96 unchanged 128×128 PNGs, numbered left-to-right then top-to-bottom. Tests reconstruct the full source byte-for-byte from the pieces. Opacity and grid lines are drawn only on the board, never baked into piece art.

New localized grid records use new stable IDs; six existing published irregular-puzzle records and already-running matches remain intact for compatibility. Migration 030 changes only future Duo starts to approved grid content and 180 seconds. Scoring (+1), 48/48 teammate ownership, deadline authority and subsequent phases remain unchanged.

## Reviewable shared-production change

Apply only `202609190030_grid_puzzle_180.sql` and six new approved localized puzzle records (two artworks × English/French/Arabic). No existing content rows, accounts, scores, balances or active deadlines are overwritten. The prior production approvals covered only migrations 001–029 and their content; this additional deployment requires scope-specific owner approval. Generated incremental SQL will be in `.local/backend/approved-content.sql` after unchanged published IDs are verified.

## Selected artwork

Imported mode shields, Gold/Gems/Flame art and empty-store/ranking illustrations from the supplied mockup pack. `assets/SELECTED_MOCKUP_ASSETS.json` records source names and hashes. No mockup prices, usernames, fake inventory or replacement branding were copied. Supplied screen art remains under `assets/screen/splash` and `assets/screen/home`; each folder is discovered at runtime, randomly selected once per application launch and kept stable through navigation/recomposition. One image currently exists in each folder; selection supports additional PNG/JPEG/WebP files. Home art is drawn at exactly 75% alpha beneath native readable controls. Splash progress reflects local artwork loading and waits for account initialization, with a short completion transition.

## Verified handoff

Version 1.0.1: 63 JVM tests, 27 emulator tests, 24 Python tests and 41 local PostgreSQL security/game-flow checks pass. Following the visual contrast fix, all five affected navigation tests passed again. Debug and optimized unsigned release APK assembly pass; lint has zero errors and 39 warnings. Installed testing APK is `deliverables/BrainyBrawl-1.0.1.apk`; its development signature, file integrity, packaged artwork, credential exclusion and native alignment were checked. It is not a signed Play release.

To author another puzzle, install `tools/asset-requirements.txt`, then run `python tools/puzzle_image_cutter.py assets/puzzles/example.png`. Use a 3:2 source with dimensions divisible by 12 and 8. Existing output directories are deliberately protected. New content needs new stable localized IDs and the normal publication review. To add background variation, add PNG/JPEG/WebP files with simple alphanumeric filenames to `assets/screen/home` or `assets/screen/splash` and rebuild.
