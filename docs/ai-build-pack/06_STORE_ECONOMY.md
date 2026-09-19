# 06 --- Store, Currencies, Inventory & Cosmetics

## Objective

Implement the three-currency economy and cosmetic inventory without
client-authoritative balances.

## Currencies

-   Gold: purchasable or earned through achievements/dailies.
-   Gems: purchasable only via real-money IAP.
-   Flames: never purchasable; earned only by winning 1v1, Duo, or
    Squad.

## Flame Vault

Flames unlock a separate store tab containing Legendary cosmetics: -
avatars - frames - banners

Do not allow Gold/Gems to purchase Flame Vault items.

## Start Game loadout

Before a match, the player selects exactly 2 power-ups/boosts to bring
into the match.

## Backend rules

Currency balances are ledger-derived or server-authoritatively mutated.
Every transaction should include: - transaction ID; - user ID; -
currency; - delta; - reason; - source event; - created timestamp; -
idempotency key.

Never let the client send `balance = 5000`.

## IAP

Integrate Google Play Billing for Gems only after the non-IAP economy
works. Use server-side purchase verification before crediting Gems.

## Deliverables

Store UI, item details, purchase confirmation, inventory, loadout
selection, currency ledger, and Flame Vault.

## Verification

Test duplicate purchase requests, reconnect after purchase, insufficient
funds, invalid item IDs, and attempting to buy Flame-only cosmetics with
other currencies.

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
