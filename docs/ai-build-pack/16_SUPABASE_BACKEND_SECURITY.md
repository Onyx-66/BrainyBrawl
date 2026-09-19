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
