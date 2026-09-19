# Account and game-navigation update — 1.0.3

## Account behavior

One registration/sign-in flow now supports online and offline use. There is no device-only account checkbox. Creating an account offline creates the private local identity immediately, then connects it to Supabase when validated internet returns. A successful email/password server sign-in also stores an offline password verifier, preserving an existing matching local identity and its practice scores/social queue. Only a real Supabase session enables competitive RPCs; local IDs never substitute for authenticated server users.

Pending registration/sign-in credentials are stored in a separate Android Keystore AES-GCM encrypted envelope, bound to the current local account, excluded from backups and redacted from diagnostic string conversion. Connection attempts reject and delete envelopes older than seven days or belonging to another account. Successful connection, callback or logout erases the envelope. The regular local account store retains only a salted PBKDF2 password verifier. Authentication, reconnect, password changes and logout are serialized. Password changes for a configured backend require a server connection and update the local verifier only after server success, so the passwords cannot diverge. Transient network failures retry when connectivity returns; bad credentials and confirmation requirements do not trigger repeated sign-ups.

Older device accounts must re-enter their password once to connect: their existing one-way password hash cannot authenticate with Supabase. Registration, login and connection screens explain confirmation/network/reauthentication states instead of saying a signed-in player must create a different kind of account. The pending room action resumes after a successful server connection. Connecting from Profile returns to the player card.

### Current hosted requirements

Read-only inspection of the project's public Auth settings found email sign-up enabled, email confirmation required, anonymous authentication disabled, and Google/Discord disabled. This update removes app-side account-type restrictions but does not bypass email verification, RLS, bans, room capacity or server availability. Internet access alone cannot establish a verified server identity. Google/Discord remain deferred under the owner's earlier instruction; their compact buttons have an explanatory disabled state. No production database or Auth settings were changed for this revision.

The Supabase email registration behavior follows the [official Kotlin Auth documentation](https://supabase.com/docs/reference/kotlin/auth-signup). A newly created unconfirmed account can practice immediately and connect after completing the server's confirmation step.

## UI and navigation

- Compact native Google and Discord logo/name buttons replace the long provider tiles. Recover-password text uses readable theme colors on an opaque surface.
- The language menu measures its trigger width and uses an opaque theme surface. English/French/Arabic flags and RTL behavior remain.
- Each online mode opens its own action page, with **Create private room** and **Join a room**. The existing server-authoritative matchmaking/join RPCs are unchanged.
- Offline has its own mode card and opens three choices: Question Round, Image Guess and Puzzle. No game launches merely by opening this chooser.
- Profile now has a framed avatar/player card, username and gold level badge, an inset email field, sign-in method chips, and paired statistic cards. Uploaded photos still use the system picker and private storage. No sample avatar inventory or rewards were fabricated.
- The selected background remains shared across all destinations at 75% opacity. Controls retain readable themed surfaces.

## Offline Puzzle

The new independent practice mode selects an approved localized raster-grid puzzle from the content repository. The player owns all 96 pieces, can rotate and place them, and has 180 seconds. The board retains the 12×8 grid and 60%-opacity full-image guide. A correct distinct piece earns one practice point, up to 96; incorrect positions/rotations, duplicate placements and late submissions earn none. Completion or timeout records the per-account puzzle best once. Backgrounding does not extend the monotonic deadline. No competitive Flames or server scores are awarded.

## Verification

JVM tests exercise offline account promotion, cached verified sign-in, legacy reauthentication, email-confirmation enforcement, secret cleanup/owner/expiry checks and puzzle scoring/deadlines. Instrumentation exercises encryption across storage recreation, mode subpages, the three offline choices, the puzzle board, provider controls, both auth themes and the measured language menu. Current final counts/build results are recorded in `SESSION_REPORT.md` after verification completes.

Unchanged release gates remain in `RELEASE_CHECKLIST.md`, including production signing/legal pages, provider setup, hosted multi-device reliability and physical-device testing.
