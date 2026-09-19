"""Exact 12 x 8 cutting, adapted from the user-supplied puzzle_image_cutter.py.
Original pixels/opacity are preserved. Used only at authoring time, never on Android.
"""
from __future__ import annotations
import os, threading, argparse
from dataclasses import dataclass
from pathlib import Path
from typing import Callable
from PIL import Image, ImageOps

COLUMNS = 12
ROWS = 8
PART_COUNT = COLUMNS * ROWS


class ExportCancelled(Exception):
    """Raised when the user cancels the current export."""


@dataclass(frozen=True)
class ExportResult:
    source: Path
    folder: Path
    width: int
    height: int
    tile_size: int


def output_folder(source: Path, root: Path | None = None) -> Path:
    """Use the source filename without its final extension."""
    return (root if root is not None else source.parent) / f"{source.stem}_parts"


def validate_size(width: int, height: int) -> int:
    """Return the square side length, or explain why exact cutting is impossible."""
    if (
        width < COLUMNS
        or height < ROWS
        or width % COLUMNS != 0
        or height % ROWS != 0
        or width // COLUMNS != height // ROWS
    ):
        raise ValueError(
            f"{width} x {height} pixels cannot be cut unchanged into 12 x 8 "
            "equal square pieces. Use a landscape 3:2 image whose width is "
            "divisible by 12 and height by 8, for example 1536 x 1024 "
            "(128 x 128 per piece) or 1200 x 800 (100 x 100 per piece). "
            "This image was not cropped, resized or exported."
        )
    return width // COLUMNS


def split_image(
    source: Path | str,
    root: Path | str | None = None,
    *,
    progress: Callable[[int], None] | None = None,
    cancel: threading.Event | None = None,
) -> ExportResult:
    """Export one picture into a new folder containing exactly 96 PNG files.

    Existing output paths are never overwritten. On cancellation or failure,
    files created by this call are removed (provided the OS permits cleanup).
    PNG-supported image modes are preserved. CMYK and other unsupported modes
    are rejected rather than silently changing their color representation.
    """
    source = Path(source).expanduser().absolute()
    root = Path(root).expanduser().absolute() if root is not None else None
    destination = output_folder(source, root)

    def check_cancel() -> None:
        if cancel is not None and cancel.is_set():
            raise ExportCancelled("Export cancelled.")

    check_cancel()
    if os.path.lexists(destination):
        raise FileExistsError(
            f"Output already exists: {destination}\n"
            "Nothing was overwritten. Move or rename that folder, or choose "
            "a different output location."
        )

    with Image.open(source) as original:
        if getattr(original, "n_frames", 1) > 1:
            raise ValueError(
                "Animated or multi-page images are not supported. "
                "Export a single frame as PNG first."
            )

        # Correct camera orientation before calculating rows and columns.
        image = ImageOps.exif_transpose(original)
        try:
            width, height = image.size
            tile_size = validate_size(width, height)
            if image.mode not in {"1", "L", "LA", "P", "RGB", "RGBA", "I;16"}:
                raise ValueError(
                    f"Image mode {image.mode!r} is not supported for unchanged "
                    "PNG export. Convert the image to RGB or RGBA first."
                )

            image.load()
            # Retain useful color information without carrying camera metadata.
            save_options = {}
            for key in ("icc_profile", "transparency", "dpi"):
                if image.info.get(key) is not None:
                    save_options[key] = image.info[key]

            check_cancel()
            destination.parent.mkdir(parents=True, exist_ok=True)
            # Exclusive directory creation also prevents accidental rerun overwrite.
            destination.mkdir(exist_ok=False)
            created: list[Path] = []
            try:
                for row in range(ROWS):
                    for column in range(COLUMNS):
                        check_cancel()
                        part_number = row * COLUMNS + column + 1
                        x = column * tile_size
                        y = row * tile_size
                        box = (x, y, x + tile_size, y + tile_size)
                        name = f"{source.stem}_Part_{part_number}.png"
                        piece_path = destination / name
                        piece = image.crop(box)
                        try:
                            # Remove inherited metadata; reapply selected data above.
                            piece.info.clear()
                            with piece_path.open("xb") as stream:
                                created.append(piece_path)
                                piece.save(stream, format="PNG", **save_options)
                        finally:
                            piece.close()
                        if progress is not None:
                            progress(part_number)
                check_cancel()
            except BaseException:
                # Delete only files created by this export, never unrelated files.
                for piece_path in reversed(created):
                    try:
                        piece_path.unlink(missing_ok=True)
                    except OSError:
                        pass
                try:
                    destination.rmdir()
                except OSError:
                    pass
                raise

            return ExportResult(source, destination, width, height, tile_size)
        finally:
            image.close()

if __name__ == "__main__":
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source",type=Path)
    parser.add_argument("--output",type=Path)
    args=parser.parse_args()
    result=split_image(args.source,args.output)
    print(f"Exported 96 unchanged {result.tile_size} x {result.tile_size} PNG pieces to {result.folder}")
