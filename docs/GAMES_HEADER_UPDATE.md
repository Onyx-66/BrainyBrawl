# Games, header and appearance update — 1.0.6

The Games screen now uses the owner-supplied 1v1, Duo, Squad, Solo and Offline cards and matching icons. Runtime painter bounds remove transparent outer padding without modifying the original images. UI artwork decodes are bounded to 720 pixels on the longest side; scene images keep their existing decode path.

The shared header shows the selected framed avatar, username, level and actual Coins/Gold, Gems and Flames balances. Missing online balances show a dash. Currency controls open the matching store subsection, including repeated taps while already in the store. Compact number labels retain exact amounts for accessibility. Small screens and larger system fonts use a second header row. Reference values are never copied into player balances.

Bottom navigation uses glossy active buttons and only draws the selected tab label. Inactive tabs retain accessible names. Mode actions, lobbies and offline game pages retain Games as their parent tab.

The supplied pack contains 10 avatars and 20 frames. All are included, with selection limited to available artwork. Previously saved avatar IDs 11/12 remain readable via the existing fallback. Rectangular source frames are fitted into a square presentation; the portrait is aligned to the transparent opening. Selection remains account-scoped and syncs using the existing authenticated service. No schema or economy changes were deployed.

Verification: 76 JVM tests, 40 emulator tests and 24 Python/content tests passed. All 30 appearance assets passed strict validation. Debug and optimized release builds passed; lint has 0 errors and 74 warnings. APK ZIP CRC, private credential exclusion, all 192 exact puzzle tiles, native 16 KB alignment, ZIP alignment and signature verification passed. English, Arabic and light-theme layouts were captured and inspected. The existing signed-in account was preserved, and the normal app was launched on emulator-5554 at the Games page.

Installable development-signed APK: deliverables/BrainyBrawl-1.0.6.apk (67,051,032 bytes).
SHA-256: d65d1847d34578449c39ba7430149a4aafad567f5da2108757fd495d98d60923

Artwork source: owner-updated assets/ui, assets/avatars and assets/frames. Original PNGs are preserved. Legacy placeholder README files were removed by the owner; this document records the active pack convention (avatar_01.png through avatar_10.png; frame_01.png through frame_20.png).

Android Studio startup recovery: its default system cache contained an inaccessible stale .port socket for exited PID 18540. The stale .lock was backed up locally; only this task's failed startup processes were stopped. Studio successfully opened BrainyBrawl using .local/ide-session.properties and a fresh .local/android-studio-system cache, preserving the existing IDE configuration. Window title verified: BrainyBrawl – .gitignore.
