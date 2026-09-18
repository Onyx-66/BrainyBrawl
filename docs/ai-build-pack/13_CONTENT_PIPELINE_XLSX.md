# 13 — Content Database & XLSX Import Pipeline

## Objective
Make the XLSX the authoring source for game content while keeping runtime access safe, fast, and localizable.

## Workbook
Use `Brainy_Brawl_Content.xlsx`.

Sheets:
- README
- Questions
- ImageGuess
- Puzzles
- PuzzlePieces
- PrecisionTap
- WordScramble
- SpeedSort
- Reactions
- ModesConfig
- EconomyItems
- LocalizationKeys

## Rules
Every content row needs a stable unique ID.

Production content must include:
- locale;
- status (`DRAFT`, `REVIEW`, `APPROVED`, `RETIRED`);
- author/source metadata where applicable;
- theme/category;
- difficulty;
- explanation where relevant;
- media asset key where relevant.

Never use row number as an ID.

## Import pipeline
1. Validate workbook.
2. Detect duplicate IDs.
3. Validate required columns.
4. Validate foreign keys.
5. Validate answer counts.
6. Validate exactly-one correct answer for Question rows.
7. Validate exactly-four selections are possible for ImageGuess.
8. Validate hidden point values.
9. Validate mini-game configuration ranges.
10. Validate localization keys.
11. Produce a machine-readable import report.
12. Import approved content only.

## Media
Image rows reference an `asset_key`, not a local absolute path.
Storage should contain versioned assets and metadata.

For image trivia, keep `source_url`, `license`, and `attribution` fields where applicable.

## Localization
English is the master/reference language.
French and Arabic use the same keys.
Arabic requires RTL-aware rendering.

## Deliverables
- Workbook validator.
- Import script/tool.
- Database seed/import process.
- Error report.
- Example approved content.

## Important
The GDD does not provide a complete production trivia database or image asset library. The workbook therefore contains a schema plus seed/demo records. Production content must be reviewed and licensed before release.
