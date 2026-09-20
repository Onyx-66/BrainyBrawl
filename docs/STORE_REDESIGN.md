# Store redesign — 1.0.5

Owner request: glossy supplied artwork, three currency subsections and six packs per currency. This supersedes the earlier design prohibition on offering Flames for purchase. No production economy migration or payment rollout is included here.

The store offers browsable Gems / Coins / Flames sections, localized in English, French and Arabic, with two-column cards. Coins corresponds to the existing backend gold currency. Cosmetic catalog and Flame Vault purchases retain their existing server-authoritative, idempotent RPC flow. Offline users may browse currency packs.

Draft quantities: Gems 80/250/550/1200/2600/5500; Coins 500/1500/3500/8000/18000/40000; Flames 5/15/35/80/180/400. These are merchandising proposals, not published products. No prices or discounts are fabricated. All eighteen checkout buttons say Coming soon and are disabled. No payment SDK, product IDs, receipt verifier or granting endpoint is configured. Activating these packs requires Play products, localized prices and server-side verified, idempotent grants. Purchased Flames must be separated from earned competitive progression before activation; current scores, rank and level rules are unchanged.

Supplied Desktop attachment originals were copied unchanged into assets/ui because they were not present in the repository asset folder. Glossy store and resource art is used in the storefront; home/game/store/profile art replaces the four navigation glyphs. Other supplied card backplates are retained for subsequent screen redesign. User-added assets/avatars/avatar_01.jpg is left untouched.

Verification: debug and release builds, JVM tests, lint, emulator browsing and disabled-checkout assertions, and a rendered store screenshot. APK remains development signed; this is not a Play billing or production release.

Final results: 76 JVM tests passed; 37 emulator tests exercised (36 passed in full run, obsolete offline-store-gate expectation updated and its test passed on rerun); 0 lint errors, 69 warnings. APK signature verification and 16 KB ZIP alignment passed. Screenshot: .local/store-offers.png. Installable build: deliverables/BrainyBrawl-1.0.5.apk.
