# Release readiness

No release or backend deployment has been performed. Debug verification and an
unsigned compilation are not Google Play release approval.

## Local engineering checks

- Run `python -m unittest discover -s tools -p test_content_pipeline.py`.
- Run `npm ci --prefix tools/backend-tests` and `npm test --prefix tools/backend-tests`.
- Run Gradle `testDebugUnitTest lintDebug assembleDebug`.
- Run `connectedDebugAndroidTest` on the 1080 x 2340 portrait emulator and an API 26 device.
- Run optimized `assembleRelease` and test the signed candidate on physical devices.
- Inspect English/French/Arabic, RTL, long text, large fonts, dark/light themes,
  image zoom, all game HUDs, reactions, result screens and TalkBack.
- Exercise real Supabase Auth/PKCE and simultaneous 40-player Realtime sessions,
  network loss, background/resume, duplicate requests and expired sessions.

## Required owner/environment inputs

- Resolve Solo schedule, Image Guess scoring, power-up effects/grants, disconnect
  forfeits, and any remaining production content rules in the decision register.
- Approve sourced/localized trivia, images/licenses, scramble scores, precision
  settings and catalog labels. Run `python tools/content_pipeline.py validate --production`.
  DEV_SAMPLE files intentionally fail this gate.
- Supply a non-production Supabase project first, using the HTTPS URL and public
  client key. Configure verified email and Google/Discord providers and exact
  `brainybrawl://auth/callback` redirects. Apply migrations only with authorization.
- Configure a server-only scheduler for `advance_due_matches()` and
  `close_abandoned_rooms()`. Validate RLS with real identities before promotion.
- Resolve account-deletion retention policy and verify its email challenge and
  irreversible cleanup against a disposable account before release.
- Approve privacy/data-safety declarations, age rating, moderation operations,
  production analytics/crash retention, store screenshots and marketing copy.
- Provide `PRIVACY_POLICY_URL` and `TERMS_URL` as owner-approved HTTPS pages.
- Provide signing environment variables `BRAWL_KEYSTORE_PATH`,
  `BRAWL_KEYSTORE_PASSWORD`, `BRAWL_KEY_ALIAS`, `BRAWL_KEY_PASSWORD` outside Git.
  Without them, release builds remain unsigned. No debug signing fallback exists.
- Approve IAP product mapping, server Google Play verification and refund policy
  before enabling Gem purchases. Flames remain skill-earned only.
- Explicitly approve production Supabase deployment and Google Play submission.

## Current verification limits

PGlite proves local SQL/RLS behavior, not network concurrency or hosted provider
configuration. Emulator tests use development/offline fixtures; they do not
fabricate an authenticated competitive session. No production content, billing
catalog, credentials, legal policy or signing material is supplied by this build.

Password recovery also requires the exact allowed redirect
`brainybrawl://auth/callback?flow=recovery`; a successfully exchanged PKCE code
then opens the password form. The flow parameter only controls navigation and
does not bypass code/session verification.
