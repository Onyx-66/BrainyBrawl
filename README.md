# Brainy Brawl AI Build Pack v4 --- Codex

Use `docs/ai-build-pack/CODEX_MASTER_PROMPT.md` as the **single
copy/paste prompt** for Codex (GPT-6 ASTRA).

Codex reads and executes `docs/ai-build-pack/PROMPTS.md` as one
continuous build plan against the supplied Android project root.

Visual target: `mockups/UI_MASTER_REFERENCE.png`.

Runtime/source content: individual XML files under the project's
`content/` directory, one file per game/mini-game content type. The
previous XLSX is reference/migration input only.

Final Codex artifact: `BrainyBrawl_CHANGED_FILES.zip`, containing only
changed/new files plus `CHANGED_FILES_MANIFEST.md`.
