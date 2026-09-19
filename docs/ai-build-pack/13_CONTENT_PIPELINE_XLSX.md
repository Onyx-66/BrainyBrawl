# 13 --- Content Pipeline: XML Per Game/Mini-Game

## Runtime/source format

The project uses **individual XML files**, one per game/mini-game
content type.

The previous Excel workbook is reference/authoring/migration input only.
It is **not** an Android runtime format.

## Required repository content directory

``` text
content/
├── question_round.xml
├── image_guess.xml
├── collaborative_puzzle.xml
├── precision_tap.xml
├── roll_the_dice.xml
├── word_scramble.xml
├── speed_sort.xml
└── reactions.xml
```

Do not create one giant XML containing every game.

## XML requirements

All files must: - be UTF-8; - declare schema/content version; - use
stable IDs; - contain locale metadata; - contain explicit approval
status; - contain no secrets or executable code; - reference
images/audio using stable asset references; - contain
content/configuration, not authoritative online match state.

Use `DEV_SAMPLE` for seed records unless genuine production approval
metadata exists. Never fabricate licensing/attribution claims.

### `question_round.xml`

Stable ID, theme/category, question, exactly five options, exactly one
correct option, optional explanation, difficulty, locale, approval
status.

### `image_guess.xml`

Stable ID, image asset reference, theme, specification, exactly ten
choices, hidden point values, exactly four selections allowed,
explanation, difficulty, approval/source metadata.

### `collaborative_puzzle.xml`

Stable ID, 12×8/96 conceptual pieces, image asset reference,
irregular/variable block metadata where required, placement/orientation
data, validation metadata.

### `precision_tap.xml`

Stable ID, target/hot-zone parameters, timing/difficulty profile, streak
configuration, approved variants.

### `roll_the_dice.xml`

Stable ID, 20-slot roulette/wheel configuration, labels/assets,
authoritative resolution metadata.

### `word_scramble.xml`

Stable ID, source word/phrase, category/theme, accepted answer, optional
aliases, difficulty, locale, approval status.

### `speed_sort.xml`

Stable ID, item label, category, asset reference where needed,
difficulty, locale, approval status.

### `reactions.xml`

Stable ID, category, localization key, trigger context,
moderation/approval status.

## Validation

Implement deterministic validation and run it in CI. Fail on malformed
XML, duplicate IDs, missing required fields, wrong answer counts,
multiple/no correct answers, invalid puzzle piece counts, broken
references, unsupported locales, invalid placeholders, missing approval
metadata, or incompatible schema versions.

## Android architecture

``` text
content/*.xml
   ↓
validator
   ↓
ContentRepository
   ↓
domain/use cases
   ↓
ViewModel
   ↓
Compose
```

Composables must not parse XML.

Online scoring/match state remains server-authoritative.

## Migration

If useful, convert the existing reference workbook into the individual
XML files while preserving stable IDs. Do not add an Excel parser to the
Android runtime.

## Definition of done

Every required XML exists, each content type has its own file,
validation is automated, IDs are stable, and no XLSX runtime dependency
exists.
