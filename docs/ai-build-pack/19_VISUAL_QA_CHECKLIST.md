# 19 --- Visual QA Checklist

Use this checklist for every UI change.

## Device/frame

-   [ ] 1080 × 2340 portrait screenshot captured
-   [ ] 20:9 ratio
-   [ ] system bars/insets handled correctly
-   [ ] no clipped/overlapping content

## Composition

-   [ ] top bar/resource area matches reference pattern
-   [ ] primary CTA placement matches reference grammar
-   [ ] secondary/destructive actions use correct hierarchy
-   [ ] bottom navigation matches reference where applicable
-   [ ] cards/panels have consistent spacing and corner treatment
-   [ ] gameplay HUD stays stable between rounds

## Color semantics

-   [ ] primary/secondary colors use design tokens
-   [ ] green is reserved for positive/ready/correct actions
-   [ ] red is reserved for destructive/wrong/cancel actions
-   [ ] yellow/gold is used for rewards/Flame emphasis
-   [ ] purple/blue/cyan match the Brainy Brawl palette
-   [ ] text contrast is readable

## Typography

-   [ ] headings are bold and clear
-   [ ] body text is readable
-   [ ] metadata is not excessively small
-   [ ] long strings do not overflow

## Interaction

-   [ ] touch targets are practical
-   [ ] pressed/disabled/loading states exist
-   [ ] no accidental input blocking
-   [ ] dialogs/modals match the reference behavior

## Localization

-   [ ] English
-   [ ] French
-   [ ] Arabic
-   [ ] RTL
-   [ ] mixed numbers/placeholders
-   [ ] no hard-coded user-facing strings

## Fidelity rule

When the screenshot differs from the master reference, first determine
whether the difference is: 1. required by the GDD; 2. required for
accessibility/responsiveness; 3. an intentional implementation
improvement; 4. accidental visual drift.

Only #4 should be fixed automatically. Product-rule changes require an
explicit decision.
