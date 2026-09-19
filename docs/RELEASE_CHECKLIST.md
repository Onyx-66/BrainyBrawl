# Release readiness

The owner-authorized Supabase schema/content/admin setup is deployed. Google Play submission has not occurred. Debug verification and an unsigned bundle do not constitute release approval.

## Passed engineering checks

- 63 JVM tests, 27 API 37 emulator instrumentation tests, 24 Python tests, 41 PostgreSQL/PGlite verification groups and Edge request-body tests.
- Debug assembly, debug lint (zero errors; 39 warnings), optimized unsigned release APK 1.0.2; unsigned AAB was verified on the preceding revision.
- APK ZIP alignment and all bundled native ELF load segments meet 16 KB alignment. A physical 16 KB device run is still outstanding.
- Eight production XML packs: 3,861 approved records, including 1,225 question concepts in each of English/French/Arabic. Cross-language aliases and independent numeracy checks pass.
- Real hosted email sign-in, account/level/admin status, Store and Leaderboards, RLS and service-only RPC restrictions. Actual Android Store/Leaderboard navigation works while signed in.
- Dark/light auth surfaces, bento modes/actions, local/online profile flow, Arabic RTL/Tajawal, French, immediate offline answer controls, 45-second questions, 30-second offline images and isolated SQL-fixture multiplayer HUDs.

## Required release gates

- Provide owner-approved `PRIVACY_POLICY_URL`, `TERMS_URL`, and `ACCOUNT_DELETION_URL` HTTPS pages. Define retention and implement/operate online account-deletion processing; the in-app request queue alone does not complete deletion.
- Supply signing environment settings `BRAWL_KEYSTORE_PATH`, `BRAWL_KEYSTORE_PASSWORD`, `BRAWL_KEY_ALIAS`, `BRAWL_KEY_PASSWORD` outside Git. No debug-signing fallback is used for release.
- Install an authorized trusted scheduler for `advance_due_matches()` and `close_abandoned_rooms()`. Current snapshots recover deadlines when a player returns; no hosted scheduler is installed.
- Run real simultaneous-device matches through all phases, hosted Realtime reconnect/partition/load tests, API 26/physical Galaxy A56 checks, TalkBack and measured frame/memory testing. Local SQL tests do not model concurrent network connections.
- Verify email confirmation/recovery delivery and deep links. Google/Discord are deferred by the owner and disabled in this build; enable their build flags only after provider setup/testing.
- Approve moderation operations, data-safety declarations, privacy/retention policy, 13+ content rating/target audience, analytics/crash policy, store screenshots and marketing copy.
- Define commercial catalog/prices, boost effects/grants, achievements/daily awards and any ads/Battle Pass/IAP rules before enabling those services. Purchasable Flames are prohibited. The empty store is intentional.
- Define draft-chooser disconnect/forfeit policy; current persisted chooser assignment waits for reconnect. Solo and Image Guess scoring decisions are now resolved and implemented.
- Explicitly approve the final signed Play submission after these gates pass.

Run `python tools/release_preflight.py`; it currently blocks legal/deletion URLs and signing settings. Content now passes the production gate. No checks were weakened to achieve a pass.

References: [Play target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en), [16 KB support](https://developer.android.com/guide/practices/page-sizes), [account deletion](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en). Target SDK 37 and technical checks do not guarantee Play acceptance.
