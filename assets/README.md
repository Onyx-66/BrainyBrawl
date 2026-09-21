# Asset layout

- `icons/`: navigation, currencies, game modes, Bluetooth modes and the start-game mascot.
- `ui/`: card backgrounds, currency counters and plus buttons.
- `avatars/`, `frames/`: numbered player cosmetics.
- `screen/`: full-screen backgrounds.
- `images/originals/`: preserved source PNGs; not packaged.
- `images/scenes/`: optimized image-game WebPs.
- `images/correct_answers.txt`: complete possible answers for each source image.
- `puzzles/originals/`: preserved source PNGs; not packaged.
- `puzzles/puzzle_*.webp`: packaged puzzle images, cropped at runtime.
- `store/`: editable, localized offer catalogs; see its README.

Historical SVG samples and unused older tile folders are retained for provenance; old `_parts` folders are excluded from the APK. Active records are defined in root `content/*.xml`. Refer to `docs/CONTENT_AUTHORING_GUIDE.md` for adding content and translations. Use forward slashes and exact filename case in all asset references.
