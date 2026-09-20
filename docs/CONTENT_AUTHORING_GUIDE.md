# Content authoring guide

Work in `C:/Users/kossa/AndroidStudioProjects/BrainyBrawl`. Edit the root `content/` and `assets/` folders, never generated files under `app/build/`. Save XML as UTF-8.

## Common rules

1. Assign permanent unique IDs, such as `IMG_EN_ANIMALS_0101`, `Q_EN_SCIENCE_0101` or `PUZ_LAKE_V2_EN`. Use letters, digits, dots, underscores and hyphens, with no spaces. Change child option IDs too.
2. Create separate `en`, `fr` and `ar` records. The app uses the selected language without silently substituting English. Translations of one question should share a `concept_id`.
3. Start with `status="DRAFT"`; change to `APPROVED` after reviewing facts, translations and image rights. Drafts are not playable. `DEV_SAMPLE` is debug-only.
4. Increment the file's root `contentVersion` when changing the bank; keep `schemaVersion="1"`. Create new versioned IDs when replacing published content instead of overwriting old IDs.
5. Asset references use forward slashes and exact case, for example `assets/images/red_fox.png`. PNG, JPG, JPEG, WebP and SVG files are packaged automatically.

## Guess the image

1. Put the original picture in `assets/images/`, for example `assets/images/red_fox.png`.
2. Open `content/image_guess.xml`. Copy a complete existing `<item> ... </item>` block, including `<choices>`, before the final `</content>`.
3. Set a new item ID, locale and status. Change all ten choice IDs to use the new prefix.
4. Edit these fields:

| Field | Purpose / example |
|---|---|
| `theme` | Broad topic: `Animals` |
| `specification` | More specific topic: `Forest mammals` |
| `prompt` | `Which animal is shown?` |
| `asset_key` | A stable descriptive key, such as `red_fox_0101` |
| `asset_ref` | The packaged filename: `assets/images/red_fox.png` |
| `difficulty` | Your editorial difficulty label, such as `standard` |
| `explanation` | Why the correct answer is correct |
| `source_url`, `license`, `attribution` | Actual source and permission to use the picture |

5. Supply exactly **10 choices**. Set each choice's label, unique ID, `correct="true"` or `correct="false"`, and `points` from 0 to 250. Update the legacy `option_N`, `correct_N` (1/0) and `points_N` fields to match. The runtime answer definitions come from the `<choices>` nodes.
6. Keep `selection_count` at **4**, `time_limit_seconds` at **30** and `scoring_policy` at **correct_only**. Players select four candidates; correct selections earn their assigned points and wrong selections earn zero. Four selections do not require exactly four correct choices. Use zero points on wrong choices for clarity.
7. Add French and Arabic records with unique IDs, translating the topic, subtopic, prompt, choices and explanation. They may share the same image.
8. Mark reviewed records `APPROVED`, validate and rebuild. Test in **Games → Offline → Guess the image**. Content is sampled, so your new picture may not appear first.

Example of one choice, within a complete ten-choice item:

```xml
<choice id="IMG_EN_ANIMALS_0101_O1" correct="true" points="10">Red fox</choice>
```

## Questions

1. Open `content/question_round.xml` and copy a complete item, including `<options>` and `<acceptedAnswers>`.
2. Give the item and all five options new IDs.
3. Set `theme` to the topic, `difficulty` to your difficulty label, `question` to the prompt, and `explanation` to the explanation. Update `source_note` with the fact source.
4. Write exactly **five options**, with exactly **one** `correct="true"`. Update the corresponding `option_1` through `option_5` fields. `correct_option_index` is zero-based: 0 means the first option; 2 means the third.
5. Keep `time_limit_seconds` at **20** and `base_points` at **1** for the existing question game. Bluetooth has separate 30-second / 10-point friendly rules.
6. Under `<acceptedAnswers>`, add the correct answer and accepted spelling variants for stages that accept typed input. Do not add incorrect alternatives.
7. Create `fr` and `ar` translations with unique IDs and a shared `concept_id`. Approve reviewed items and increment `contentVersion`.
8. Validate, rebuild and test in **Games → Offline → Questions**. Gradle generates the language shards and `questions-index.tsv` automatically; do not edit those generated files.

