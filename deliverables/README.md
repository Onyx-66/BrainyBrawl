# Brainy Brawl 1.0.4 — mobile test build

`BrainyBrawl-1.0.4.apk` is the current signed, installable testing APK for Android 8.0/API 26 or newer. Transfer it to your phone and open it to install. Package: `com.brainybrawl.app`; version code: 5. It updates the previous development-signed build without clearing account data.

This version redesigns the player/account card, adds twelve avatar and twelve square-frame slots with server sync, moves level/currencies/friends into the card, groups email/platform/password/deletion controls, and uses native Lucide icons from the Morphicons gallery. The gallery photo picker is removed. The final art can be added later using `assets/avatars/README.md` and `assets/frames/README.md`; native preview art works now.

The existing backend is configured. Live avatar upload and appearance metadata were verified on the installed account. Google/Discord setup remains deferred; email changes retain confirmation requirements. This uses the existing development signing key and is not a Play Store release. Remaining gates are in `docs/RELEASE_CHECKLIST.md`. The APK is excluded from Git and the source-change ZIP.

Size: 30338838 bytes.

SHA-256: `2eb2b90703aef415598388e3a464655a35ca24b82e50db95db4aea733d799ed4`

Verified: v2 APK signature, ZIP CRC, no private environment credentials, two scenes, 192 exact puzzle tiles and four native ELF/ZIP 16 KB alignment checks. Debug and optimized unsigned release builds pass. Tests: 76 JVM, 36 emulator plus targeted rechecks, and 24 Python. Lint: zero errors, 68 warnings.
