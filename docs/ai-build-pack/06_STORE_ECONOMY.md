# 06 — Store, Currencies, Inventory & Cosmetics

## Objective
Implement the three-currency economy and cosmetic inventory without client-authoritative balances.

## Currencies
- Gold: purchasable or earned through achievements/dailies.
- Gems: purchasable only via real-money IAP.
- Flames: never purchasable; earned only by winning 1v1, Duo, or Squad.

## Flame Vault
Flames unlock a separate store tab containing Legendary cosmetics:
- avatars
- frames
- banners

Do not allow Gold/Gems to purchase Flame Vault items.

## Start Game loadout
Before a match, the player selects exactly 2 power-ups/boosts to bring into the match.

## Backend rules
Currency balances are ledger-derived or server-authoritatively mutated.
Every transaction should include:
- transaction ID;
- user ID;
- currency;
- delta;
- reason;
- source event;
- created timestamp;
- idempotency key.

Never let the client send `balance = 5000`.

## IAP
Integrate Google Play Billing for Gems only after the non-IAP economy works.
Use server-side purchase verification before crediting Gems.

## Deliverables
Store UI, item details, purchase confirmation, inventory, loadout selection, currency ledger, and Flame Vault.

## Verification
Test duplicate purchase requests, reconnect after purchase, insufficient funds, invalid item IDs, and attempting to buy Flame-only cosmetics with other currencies.