## Puzzles

1. Put your original in `assets/puzzles/`, for example `assets/puzzles/mountain_lake.png`. Use a simple filename without spaces.
2. Use a landscape **3:2** image divisible into **12 columns × 8 rows** of equal square pieces. **1536 × 1024** and **1200 × 800** are valid examples. The cutter preserves pixels and rejects incompatible sizes rather than resizing or cropping them.
3. Install the authoring dependency once:

```powershell
python -m pip install -r tools/asset-requirements.txt
```

4. Cut the original:

```powershell
python tools/puzzle_image_cutter.py assets/puzzles/mountain_lake.png
```

This creates `assets/puzzles/mountain_lake_parts/` containing `mountain_lake_Part_1.png` through `mountain_lake_Part_96.png`, numbered left to right, then top to bottom. Existing exports are not overwritten.

5. Register the puzzle using the new helper, after reviewing art, topics and translations:

```powershell
python tools/add_puzzle_content.py assets/puzzles/mountain_lake.png --id PUZ_MOUNTAIN_LAKE_0101 --theme-en "Mountain lakes" --theme-fr "Lacs de montagne" --theme-ar "بحيرات جبلية" --license "Original owner artwork" --attribution "Your name" --source-url "project://assets/puzzles/mountain_lake.png" --approve
```

Replace the sample rights and attribution with the actual information. Omit `--approve` to save drafts for review first.

6. The helper appends three records, ending in `_EN`, `_FR` and `_AR`, to `content/collaborative_puzzle.xml`. It creates all 96 piece references, exact coordinates, 48 pieces per side and a **180-second** timer. It refuses duplicate IDs and missing tiles. Edit each record's `theme` field to change its topic later. The current puzzle board displays the artwork; a topic field does not create a topic-selection screen.
7. Validate, rebuild and test in **Games → Offline → Puzzle**. The reference image now appears at **25% opacity**, while placed pieces remain fully opaque. This rendering setting is in `PuzzleGame.kt`; the original picture and tile files are unchanged.

## Validate and build

Run from the project root:

```powershell
python tools/content_pipeline.py validate
python tools/content_pipeline.py validate --production
$env:JAVA_HOME = 'C:/Program Files/Android/Android Studio/jbr'
./gradlew.bat testDebugUnitTest assembleDebug --console=plain
```

The production check requires all records to be approved. Ordinary validation can be used while drafts remain in progress. The installable development APK is `app/build/outputs/apk/debug/app-debug.apk`.

## Internet matches

Rebuilding the APK updates bundled offline/Bluetooth content and packaged art. Internet matches also need matching, reviewed server records. This UI/Bluetooth update does not change production content.

1. Choose the exact new IDs to publish. Keep existing published IDs immutable.
2. Create an export containing **only the new IDs**. The generic export command exports the whole bank and must not be replayed against an already populated server. Save this example as `.local/export_new_content.py`, replace the example IDs, then run `python .local/export_new_content.py` from the project root:

```python
from pathlib import Path
import sys
import xml.etree.ElementTree as ET
sys.path.insert(0, 'tools')
from content_pipeline import export_approved, KINDS
wanted = {'IMG_EN_ANIMALS_0101', 'IMG_FR_ANIMALS_0101', 'IMG_AR_ANIMALS_0101'}
all_ids = {item.get('id') for kind in KINDS
           for item in ET.parse(Path('content') / (kind + '.xml')).getroot()}
assert wanted <= all_ids, 'A requested new ID is missing'
export_approved(Path('.local/new-content.sql'), exclude_ids=all_ids-wanted)
```

3. Review `.local/new-content.sql` and apply it through the project's authorized database migration process. Correct answers remain in `private.content_answers`, separate from public display content. ID conflicts intentionally fail rather than overwrite existing matches.
4. Distribute the APK containing the new images before enabling their server records. Art references currently point to packaged files; older APKs do not receive new images from a database update alone.
5. Test each supported language. Topic fields describe content; they do not add a new game mode or topic selector automatically.
