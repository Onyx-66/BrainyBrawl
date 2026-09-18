# 17 — Decision Register

This file prevents AI agents from silently inventing product rules.

## Confirmed by GDD
- Platform: Android/Google Play.
- Modes: 1v1, Solo Online, Duo, Squad, Offline.
- Room sizes: 2 / 20 / 40 / 40 / 1.
- Private-by-default for Solo Online/Duo/Squad.
- Matchmaking toggle opens empty slots.
- 1v1 public quick match + friend invite.
- Flames are skill-earned only.
- Launch chat is preset quick replies/emotes, not free text.
- English/French/Arabic at launch.
- Arabic requires RTL engineering.
- Kit A is the primary UI system.
- Image Viewer behavior is defined.
- Exact IAP tiers/Battle Pass/ad earning remain open.
- Full content sourcing/moderation pipeline remains open.
- Anti-cheat details remain open, though server-authoritative validation is recommended.
- Disconnect/reconnect behavior remains an engineering item.

## Native-stack decisions made for this build pack
These are implementation choices requested by the user, not statements from the GDD:
- Android Studio.
- Kotlin.
- Jetpack Compose.
- Supabase Auth/Postgres/Realtime/Storage/Edge Functions.
- Google Play Billing for Gems.
- Room/local cache where useful.

## Must not be invented by agents
- Final Solo Online phase schedule.
- Exact IAP pricing.
- Battle Pass rules.
- Advertising economy.
- Final production trivia sourcing policy.
- Legal/licensing status of third-party images.
- Moderation thresholds.
- Competitive ranking formulas beyond those explicitly defined.
