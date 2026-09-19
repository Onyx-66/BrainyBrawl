You are Codex (GPT-6 ASTRA). You are receiving the CURRENT ROOT FOLDER
of the Brainy Brawl Android project.

Your mission is to implement the complete Brainy Brawl game in ONE
CONTINUOUS SESSION using the supplied repository and the complete
instruction set in `docs/ai-build-pack/`.

DO NOT ask the user to feed you separate prompts. Read and execute
`docs/ai-build-pack/PROMPTS.md` yourself from start to finish.

## FIRST: inspect the repository

Before editing: 1. Inspect the entire repository tree. 2. Inspect Git
status and current branch. 3. Locate `docs/ai-build-pack/`. 4. Read ALL
`.md` instruction files there. 5. Read `PROMPTS.md` completely. 6. Read
`17_DECISION_REGISTER.md`. 7. Read `18_MOCKUPS_REFERENCE.md`. 8. Inspect
`mockups/UI_MASTER_REFERENCE.png`. 9. Inspect the existing Android
project, Gradle files, package/application identity, resources, tests,
and current implementation. 10. Inspect existing content/workbook data
as needed.

Do not assume the project is a clean template. Preserve working code and
integrate with the actual project.

## SOURCE OF TRUTH

Use this hierarchy: 1. GDD-derived written product/gameplay
requirements. 2. Feature instruction `.md` files. 3.
`17_DECISION_REGISTER.md`. 4. `mockups/UI_MASTER_REFERENCE.png` for
visual design. 5. `13_CONTENT_PIPELINE_XLSX.md` for XML content format.
6. `PROMPTS.md` for implementation order.

Written gameplay/product rules beat the mockup if they conflict.

NEVER silently invent unresolved product rules. Record them as
`OPEN_DECISION` and continue with non-dependent work.

## VISUAL TARGET --- MANDATORY

The finished app must visually converge on:

`mockups/UI_MASTER_REFERENCE.png`

Reference frame: - Samsung Galaxy A56-style; - 1080 × 2340 px; - 20:9
portrait.

Match: - deep navy background; - dark blue panels/cards; - saturated
purple/blue/cyan accents; - green positive/ready/correct actions; - red
destructive/wrong/cancel actions; - yellow/gold reward/Flame emphasis; -
rounded cards/buttons; - bold friendly typography; - top resource
area; - bottom navigation; - player/team cards; - gameplay HUD; -
timers/progress; - result/reward screens; - popup/reaction treatment; -
consistent compact spacing/hierarchy.

Do not copy illustrative names, prices, questions, scores, or rewards
from the mockup into production.

Do not let generic Material 3 styling replace the Brainy Brawl visual
identity.

Verify major UI screens at 1080 × 2340 against the master board.

## CONTENT FORMAT --- MANDATORY

The project-root `content/` directory must contain individual XML files:

``` text
content/
  question_round.xml
  image_guess.xml
  collaborative_puzzle.xml
  precision_tap.xml
  roll_the_dice.xml
  word_scramble.xml
  speed_sort.xml
  reactions.xml
```

ONE FILE PER GAME/MINI-GAME CONTENT TYPE.

Do NOT: - create one giant all-games XML; - use XLSX at Android
runtime; - add an Excel parsing dependency to the Android app; -
hard-code production trivia/game content in Kotlin.

If the old workbook contains useful seed data, migrate it to the
individual XML files while preserving stable IDs. The workbook is
reference/migration input only.

XML must be UTF-8, versioned, stable-ID based, validated, deterministic,
and explicit about approval status.

## ENGINEERING + SECURITY

-   Kotlin + Jetpack Compose.
-   Supabase backend.
-   Keep UI/domain/data boundaries clear.
-   Keep game rules independent of Compose.
-   Keep Realtime subscriptions out of Composables.
-   Competitive state, scoring, deadlines, winners, Flames, balances,
    and leaderboard writes are server-authoritative.
-   Never expose a Supabase service-role/privileged key in Android.
-   Never commit secrets.
-   Never trust client-provided competitive results.
-   Default-deny RLS and automated security tests.
-   No free-text chat at launch.
-   User-facing strings use localization keys.
-   Preserve Light/Dark and Arabic RTL.
-   Add loading/empty/error/reconnect states.
-   Never weaken/delete tests to get green.
-   Do not publish/deploy production automatically.
-   Do not make unrelated rewrites.

## EXECUTE THE COMPLETE PLAN

Now read and execute ALL phases in:

`docs/ai-build-pack/PROMPTS.md`

Execute them sequentially in this single session: -
architecture/foundation; - design system; - Supabase
schema/RLS/functions; - XML content pipeline; - authentication; -
home/navigation; - profile/friends/safety; - economy/store; -
lobby/matchmaking/Realtime; - game engine; - all documented
mini-games; - 1v1; - Offline; - Duo; - Squad; - Solo shell without
inventing unresolved schedule; - leaderboards; -
settings/localization/RTL; - reactions; - security; - reliability; -
performance; - visual QA; - content QA; - regression; - release
preparation; - final review.

Do not stop for user approval after each phase.

Stop only for a genuine human approval gate, such as: - a missing
product decision that blocks safe implementation; - destructive
production/shared database operation; - production
deployment/publication; - a real credential/secret that cannot safely be
represented as configuration; - a breaking external contract requiring a
human decision.

Otherwise continue.

After major phases, run the relevant build/tests/lint and fix failures
caused by your work.

## FINAL DELIVERY --- CHANGED FILES ONLY

At the end:

1.  Determine the exact starting repository state before your edits.
2.  Inspect final Git diff/status.
3.  Create `CHANGED_FILES_MANIFEST.md` listing every added, modified,
    and deleted file and why.
4.  Include build/test/lint results, Supabase changes, XML content
    changes, visual QA, security status, and remaining OPEN_DECISIONs.
5.  Create `BrainyBrawl_CHANGED_FILES.zip`.
6.  The ZIP must contain ONLY files that were added or modified relative
    to the starting repository, plus `CHANGED_FILES_MANIFEST.md`.
7.  Preserve each file's path relative to the project root.
8.  Do not include unchanged files.
9.  Do not include `.git/`, `.gradle/`, build outputs, IDE caches,
    `local.properties`, keystores/signing keys, secrets, or
    machine-specific files.
10. Explicitly list deleted files in the manifest.
11. Do not silently omit any changed file.

The user will merge this ZIP back into the original project root.

## FINAL RESPONSE

Return a concise report: - what was implemented; - build/test/lint
results; - Supabase migrations/functions; - XML content files; -
UI/visual QA status; - security status; - remaining
limitations/OPEN_DECISIONs; - exact location of
`BrainyBrawl_CHANGED_FILES.zip`.

Do not claim complete success if critical build/security failures
remain.
