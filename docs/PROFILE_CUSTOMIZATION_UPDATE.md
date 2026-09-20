# Profile and appearance update — 1.0.4

## Player card

The redundant Player card heading and gallery upload button are removed. Avatar and frame share one square canvas in the header, player card and picker. The card contains username, permanent player number, level and Gold/Gems/Flames as compact icon/value chips, plus Friends with the accepted-friend count. Avatars and Frames sit side by side and each opens twelve selectable slots. Long balances use compact visual notation while accessibility descriptions retain the exact number.

The old device-account explanation is removed from Profile. Successful and queued appearance-save feedback clears after three seconds. The selected scene still appears behind all pages.

## Account controls

Account contains an Email row and edit icon, linked provider rows, and buttons for available unlinked Google/Discord identities. Provider rows use the actual identity display/email data when available. Account linking calls Supabase linkIdentity, not a separate sign-in; it cannot silently replace the current account. Google/Discord remain disabled under the owner's existing deferral and will require provider setup plus manual identity linking enabled in Supabase.

Email edits use Supabase updateUser and its verification flow. The previous email stays active until the server confirms the change. A confirmed change updates the matching local verifier record without discarding the player's local identity or practice data. Password changes and account-deletion controls share a row, with a separate icon/sign-out control below. Local deletion removes local appearance/cache; online deletion still records the existing deletion request rather than claiming irreversible cleanup. No automatic deletion or verification bypass was added.

## Artwork handoff

Add square PNGs to these existing repository folders:

- `assets/avatars/avatar_01.png` through `avatar_12.png`
- `assets/frames/frame_01.png` through `frame_12.png`

Each folder has a README with dimensions and naming. Recommended size is 1024×1024, at most 4096×4096 and 8 MB each. Frames require a transparent central opening (the avatar occupies the inner 83% of the square canvas). Rebuild after adding files; no code changes are needed. The current folders have no final pack, so all twelve slots render native square preview artwork. These are clearly documented placeholders, not a claim that the final twelve designed portraits were supplied. The older reference pack contains only eight avatars/three frames and was not substituted for the requested twelve-item pack.

Run `python tools/validate_appearance_assets.py`; add `--strict` when all 24 final files are present. Runtime decoding is bounded and missing/invalid art uses a safe square fallback.

## Server persistence and privacy

Selections are account-scoped and queued offline. The selected avatar is encoded as a bounded 512×512 JPEG and uploaded to the existing private profile-photos bucket, followed by the existing set_profile_photo RPC. Avatar/frame slot IDs are stored in the signed-in user's brawl_appearance Auth metadata. Frame artwork is bundled rather than duplicated for each user. A fresh sign-in restores the selection; offline-created choices follow the matching account when it connects.

Only slots 1–12 can be selected. These free baseline appearance preferences grant no inventory entitlements, currency, scores, Flames or roles. Existing premium inventory and server ownership checks remain authoritative. HTTP calls pin the initiating session's JWT, preventing a concurrent sign-out/account switch from changing the target identity. Failed uploads stay pending, and delayed responses cannot overwrite another local user's selection. Private credentials are not embedded. No new schema migration, RLS change or Auth configuration deployment is required.

The old photo sanitizer remains covered for legacy cache compatibility, but no gallery/file chooser appears in the application UI.

## Icons

Navigation and action symbols use native Android vectors from Lucide, one of the libraries featured in the owner's [Morphicons reference](https://www.morphicons.com/). This Compose app does not embed its JavaScript runtime. Google/Discord brand marks and supplied game/currency artwork retain their separate purpose. Source URLs/hashes are recorded in assets/icons/LUCIDE_SOURCES.json; Lucide's ISC/MIT notices ship in app/src/main/assets/licenses/Lucide-LICENSE.txt.

## Verification

Appearance tests cover all slot/path bounds, malicious metadata, square/transparent geometry, bounded JPEG output, offline persistence, retries, server restoration, account switching and local-to-online adoption. Auth tests verify confirmed-only email cache updates and existing-account linking. UI tests cover twelve-item selectors, three-second notices, the grouped account controls, deletion cancellation, Arabic layout and removed gallery controls. Final counts and artifact hashes are recorded in SESSION_REPORT.md after the build and emulator pass.

[Supabase linking](https://supabase.com/docs/reference/kotlin/auth-linkidentity) and [verified email updates](https://supabase.com/docs/reference/kotlin/auth-updateuser) document the external provider/email requirements. The broader Play release gates remain in RELEASE_CHECKLIST.md.

Final verification: 76 JVM tests, 36 emulator tests and 24 Python tests passed. Seven appearance/profile checks and three final dialog checks passed after visual adjustments. Lint has zero errors and 68 warnings. APK 1.0.4 was installed in place; a real app Save and independent read-only hosted inspection confirmed avatar 1/frame 1 and a private 9,610-byte JPEG. No schema/provider changes were deployed. The final picker shows every slot label completely, and the dialogs use the app's navy surface.
