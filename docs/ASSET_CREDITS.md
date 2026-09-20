# Interface asset credits

- Poppins Regular, Bold and ExtraBold: Google Fonts distribution, SIL Open Font
  License 1.1. Source: https://github.com/google/fonts/tree/main/ofl/poppins
  License ships in `app/src/main/assets/licenses/Poppins-OFL.txt`.
- Controller/trophy/mode symbols and gradient hero/tile graphics: original
  Compose vector artwork created for Brainy Brawl. No reference-board sample
  player, price, score or question is embedded in these assets.

- Launcher icon and transparent Brainy Brawl logo: supplied by the project owner on September 19, 2026. Source images are copied intact to drawable-nodpi/brand_icon.png and brand_logo.png.

- Tajawal Regular, Bold and ExtraBold: [official Google Fonts distribution](https://github.com/google/fonts/tree/main/ofl/tajawal), SIL Open Font License 1.1. Bundled in `res/font`; license in `assets/licenses/Tajawal-OFL.txt`. Arabic uses these fonts instead of platform fallback.

- Selected mode badges, currency symbols, empty-store/ranking art, and Orbital Garden puzzle: owner-supplied `assets_mockups` pack. The pack describes these as original artwork. Selected source names and hashes are recorded in `assets/SELECTED_MOCKUP_ASSETS.json`; none of its sample product data is shipped.
- Alpine lake puzzle and splash/home scenes: supplied by the project owner. Originals are preserved in `assets/puzzles` and `assets/screen`. Puzzle PNG tiles are exact, unresized crops made with `tools/puzzle_image_cutter.py`, adapted from the owner-supplied reference script.

- Compact provider-button reference: [Divyank-Gupta-g / happy-robin-54](https://uiverse.io/Divyank-Gupta-g/happy-robin-54), supplied by the owner. Native Compose layout and Canvas provider marks are implemented locally; no web component code was copied. Google/Discord marks identify their respective authentication providers, not Brainy Brawl branding.

- Navigation/account/action vectors: [Lucide](https://github.com/lucide-icons/lucide), featured by the owner's [Morphicons reference](https://www.morphicons.com/). ISC/MIT license and required notices are bundled in `assets/licenses/Lucide-LICENSE.txt`. Source SVG hashes and URLs: `assets/icons/LUCIDE_SOURCES.json`. Native vector conversion retains stroke geometry; the Morphicons JavaScript animation runtime is not embedded in Compose.
- Twelve avatar/frame slots currently use original native square preview drawings. Final owner art can be added at the named PNG paths in `assets/avatars/README.md` and `assets/frames/README.md`.
