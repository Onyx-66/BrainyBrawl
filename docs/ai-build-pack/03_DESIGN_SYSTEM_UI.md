# 03 --- Design System & UI Visual Contract

## Objective

Implement a reusable Compose design system that makes the finished app
visually converge on `mockups/UI_MASTER_REFERENCE.png`.

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

## Visual identity

Brainy Brawl is a **dark, saturated, friendly competitive mobile game**.

Target characteristics: - deep navy/blue application background; - dark
blue cards/panels; - bright saturated primary actions; - green for
positive/success actions; - red for destructive actions; - yellow/gold
for rewards and Flame-related emphasis; - purple for
secondary/competitive/game-mode emphasis; - cyan/light-blue for
information, outlines, and selected states; - high contrast white
primary text; - muted blue-grey secondary text; - rounded, compact
mobile-game components; - subtle depth/shadows, never
glassmorphism-heavy; - playful but readable iconography.

### Reference color tokens

Treat these as **design-system target tokens** and tune only if
screenshot comparison shows the implementation drifting from the
supplied reference:

  Token            Target      Use
  ---------------- ----------- -----------------------------------
  `bbBackground`   `#061A33`   app background
  `bbSurface`      `#0A2442`   large panels
  `bbCard`         `#0D3155`   cards/containers
  `bbPrimary`      `#6B4FF6`   primary/selected game actions
  `bbSecondary`    `#246BFD`   secondary/navigation/game actions
  `bbInfo`         `#36C7FF`   info, outlines, selected accents
  `bbSuccess`      `#10D86A`   correct/confirm/ready
  `bbWarning`      `#FFC83D`   gold/reward/warning
  `bbDanger`       `#FF4B5C`   cancel/destructive/wrong
  `bbText`         `#FFFFFF`   primary text
  `bbTextMuted`    `#A9BCD1`   secondary text
  `bbOutline`      `#2E78B8`   borders/contours

These values are visual targets, not gameplay data.

## Button hierarchy

### Primary

-   Use `bbSuccess` for **Ready / Confirm / Continue / Play** when the
    reference uses a positive action.
-   Use `bbPrimary` or `bbSecondary` for major navigation/game-mode
    actions where the reference uses purple/blue.
-   Strong contrast, rounded rectangle, bold label.

### Secondary

-   Dark blue fill or outlined blue/cyan treatment.
-   Used for alternate navigation and non-primary choices.

### Destructive

-   `bbDanger`.
-   Used for Cancel, Leave, Remove, Report confirmation where
    destructive action is required.
-   Never use red for ordinary navigation.

### Reward

-   `bbWarning` / gold-yellow.
-   Used for Flames/reward emphasis and reward CTA states.

### States

Every button must have normal, pressed, focused, disabled, loading, and
selected states.

## Typography

Use a rounded, friendly sans-serif equivalent to the reference. Prefer
Poppins when licensing/build availability is appropriate; otherwise use
a bundled/system-safe equivalent with the same geometric character.

Hierarchy: - large bold display titles; - bold section headers; -
medium/bold action labels; - readable body text; - compact metadata.

Do not use tiny text to fit a screen. The reference's compactness comes
from hierarchy and spacing, not illegibility.

## Screen composition

For the reference device: - portrait; - 1080 × 2340 logical screenshot
target; - 20:9 ratio; - safe handling of system bars/insets; - content
must not depend on a single hard-coded pixel width.

Use responsive constraints, then verify screenshots at the target ratio.

## Persistent patterns

Where the reference shows them, preserve: - top-left back navigation on
secondary screens; - top resource strip for Gold/Gems/Flames; - bottom
navigation for Home/Games/Store/Profile-style primary destinations; -
full-width bottom CTA for major actions; - compact cards for players and
modes; - centered countdown/timer treatment; - bottom-aligned gameplay
actions; - clear result hierarchy.

## Required components

Build and reuse: - AppScaffold - TopBar - ResourceBar - PrimaryButton -
SecondaryButton - DangerButton - IconButton - ModeCard - PlayerCard -
TeamCard - CurrencyChip - FlameChip - ScoreChip - Timer - ProgressBar -
AnswerOption - MultiSelectAnswerOption - ThemeChoice - TeamSlot -
LeaderboardRow - StoreItemCard - CosmeticCard - QuickReactionOverlay -
Dialog - BottomSheet - LoadingState - EmptyState - ErrorState -
ReconnectingState - VictoryState - DefeatState - FlameRewardState

## Image viewer

For interactive images: - visible light-blue contour/glow; -
approximately 80% black dim backdrop; - tap outside closes; -
green-tinted close zone; - no close button over the image.

## Accessibility and localization

-   Minimum practical touch targets.
-   Content descriptions for icons.
-   Do not communicate meaning by color alone.
-   Support font scaling.
-   Verify French and Arabic strings.
-   Verify Arabic RTL mirroring and mixed numeric placeholders.

## Visual QA

For every UI prompt: 1. Build the screen. 2. Capture a 1080 × 2340
screenshot. 3. Compare hierarchy, placement, color semantics, spacing,
and component shapes with `mockups/UI_MASTER_REFERENCE.png`. 4. Fix
visual drift before declaring the prompt complete. 5. Do not alter
product behavior merely to make the screenshot match.

## Acceptance

The app should feel like the same product shown in the master board, not
like a generic Material 3 app with Brainy Brawl colors pasted on top.
