"""Validate the optional owner-supplied 12-avatar and 12-frame PNG packs."""
import argparse
from pathlib import Path
from PIL import Image

ROOT=Path(__file__).resolve().parents[1]

def validate(strict=False):
    present=0
    missing=[]
    for kind in ('avatar','frame'):
        folder=ROOT/'assets'/(kind+'s')
        expected={f'{kind}_{i:02}.png' for i in range(1,13)}
        unexpected=[p.name for p in folder.glob('*.png') if p.name not in expected]
        if unexpected:raise ValueError(f'Unexpected {kind} filenames: {unexpected}')
        for name in sorted(expected):
            path=folder/name
            if not path.exists():missing.append(str(path.relative_to(ROOT)));continue
            if path.stat().st_size>8*1024*1024:raise ValueError(f'{name}: exceeds 8 MB')
            with Image.open(path) as picture:
                if picture.format!='PNG' or not (1<=picture.width<=4096 and picture.width==picture.height):
                    raise ValueError(f'{name}: use a square PNG of at most 4096 pixels')
                if kind=='frame' and picture.convert('RGBA').getpixel((picture.width//2,picture.height//2))[3]!=0:
                    raise ValueError(f'{name}: the center must be transparent')
                picture.verify() if kind=='avatar' else picture.load()
            present+=1
    if strict and missing:raise ValueError(f'Missing {len(missing)} final artwork files')
    print(f'{present}/24 artwork files validated; {len(missing)} slots use built-in square previews.')
    return present

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--strict',action='store_true',help='Require the complete final 24-file pack')
    validate(parser.parse_args().strict)
