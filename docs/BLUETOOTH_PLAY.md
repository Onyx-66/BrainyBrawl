# Bluetooth friendly matches

1. Install the same Brainy Brawl version on every phone.
2. Open Games → Bluetooth with friends (also available inside each mode).
3. Enable Bluetooth and allow Android's Nearby devices permission when requested.
4. One player chooses a mode and hosts a room. Allow the phone to be discoverable.
5. Other players use the Bluetooth settings button to pair with the host, return to the game and select the host in the paired-device list. Refresh if necessary.
6. Joining players press Ready. The host starts when the required players are ready.
7. Keep the apps open and phones nearby. A disconnect during play ends the match; create a new room to restart.

| Mode | Players | Scoring |
|---|---:|---|
| 1v1 | 2 | Individual |
| Duo | 4 | Two teams of two |
| Squad | 8 | Two teams of four |
| Solo | 2–8 | Individual |

These are unranked friendly **quiz matches** with up to 15 bundled questions in the host's language, 30 seconds per question, 10 points per correct answer and a three-second answer reveal. Team allocation alternates as players join. Highest score wins; equal scores are ties. They do not run the internet mode's full mixed-minigame sequence or grant ranked rewards/currencies. No account or internet is needed. Solo Bluetooth is capped at eight total players; internet Solo retains its separate capacity.

The secure paired Bluetooth Classic RFCOMM transport uses a fixed service UUID and bounded, versioned JSON. The host controls the questions, timer and scores. Correct answers are revealed only after the round closes. A client cannot submit another player's identity or overwrite host scores. Leaving closes sockets; timeouts and disconnects produce a recoverable error.

Physical multi-phone radio testing remains necessary. The emulator cannot establish real Bluetooth links. Phone/controller combinations can support fewer simultaneous RFCOMM connections; validate an eight-phone Squad session on target devices before a public release.

Android references: [Bluetooth permissions](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions), [Connecting devices](https://developer.android.com/develop/connectivity/bluetooth/connect-bluetooth-devices).
