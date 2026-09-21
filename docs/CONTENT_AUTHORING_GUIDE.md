# Content authoring guide

Work in `C:/Users/kossa/AndroidStudioProjects/BrainyBrawl`. Edit `assets/`, `content/` and `content/sources/`; never edit generated `app/build/` files. Save text as UTF-8. English, French and Arabic are supported.

## One-time setup

```powershell
python -m pip install --target .local/python -r tools/asset-requirements.txt
```

Pillow makes WebP copies and num2words adds written-number answer aliases. Original PNGs stay in the `originals` folders and are excluded from the APK. Keep permanent content IDs: when replacing a published question or image, assign a new ID and retire the previous record so match history remains readable.

## Add an image guessing round

1. Put the PNG in `assets/images/originals/`, for example `C01_Kitchen.png`. Use a simple filename with no spaces.
2. Open `assets/images/correct_answers.txt`. Copy an existing row and fill its four columns: unique image ID, filename without extension, original filename, and **every possible correct answer** separated by commas. For example:

   ```text
   C01 | C01_Kitchen | C01_Kitchen.png | man, cat, table, bread
   ```

   Include all visible concepts, even if you would not choose them as one of the four answers. The complete pool is what prevents a fifth correct answer being offered as a distractor.
3. Add new concepts to `content/sources/image_vocabulary.tsv`. Each line is `English aliases separated by commas|French label|Arabic label`. Reuse existing concepts instead of creating singular/plural or synonym duplicates. Example:

   ```text
   cat,cats|chat|قطة
   ```

   One image needs at least four distinct concepts. Every image must have at least six genuinely absent concepts available as distractors. Review the picture and its full possible-answer list, including background objects.
4. Generate the optimized images and records:

   ```powershell
   python tools/import_visual_content.py
   python tools/import_image_pools.py
   ```

   The visual importer also refreshes the numbered puzzle pack described below. It preserves originals. Image records are generated for all three languages with IDs such as `SCENE_C01_EN`.
5. In `content/image_guess.xml`, find those three IDs. Edit `theme` for the broad topic, `specification` for the subtopic, and `prompt` / `explanation` for the instruction. The importer defaults to Observation and the image ID. Re-running it resets these generated fields, so keep a copy of custom editorial changes or apply them after importing.
6. Review the generated `content/sources/image_pools.json`: a wrong concept must never describe the image. The generator excludes the complete correct pool, its vocabulary aliases and overlapping generic labels. Editorial review is still needed for concepts missing from your source list.
7. Validate, build and test through **Games → Offline → Guess the image**.

Each round samples **four correct choices and six wrong choices**, shuffles the ten, and allows exactly four selections. Online players receive the same locked selection for that round. Unselected correct concepts never become wrong options. For hand-authored XML, use `<choices pool="true">` with at least four correct and six incorrect choices. A fixed, non-pool round must have exactly ten choices and exactly four correct.

## Add or edit a question

The supplied 250-question source is `content/sources/questions_250.json`. Its translated topics live in `question_topics.json`; explicit accepted alternatives live in `question_aliases.json`.

1. To correct that supplied pack, edit the relevant `question`, `answer` and `topic` in the JSON. Add meaningful short forms and alternate spellings under that question ID in `question_aliases.json`.
2. Run `python tools/import_question_pack.py`. It rebuilds that 250-question pack in all three languages and expands aliases across the existing bank. It expects 250 source questions; to append unrelated questions use the XML steps below.
3. To add a new question, copy a full approved item in `content/question_round.xml`. Give it a new permanent ID, such as `Q_SCIENCE_0101_EN`, and new option IDs. Start with `status="DRAFT"`.
4. Edit `theme`, `difficulty`, `question`, `explanation`, `source_note` and `concept_id`. Use the same `concept_id` for all translations.
5. Supply five options with exactly one correct option. Keep the legacy `option_1` through `option_5` fields consistent; `correct_option_index` starts at zero. Keep the current 20-second timer and one base point.
6. Add `<acceptedAnswers><answer>...</answer></acceptedAnswers>` with the answer in **all three languages**, common abbreviations, meaningful short names, and written/numeric forms. Copy the same accepted set into the English, French and Arabic records.
7. Review facts and translations, set the three records to `APPROVED`, increase the root `contentVersion`, validate and build.

Typed answers ignore case, accents, extra spacing, Arabic diacritics and Arabic/Persian digit shapes. Bounded spelling matching handles common insertions, omissions, substitutions and swapped letters. Authored aliases accept context-specific forms such as Mali, Westfalia and Constantine. Number questions accept authored words or digits (including the requested `eiyt` alias for eight). Different digits and known different number words remain incorrect. Avoid adding broad aliases that could also mean a different answer.

## Add a puzzle

1. Place the PNG in `assets/puzzles/originals/`, using the next number, such as `puzzle_50.png`. The current layout is **12 columns × 8 rows**, with 96 pieces. Use **1536 × 1024** or another 3:2 image for square pieces.
2. Run `python tools/import_visual_content.py`. It creates `assets/puzzles/puzzle_50.webp` and three XML records, `PZ_PUZZLE_50_EN/FR/AR`. This importer treats the numbered pack as the current collection and retires older non-pack records while preserving their definitions.
3. In `content/collaborative_puzzle.xml`, edit `difficulty` and add translated `theme` fields if desired. Topic metadata does not create a topic-selection screen. Review `source_url`, `license` and `attribution`; replace the owner-art default when using a different source.
4. Validate, build and test **Games → Offline → Puzzle**. All internet puzzle modes use the same board renderer.

The app crops pieces directly from the WebP using the normalized polygons in XML; separate piece PNGs are unnecessary. A thin white border surrounds each placed piece or connected group. Shared internal borders disappear as neighbours are placed. The guide is 18% opaque and can be switched off in Settings.

The older `puzzle_image_cutter.py` and `add_puzzle_content.py` remain available for custom exact PNG tile workflows. Their generated `_parts` directories are excluded from this APK configuration; use the new full-image path for the current pack.

## Validate and build

```powershell
python tools/content_pipeline.py validate
python tools/content_pipeline.py validate --production
$env:JAVA_HOME = 'C:/Program Files/Android/Android Studio/jbr'
./gradlew.bat testDebugUnitTest assembleDebug --console=plain
```

Production validation requires publishable records to be approved; retired records remain for history. The APK is `app/build/outputs/apk/debug/app-debug.apk`. Install it with `adb install -r` to preserve existing app data. Content is sampled, so a new item may not appear first.

## Make new content available in internet matches

A new APK updates practice and Bluetooth content. Internet matches also need the matching server records. Ship/install the APK with new artwork before publishing its online records, because an older APK does not contain new packaged images.

1. Run `python tools/publish_content_update.py` for a read-only preview. It validates the bank, prepares only IDs missing from the configured backend, lists retired records and reports alias updates.
2. Review `.local/backend/content-1.0.8.sql`, the XML changes and the target configured in your private `.env`. Keep credentials out of Git.
3. Apply reviewed backend migrations first through `tools/deploy_backend.py` if schema changes are pending.
4. During a period with no active matches, publish with `python tools/publish_content_update.py --apply --confirmed-project YOUR_PROJECT_REF`. The operation is atomic, refuses active matches, preserves historical records and updates existing question aliases. Use the actual project reference, never a password.
5. Verify the new languages and image rounds. Correct-answer keys remain private on the server.

Store editing has its own guide at `assets/store/README.md`.
