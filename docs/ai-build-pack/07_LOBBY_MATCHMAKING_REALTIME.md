# 07 — Rooms, Matchmaking & Realtime Multiplayer

## Objective
Implement room lifecycle and realtime synchronization for up to 40 players in team modes.

## Modes/room sizes
- 1v1: 2
- Solo Online: up to 20
- Duo: up to 40 / 20 teams
- Squad: up to 40 / 10 teams of 4
- Offline: no room/networking

## Room defaults
Solo Online, Duo, and Squad rooms start Private.
A Matchmaking toggle opens empty slots to public matchmaking.
Private rooms must not enter the public pool until opened.
1v1 supports public quick match and direct friend invite.

## Lobby behavior
Implement:
- create room;
- join by invite/code if selected by product design;
- friend invite;
- leave;
- host migration or explicit host-loss behavior;
- team assignment;
- team naming;
- ready state;
- player presence;
- match start authorization.

Duo/Squad team names default to `X's Team` using Player 1's username if no custom name is supplied.

## Realtime
Use Supabase Realtime for:
- room membership;
- ready states;
- team changes;
- live gameplay events where appropriate.

Do not broadcast large/high-frequency state blindly. Prefer compact event messages and server-stored authoritative state for important transitions.

## Disconnect/reconnect
Design and test:
- temporary network loss;
- app backgrounding;
- reconnect;
- player replacement where allowed;
- abandoned room;
- match already advanced while client was offline.

## Deliverables
Lobby UI + repository + realtime event layer + server policies/functions + automated state-machine tests.

## Verification
Test 2, 20, and 40-player simulated rooms where possible. Measure event frequency and payload size.
