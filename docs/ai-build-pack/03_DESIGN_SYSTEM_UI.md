# 03 — UI Design System & Visual Language

## Objective
Translate the supplied UI references and GDD UI Kit into a reusable Compose design system.

## Visual direction
Use **Kit A / Dark Panel** as the primary system:
- flat saturated colors;
- rounded panels;
- strong dark outlines;
- subtle depth/drop-shadow treatment;
- high-contrast blue, yellow, green, purple, red accents;
- dark-grey neutral panels;
- bold readable typography;
- compact mobile-game HUD components.

Kit B is supplementary only for the specified glyphs: cart, trophy, mail, question mark, camera, gamepad, megaphone. Re-skin those glyphs to match Kit A rather than mixing glossy and flat styles.

## Required components
Build reusable Compose components for:
- Primary/secondary/destructive buttons
- Icon buttons
- Currency chips: Gold/Gems/Flames
- Score/timer/streak chips
- Progress/loading bar
- Player level badge
- Player/team cards
- Mode cards
- Tabs
- Toggle
- Slider
- Dialog/modal
- Bottom sheet
- Chat/reaction popup
- Answer option
- Multi-select answer option
- Team slot
- Leaderboard row
- Cosmetic card
- Store item card
- Empty/loading/error states

## Image viewer
Every interactive image gets:
- visible light-blue contour/glow;
- fullscreen modal on tap;
- approximately 80% black dim backdrop;
- tap outside image closes;
- green-tinted close zone;
- no close button over the image.

## Accessibility
- minimum readable touch targets;
- semantic labels;
- content descriptions for icons;
- support font scaling;
- avoid color-only meaning;
- ensure timers and state changes are communicated clearly.

## Theme
Support Light and Dark from day one. Do not duplicate screens for themes; use tokens.

## Deliverables
A design-system package plus a showcase screen demonstrating every component in both themes and with long English/French/Arabic strings.

## Verification
Screenshot tests or visual regression checks for representative components and at least one RTL layout.
