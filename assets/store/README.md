# Editable store catalogs

The app reads these files directly from the packaged assets:

```text
store/
  currencies/
    coins/offers.json
    gems/offers.json
    flames/offers.json
  cosmetics/
    avatars/offers.json
    frames/offers.json
```

Each file is a JSON array. Each offer contains:

| Field | Meaning |
|---|---|
| `id` | Stable unique offer ID within the file |
| `name` | Object with `en`, `fr`, `ar` display names |
| `description` | Optional localized text; empty strings are hidden |
| `asset` | Packaged art path, such as `assets/icons/coin_icon.png` |
| `quantity` | Positive amount of currency or number of cosmetics |
| `price` | Non-negative numeric price |
| `currency` | `USD` for the draft cash packs; `gold` for coin-priced cosmetics |
| `enabled` | Whether the catalog entry is available; see purchase limitations below |
| `index` | Cosmetics only: existing numbered avatar/frame index |

To edit an offer, change its entry, keep its ID stable, validate JSON, then rebuild the APK. Add/remove entries to change the offer list. Art stays in its existing asset folder so it is not duplicated. Translate names and any description you want shown in each language. For a new numbered cosmetic, also extend the supported appearance catalog and server validation; changing the JSON alone does not register a new avatar/frame index.

The initial currency prices are **editable merchandising drafts in USD**, not live Google Play products. Currency checkout stays disabled until Play Billing and server receipt verification are configured. Setting `enabled=true` does not enable money collection. Currently included avatars/frames are free (`price=0`) and can be equipped. Setting a cosmetic price above zero does not safely create a paid entitlement; configure its authoritative server offer and purchase flow first. Existing server-managed paid cosmetics are displayed separately within the same Cosmetics tab.

Client JSON never grants paid currency or bypasses server ownership checks. Server catalog changes require the usual reviewed backend update; rebuilding the APK changes its bundled display catalog.
