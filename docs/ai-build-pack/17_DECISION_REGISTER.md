# 17 --- Decision Register

This file prevents AI agents from silently inventing product rules.

## Confirmed by GDD

-   Platform: Android/Google Play.
-   Modes: 1v1, Solo Online, Duo, Squad, Offline.
-   Room sizes: 2 / 20 / 40 / 40 / 1.
-   Private-by-default for Solo Online/Duo/Squad.
-   Matchmaking toggle opens empty slots.
-   1v1 public quick match + friend invite.
-   Flames are skill-earned only.
-   Launch chat is preset quick replies/emotes, not free text.
-   English/French/Arabic at launch.
-   Arabic requires RTL engineering.
-   Kit A is the primary UI system.
-   Image Viewer behavior is defined.
-   Exact IAP tiers/Battle Pass/ad earning remain open.
-   Full content sourcing/moderation pipeline remains open.
-   Anti-cheat details remain open, though server-authoritative
    validation is recommended.
-   Disconnect/reconnect behavior remains an engineering item.

## Native-stack decisions made for this build pack

These are implementation choices requested by the user, not statements
from the GDD: - Android Studio. - Kotlin. - Jetpack Compose. - Supabase
Auth/Postgres/Realtime/Storage/Edge Functions. - Google Play Billing for
Gems. - Room/local cache where useful.

## Must not be invented by agents

-   Final Solo Online phase schedule.
-   Exact IAP pricing.
-   Battle Pass rules.
-   Advertising economy.
-   Final production trivia sourcing policy.
-   Legal/licensing status of third-party images.
-   Moderation thresholds.
-   Competitive ranking formulas beyond those explicitly defined.

## Mandatory visual reference

Use `mockups/UI_MASTER_REFERENCE.png` as the visual target for the
finished Brainy Brawl UI.

Reference frame: Samsung Galaxy A56 style, 1080 × 2340 px, 20:9
portrait.

Match the reference's: - deep navy background and dark blue surfaces; -
saturated purple/blue/cyan accents; - green positive/ready/correct
actions; - red destructive/wrong/cancel actions; - yellow/gold reward
and Flame emphasis; - rounded cards/buttons; - bold friendly
typography; - top resource area; - bottom navigation patterns; -
player/team cards; - gameplay HUD; - timers/progress; - results/reward
states; - compact mobile-game spacing and hierarchy.

The mockup is a visual reference, not permission to invent gameplay
rules or copy illustrative names, prices, questions, scores, or other
sample values into production.

If written GDD requirements conflict with the mockup, written
product/gameplay behavior wins; preserve the mockup's visual language
around that behavior.

For UI work, capture/check major screens at 1080 × 2340 and correct
accidental visual drift. Do not let generic Material 3 styling replace
the Brainy Brawl visual identity.

## Codex single-session rule

This documentation is designed for one Codex session. `PROMPTS.md` is an
ordered implementation plan, not a set of separate agent handoffs.
Execute phases continuously unless a genuine human approval gate is
reached.

## XML content rule

Runtime/source content belongs in individual XML files under the
repository-root `content/` directory, one file per game/mini-game type.
The reference workbook is not a runtime dependency.

## User correction ? 2026-09-19

All Duos continue through all phases, including Word Scramble. References to
bottom-four elimination or top-six qualification anywhere in the build pack
or workbook are superseded by this explicit product correction.

## User correction ? Squad draft randomness (2026-09-19)

For each Squad question, the server randomly chooses the theme-picking Squad and
one eligible answering teammate per Squad. The draw is persisted for the question
and cannot change on retry/reconnect. This replaces rank-based Squad draft turns.

## RESOLVED — Duo/Squad draft answers (2026-09-19)

User chose 20 seconds after the documented 15-second selection window. Every
correct team earns +1 through its designated answerer.

## RESOLVED ? Smaller Duo draft rooms (2026-09-19)

The user chose cycling through present ranked teams when a scheduled rank is
absent. Preserve all 15 questions. Map scheduled rank R to
`((R - 1) mod team_count) + 1`; the Player 1 / higher-scoring / Player 2
answerer schedule stays unchanged.


## Remaining OPEN_DECISIONs found during implementation

- IMAGE_SCORE: confirm whether all four selected choices contribute their hidden
  points or only choices marked correct. The reference data assigns positive
  weights to wrong choices. Competitive starts and approved export require an
  explicit policy; Offline Image Guess remains unavailable pending this decision.
- ACCOUNT_DELETION_RETENTION: email-verified deletion is required, but treatment
  of completed match history and purchase/audit records is undefined. A user
  decision is pending on anonymized retention versus erasing the player's records.
- DRAFT_DISCONNECT: chooser timeout/forfeit behavior is unspecified. The server
  preserves the persisted chooser/answerer assignment and waits for reconnect;
  it does not invent forfeits or silently transfer a competitive choice.
