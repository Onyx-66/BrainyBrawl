# Accounts and online setup

## Deployed project — September 19, 2026

The owner identified the supplied project as shared/production and explicitly approved the original setup and the follow-up 028–029/content changes. All 29 migrations and 3,855 approved content records are deployed. The requested Mr.onyx Auth account exists with private server administrator membership. Hosted email/password login, profile/level/admin status, store, leaderboard, anonymous denial and service-only restrictions were verified. The Android app also signed into this real account and opened Store and Leaderboards successfully.

Public URL/publishable key are read from ignored `.env` into the normal build. Secret API key, database password and administrator password are read only by local tools and must never enter the APK/Git/ZIP. Device accounts are separate identities: signing into a device account does not grant a server session or competitive authority.

Google and Discord are deliberately deferred at the owner's request. Their tiles are disabled with an explanatory label. Later, configure provider client IDs/secrets in Supabase, allow `brainybrawl://auth/callback`, test PKCE, and set the matching `GOOGLE_AUTH_ENABLED=true` / `DISCORD_AUTH_ENABLED=true` public build flags before rebuilding. Provider secrets stay in Supabase. Password recovery also needs `brainybrawl://auth/callback?flow=recovery` and working email delivery; that hosted callback/SMTP flow has not been verified.

## Repeatable backend tooling

- `python tools/prepare_backend.py --incremental` reads existing records, rejects changed published IDs and exports only new approved content to `.local/backend/approved-content.sql`. The schema bundle and checksums are reviewable there.
- `python tools/deploy_backend.py` defaults to no changes. After explicit approval of the target and scope, use `--apply --content --confirmed-project <project-ref>`. Each migration is transactional/checksummed. Content imports use bounded writes within one transaction and an import digest.
- `python tools/bootstrap_admin.py --apply` provisions the configured administrator; it preserves an existing account password. Client administrator membership never grants a service credential or unrestricted writes.
- `python tools/verify_backend.py` verifies the hosted API using a temporary session and signs out only that session.

Use a direct/session PostgreSQL connection on port 5432 for migrations, not the transaction-mode port 6543. Install the pinned local driver with `python -m pip install --target .local/python -r tools/backend-requirements.txt`. The driver loads the official Supabase Root 2021 CA via `DATABASE_SSL_CA_FILE`, preserving certificate-chain and hostname verification. Python's legacy CA-extension compatibility permits that root's older extension format; system trust and server SSL settings are unchanged. The public CA URL is published in [Supabase Studio configuration](https://github.com/supabase/supabase/blob/master/apps/studio/hooks/custom-content/custom-content.json).

## Runtime and operational limits

Android uses authenticated PostgREST RPCs directly; SQL owns authorization, clocks, scores, rewards and economy. The optional Edge gateway is implemented/tested locally but is not required by this client and was not deployed. Realtime publication contains rooms, matches, invitations and reactions. Polling recovers missed events; match snapshots advance overdue rounds on reconnect. A trusted background scheduler for `advance_due_matches()` and `close_abandoned_rooms()` is not installed; unattended advancement while every player is disconnected needs that operational setup. No extension or scheduled task was silently added to the shared project.

Online matches permit zero boosts until effects/starter grants are defined. Two chosen boosts must still be distinct, owned and approved. The commercial catalog is empty by design; no prices, IAP products or fake currency grants were invented.

Device accounts use Android Keystore encryption and salted PBKDF2 verification, with account-scoped offline statistics/photos. Offline friend/invitation requests are queued locally, and require explicit delivery after signing into a matching online email. Accepted friendship and room authorization remain server checks; the UI does not claim network delivery while offline.

Levels start at one and increase once per ten lifetime earned Flames. Spending never reduces level. Offline practice never mints Flames. Profile photos use private per-user Storage paths and bounded, metadata-stripped 512-pixel JPEGs. Local account deletion clears its identity/photo/practice/queue. Online deletion records a self-only idempotent request; retention policy, destructive processing and a public request page remain release gates.

Further sources: [API keys](https://supabase.com/docs/guides/getting-started/api-keys), [Storage access control](https://supabase.com/docs/guides/storage/security/access-control).
