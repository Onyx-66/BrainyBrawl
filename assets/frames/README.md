# Add your 12 frames here

Put the final artwork directly in this folder, using these exact filenames:

- `frame_01.png`
- `frame_02.png`
- `frame_03.png`
- `frame_04.png`
- `frame_05.png`
- `frame_06.png`
- `frame_07.png`
- `frame_08.png`
- `frame_09.png`
- `frame_10.png`
- `frame_11.png`
- `frame_12.png`

Recommended: 1024 × 1024 PNG, maximum 4096 × 4096 and 8 MB per file. Use square PNG frames with a transparent center. Keep the central 83% of the canvas clear; the avatar occupies that square underneath the overlay. Do not bake a background into the transparent opening.

Rebuild the app after adding or replacing files. No source-code changes are required. All twelve slots currently work with native square preview art when a file is absent. These previews are placeholders, not the final owner-supplied pack. Avatars/frames are cosmetic only and grant no Flames, currency or privileged roles.

Avatar choices upload a bounded 512 × 512 JPEG to the existing private profile-photos bucket when signed in online. Slot IDs synchronize to user-owned Auth metadata; frame art is bundled with the app and selected by ID. Offline changes are queued. Neither catalog artwork nor arbitrary profile uploads are exposed through a gallery picker.
