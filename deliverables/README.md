# Brainy Brawl 1.0.3 — mobile test build

`BrainyBrawl-1.0.3.apk` is the current signed, installable testing APK for Android 8.0/API 26 or newer. Transfer it to your phone and open it to install. Package: `com.brainybrawl.app`; version code: 4. It can update the earlier development-signed build without clearing account data.

This revision adds unified online/offline account connection, compact provider buttons, readable recovery text, an opaque full-width language menu, dedicated mode action pages, Offline Puzzle and the redesigned player card. The backend is configured. Hosted email confirmation is still required; Google/Discord remain deferred by the owner.

This uses the existing development signing key. It is not a Play Store release. Production signing and remaining gates are recorded in `docs/RELEASE_CHECKLIST.md`. The APK is excluded from Git and the source-change ZIP.

Size: 30248642 bytes.

SHA-256: `1086eb172fcde30119fc97c4080a507af8eab43382aed07bba93aeb13c1c7e54`

Verified: v2 APK signature, ZIP CRC, no private environment credentials, two packaged scenes, 192 exact puzzle tiles, and all four native libraries/ZIP alignment for 16 KB pages. Debug and optimized unsigned release builds pass. Tests: 72 JVM, 29 emulator with 11 affected checks rerun, and 24 Python; lint: zero errors, 52 warnings.
