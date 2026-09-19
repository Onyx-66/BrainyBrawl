# Local accounts, administration and online setup

Device accounts work without a backend: salted PBKDF2 password verifiers and account records are encrypted with Android Keystore. Offline scores are scoped to the account. Device identities cannot submit competitive scores or assign administrative privilege. Clearing app data removes device accounts; there is no fake email recovery.

Friend requests/invitations can be prepared offline. Delivery requires explicitly signing into a real Supabase account with the same email and pressing Send queued requests. The server checks accepted friendship and room membership; failed requests stay pending. This is not offline network delivery or automatic account merging.

The requested administrator credentials are in the ignored root `.env`; they are excluded from the APK, Git and changed-files ZIP. Provisioning the real administrator requires a configured backend. `python tools/bootstrap_admin.py` validates configuration without changes. After approving the target project and applying migrations, `python tools/bootstrap_admin.py --apply` creates the account if missing and assigns its server-only administrator record. Existing account passwords are preserved. No administration UI or unrestricted client write privilege is granted.

## Required configuration

Set `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY` in root `.env`, then rebuild the APK. Only explicitly named public configuration is read by Gradle. Never put a service-role key into the publishable-key field.

Set `SUPABASE_SERVICE_ROLE_KEY` locally only for provisioning tools. Do not send it in chat. Confirm whether the project is disposable development or shared/production before applying migrations. All 24 migrations must be applied in order, along with the existing documented Edge Functions, Realtime publication and trusted content import. Real multiplayer cannot run against configuration placeholders or without approved playable content.

Enable email/password auth in Supabase. Enable Google and Discord providers there with their provider client IDs/secrets and the Supabase callback URL. Add `brainybrawl://auth/callback` to the allowed redirect URLs (verify against `SupabaseAuthRepository.CALLBACK`). Provider secrets stay in Supabase. Test email confirmation and password recovery delivery with the configured SMTP service.

## Levels

Default rule introduced for the requested leveling system: level 1 at zero lifetime earned Flames; gain one level per 10 earned Flames. Only positive `match_win` Flame ledger entries count. Spending never lowers level. This formula is an adjustable product default pending owner confirmation. Offline accounts remain level 1 because offline practice does not mint competitive Flames.