- POWER_UPS: two owned loadout items are required, but production effects, catalog
  and initial grants are missing. No fabricated grants or score modifiers exist.
- PRODUCTION_CONTENT: approval, FR/AR content, asset licensing, precise Precision
  Tap tuning and scramble reward values still require approved content records.
- SPEED_SORT_OFFLINE: mini-game documentation mentions Offline use, while the
  Offline mode specification defines Question Round and Image Guess only. The
  reusable sorter exists, but no undocumented Offline schedule is selected.

Solo schedule, monetization tiers/ads/Battle Pass, moderation thresholds, legal
pages and deployment approval remain the existing owner-controlled gates.

- ACHIEVEMENTS_DAILIES: profile achievements and Gold earned through achievements/
  dailies are mentioned in feature 05/06, but no catalog, criteria, cadence or
  reward amounts are defined anywhere in the build pack. Awarding Gold or granting
  achievements requires these rules; no client-authoritative or fabricated grants
  are implemented.


## September 19 account/level follow-up

- Owner requested offline-capable device accounts, queued social invitations, supplied branding, bento modes and username/level header. Implemented separately from Supabase identity; offline practice still cannot award competitive Flames.
- OPEN_DECISION LEVEL_CURVE: owner requested Flame-based levels without thresholds. Current adjustable implementation is level = 1 + floor(lifetime earned match-win Flames / 10), with no decrease after spending. Await owner confirmation of the curve; unrelated features remain available.
- Online activation and server administrator provisioning require target-project settings and approval. Administrator credentials are in ignored `.env`, never in this register or the APK.


## Owner follow-up: localized practice and account photos (2026-09-19)

- Offline Question Round now exposes choices immediately and gives 45 seconds per answer, per the owner's manual-test request. This supersedes the old reading-only stage for offline practice; the previously approved competitive 20-second team answer windows are unchanged.
- The supported languages remain English, French and Arabic. Authored parallel content and explicit answer aliases are accepted across languages, with accent/Arabic-diacritic and Arabic-digit normalization. This does not introduce runtime translation, fuzzy matching, or a client-trusted online score.
- Account photos are optional, selected through the system photo picker. Local accounts store them privately on-device; online accounts upload to owner-restricted private Storage.
- The new question pack has 1,200 original, independently arithmetic-checked numeracy/logic concepts, each in three languages. This is not 3,600 different trivia concepts or approval of the remaining sample mini-game packs.
- Online deletion requests can be recorded now. Retention, irreversible cleanup and a public web request flow remain OPEN_DECISION/release gates.

## Owner resolutions and delegated Solo schedule — 2026-09-19

- Image Guess: only correct selected choices earn their hidden points. Wrong choices earn zero. Four selections and the 30-second deadline remain required.
- Launch boosts: an empty loadout is permitted; a selected loadout must still contain exactly two distinct owned/approved boosts. Effects, starter grants and commercial pricing remain unresolved and are not invented.
- Solo Online: owner delegated the rule decision. The final schedule is recorded in `11_MODES_SQUAD_SOLO.md`: 2–20 players, 20s simultaneous Precision Tap, 90s individual Speed Sort, 15 questions (10s read +20s answer), every correct player +1, no elimination, one winner Flame, server dice for tied leaders. This supersedes the earlier Solo OPEN_DECISION.
- Shared/production setup was explicitly approved for the supplied project. Schema/content/admin deployment is tracked in `../PRODUCTION_DEPLOYMENT_REVIEW.md`; this does not authorize Play submission or invent legal/retention terms.


## Owner artwork and puzzle update — September 19, 2026

RESOLVED: New puzzles use a regular 12-column × 8-row grid, 96 exact square PNG cuts, a 60%-opacity full-image guide, and 180 seconds. This owner instruction replaces the earlier irregular-pieces/120-second requirement. Existing published IDs and in-flight rounds retain their original contract. Splash and home choose from their respective packaged image folders once per app launch; home uses 75% opacity. See `../PUZZLE_BACKGROUND_UPDATE.md` for migration scope.

Owner clarification: the selected `assets/screen/home` image is shared across ALL app destinations at 75% opacity. The folder name does not limit it to the Home screen. Splash continues to use `assets/screen/splash`.


## Owner account and navigation correction — September 19, 2026

RESOLVED: All created accounts use one online/offline-capable flow. An account created without internet is registered locally and connected through real Supabase authentication when possible. Existing server verification and authorization remain enforced; a local identity cannot issue competitive RPCs. Legacy one-way password records require re-entry once, not a second account. Google/Discord remain deferred by the owner's earlier instruction.

RESOLVED: Mode cards open dedicated Create private room / Join a room pages. Offline is a peer mode card with Question Round, Image Guess and new single-player Puzzle choices. Offline Puzzle uses all 96 pieces, 180 seconds, one practice point per correct distinct placement and no Flames; no new online schedule or production migration is required. See `../ACCOUNT_MODES_UPDATE.md`.
