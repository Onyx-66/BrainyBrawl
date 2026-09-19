# Reference UI and account-flow correction

## Reported problems and fixes

The supplied screenshot showed the unconfigured account screen hiding every
authentication form/provider, and a persisted light/Arabic setting unlike the
navy reference. The offline loader requested the UI language even though the
bundled development questions are English-only.

- Account always presents email/password, sign-up, recovery and Google/Discord
  actions. Without a configured backend, attempting online authentication shows
  an explicit availability message; it never fabricates a session.
- Home now uses a purple/blue hero with original brain artwork, four colored
  navigation tiles and compact cyan-accented bottom navigation. Mode cards use
  the reference's purple/blue/cyan hierarchy and expand their real room actions.
- Shared buttons use raised gradients; account fields use rounded blue surfaces.
  Poppins typography and original scalable brain/controller/trophy artwork replace
  the generic presentation. Font sources/license are in `ASSET_CREDITS.md`.
- Arabic/French users can explicitly select the available English question pack.
  The Arabic shell remains RTL; English content and options render LTR.
- Offline remains available without authentication. No online match is simulated
  locally, no currency balances are invented, and private-room defaults remain.

## Verification and limitations

`RedesignedNavigationTest` exercises guest home/mode/account navigation, the
provider availability message, sign-up username field and Arabic-to-English
question-pack entry. It captures auth/home/modes/offline screens for inspection
at 1080 x 2340. Screenshots are ignored local QA artifacts, not production assets.
Visual inspection found and corrected a wrapped tile label and content direction.

Real email/Google/Discord login and online gameplay still require a Supabase test
project URL/public key, provider configuration, migrations and approved playable
content/loadouts. No such configuration was supplied during this correction.
No privileged credentials or fake online functionality were added. Existing
product-rule decisions remain in the decision register.

Final production source passes 50 JVM tests, 19 device tests, debug assembly,
optimized unsigned release assembly, and lint with zero errors/34 warnings.
Screenshot capture tests passed again after the visual corrections. The offline
regression additionally checks that five answers appear after the reading timer,
that a selection reveals feedback, and that Next advances the question.
