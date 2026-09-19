# 14 --- Localization & Arabic RTL

## Objective

Implement English, French, and Arabic without changing game code when
translations are added.

## Languages

-   `en`
-   `fr`
-   `ar`

English is the reference/master. French and Arabic must have identical
keys.

## Rules

-   Every user-facing string uses a localization key.
-   Dynamic values use named placeholders.
-   Translators may reposition placeholders but must not rename them.
-   No UI string is hard-coded in Kotlin.
-   Content database text must also be locale-aware.

## Arabic

Treat RTL as an engineering requirement: - mirror horizontal layout
where appropriate; - validate button order; - validate HUD alignment; -
validate chat/reaction placement; - use Arabic-capable font/text
shaping; - test mixed Arabic + numbers + placeholders; - do not mirror
icons whose meaning should remain directional.

## Text expansion

Test long French and Arabic strings. Do not solve overflow by truncating
important gameplay text.

## Deliverables

Localization manager/provider, resource/key strategy, RTL utilities,
language switcher, and sample translations.

## Verification

Test every major screen in all three languages and both themes. Test the
Image Viewer, game HUD, answer options, leaderboards, store, and
quick-chat.

## Mandatory visual reference

Use `mockups/UI_MASTER_REFERENCE.png` as the visual target for the
finished Brainy Brawl UI.

Reference frame: Samsung Galaxy A56 style, 1080 × 2340 px, 20:9
portrait.

Match the reference's: - deep navy background and dark blue surfaces; -
saturated purple/blue/cyan accents; - green positive/ready/correct
actions; - red destructive/wrong/cancel actions; - yellow/gold reward
and Flame emphasis; - rounded cards/buttons; - bold friendly
typography; - top resource area; - bottom navigation patterns; -
player/team cards; - gameplay HUD; - timers/progress; - results/reward
states; - compact mobile-game spacing and hierarchy.

The mockup is a visual reference, not permission to invent gameplay
rules or copy illustrative names, prices, questions, scores, or other
sample values into production.

If written GDD requirements conflict with the mockup, written
product/gameplay behavior wins; preserve the mockup's visual language
around that behavior.

For UI work, capture/check major screens at 1080 × 2340 and correct
accidental visual drift. Do not let generic Material 3 styling replace
the Brainy Brawl visual identity.

## Codex single-session rule

This documentation is designed for one Codex session. `PROMPTS.md` is an
ordered implementation plan, not a set of separate agent handoffs.
Execute phases continuously unless a genuine human approval gate is
reached.

## XML content rule

Runtime/source content belongs in individual XML files under the
repository-root `content/` directory, one file per game/mini-game type.
The reference workbook is not a runtime dependency.
