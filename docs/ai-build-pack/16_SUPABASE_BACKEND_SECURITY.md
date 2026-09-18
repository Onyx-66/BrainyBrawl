# 16 --- Supabase Schema, RLS, Edge Functions & Security

## Objective

Build the backend contract before relying on client-side feature code.

## Core tables (proposed)

-   profiles
-   connected_accounts
-   friendships
-   blocks
-   reports
-   rooms
-   room_members
-   teams
-   team_members
-   matches
-   match_participants
-   match_phases
-   match_rounds
-   submissions
-   score_events
-   match_results
-   currencies
-   currency_ledger
-   inventory
-   cosmetics
-   store_items
-   purchases
-   leaderboard_snapshots
-   content_questions
-   content_image_guess
-   content_puzzles
-   content_puzzle_pieces
-   content_word_scrambles
-   content_speed_sort
-   reactions
-   localization_entries

## RLS principles

Default deny. Explicitly allow: - users to read their own profile; -
permitted friend/profile views; - room members to read only the room
data they need; - match participants to read their match; -
public/approved leaderboard reads; - public approved game content reads
if appropriate.

Writes to competitive scoring, Flames, leaderboard records, and currency
balances should be server-authorized.

## Edge Functions

Use functions for: - secure match start; - server timestamp/deadline
handling; - answer validation; - scoring; - Flame reward; - purchase
verification; - privileged moderation operations; - leaderboard
aggregation where needed.

## Realtime

Enable only the tables/channels that need realtime. Keep payloads
minimal.

## Content import

Import only `APPROVED` content. Preserve content IDs from the XLSX.

## Secrets

Service-role key exists only server-side. Android receives only
publishable/anon credentials appropriate for the client architecture.

## Deliverables

SQL migrations, RLS policies, indexes, Edge Functions, seed/import
process, and a backend README.

## Verification

Attempt unauthorized reads/writes with test users. Prove that
client-side requests cannot award Flames, modify balances, submit scores
for another player, or alter completed match results.

## Visual reference contract --- mandatory

The project includes a visual master reference at:
`mockups/UI_MASTER_REFERENCE.png`

### What the reference controls

Use the reference as the primary **visual target** for: - overall screen
composition; - information hierarchy; - button placement patterns; -
navigation placement; - card proportions; - spacing rhythm; - dark blue
panel treatment; - saturated accent colors; - rounded corners; - bold,
friendly typography; - icon treatment; - HUD placement; - player/team
cards; - timers/progress indicators; - modal/popup treatment; -
victory/defeat/reward presentation.

The reference targets a **Samsung Galaxy A56-style 1080 × 2340 px, 20:9
portrait frame**. Compose layouts responsively, but use that frame for
visual review and screenshot baselines.

### What the reference does NOT control

The reference is not allowed to override: - GDD gameplay rules; - exact
round counts/timers/scoring; - server-authoritative behavior; -
security/RLS requirements; - localization requirements; - accessibility
requirements; - unresolved product decisions.

The GDD-derived instruction files remain authoritative for behavior. If
the mockup and a written product rule conflict, implement the written
product rule and preserve the mockup's visual language where possible.

### Visual implementation rules

-   Do not replace the reference with a generic Material 3 look.
-   Material 3 is an implementation foundation only; Brainy Brawl's
    custom design tokens must drive the visible result.
-   Do not introduce arbitrary new colors per screen. Use semantic
    design tokens.
-   Do not invent new navigation patterns when an equivalent pattern
    exists in the reference.
-   Keep primary actions visually prominent, secondary actions distinct,
    and destructive actions red.
-   Keep gameplay controls thumb-reachable and visually stable across
    rounds.
-   Use the same component for the same semantic purpose across screens.
-   Use real assets where supplied; do not create fake logos, fake store
    products, or fake player data as production content.
-   Visual placeholders are acceptable in development only when clearly
    marked and replaceable by stable asset/content IDs.
-   Every screen should be reviewable at 1080 × 2340 without clipping,
    overlap, or unreadable text.
-   Test long French and Arabic strings; RTL must mirror layout without
    breaking the reference hierarchy.

### Reference screen inventory

The master board is a visual target for these families: 1. Splash /
launch 2. Login 3. Sign up 4. Home / Landing 5. Profile 6. Friends 7.
Store 8. Settings 9. Mode selection 10. Loadout selection 11. Lobby 12.
1v1 Question Round 13. 1v1 Image Guess 14. Duo 96-piece puzzle 15. Squad
Precision Tap 16. Squad Speed Sort 17. Theme selection/draft 18. Solo
Online search/game shell 19. Results / Victory / Defeat 20. Matchmaking
/ Countdown 21. Correct / Wrong answer states 22. Reaction overlay 23.
Reconnecting 24. Flame reward 25. Shared design-system components

When implementing a screen not explicitly pictured, infer only the
**visual language and component grammar**, not new product behavior.
