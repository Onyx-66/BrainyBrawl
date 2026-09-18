# 14 — Localization & Arabic RTL

## Objective
Implement English, French, and Arabic without changing game code when translations are added.

## Languages
- `en`
- `fr`
- `ar`

English is the reference/master.
French and Arabic must have identical keys.

## Rules
- Every user-facing string uses a localization key.
- Dynamic values use named placeholders.
- Translators may reposition placeholders but must not rename them.
- No UI string is hard-coded in Kotlin.
- Content database text must also be locale-aware.

## Arabic
Treat RTL as an engineering requirement:
- mirror horizontal layout where appropriate;
- validate button order;
- validate HUD alignment;
- validate chat/reaction placement;
- use Arabic-capable font/text shaping;
- test mixed Arabic + numbers + placeholders;
- do not mirror icons whose meaning should remain directional.

## Text expansion
Test long French and Arabic strings.
Do not solve overflow by truncating important gameplay text.

## Deliverables
Localization manager/provider, resource/key strategy, RTL utilities, language switcher, and sample translations.

## Verification
Test every major screen in all three languages and both themes. Test the Image Viewer, game HUD, answer options, leaderboards, store, and quick-chat.
