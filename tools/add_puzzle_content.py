"""Register already-cut 12 x 8 puzzle artwork without editing any image pixels."""
from pathlib import Path
import argparse
import re
import xml.etree.ElementTree as ET
from PIL import Image
from puzzle_image_cutter import validate_size

ROOT = Path(__file__).resolve().parents[1]


def add_puzzle(source, content_id, themes, license_text, attribution, source_url, approved=False, root=ROOT):
    source = Path(source).resolve()
    root = Path(root).resolve()
    if not source.is_relative_to(root / 'assets' / 'puzzles') or not source.is_file():
        raise ValueError('Place the original inside assets/puzzles first.')
    if not re.fullmatch(r'[A-Za-z0-9_-]+', content_id):
        raise ValueError('Use letters, numbers, underscores or hyphens for the ID.')
    if set(themes) != {'en', 'fr', 'ar'} or not all(value.strip() for value in [*themes.values(), license_text, attribution, source_url]):
        raise ValueError('All three topics and source/license/attribution are required.')
    with Image.open(source) as image:
        validate_size(*image.size)
    pieces = source.parent / (source.stem + '_parts')
    tiles = [pieces / f'{source.stem}_Part_{number}.png' for number in range(1, 97)]
    if not all(tile.is_file() for tile in tiles):
        raise ValueError('Run puzzle_image_cutter.py on this original first; all 96 tiles are required.')
    target = root / 'content' / 'collaborative_puzzle.xml'
    tree = ET.parse(target)
    document = tree.getroot()
    existing = {node.get('id') for node in document.iter() if node.get('id')}
    new_ids = set()
    for locale in ('en', 'fr', 'ar'):
        prefix = f'{content_id}_{locale.upper()}'
        new_ids.update([prefix, *[f'{prefix}_P{n:03}' for n in range(1, 97)]])
    if existing & new_ids:
        raise ValueError('This ID already exists; existing puzzles are never overwritten.')
    for locale in ('en', 'fr', 'ar'):
        prefix = f'{content_id}_{locale.upper()}'
        item = ET.SubElement(document, 'item', id=prefix, locale=locale, status='APPROVED' if approved else 'DRAFT')
        fields = {'theme': themes[locale], 'asset_key': content_id.lower(), 'piece_count': '96', 'columns': '12', 'rows': '8',
                  'time_limit_seconds': '180', 'layout_type': 'grid_12x8', 'player_split': 'left_right',
                  'asset_ref': source.relative_to(root).as_posix(), 'source_url': source_url, 'license': license_text, 'attribution': attribution}
        for name, value in fields.items():
            ET.SubElement(item, 'field', name=name).text = value
        nodes = ET.SubElement(item, 'pieces')
        for number, tile in enumerate(tiles, 1):
            row, column = divmod(number - 1, 12)
            polygon = [(column / 12, row / 8), ((column + 1) / 12, row / 8), ((column + 1) / 12, (row + 1) / 8), (column / 12, (row + 1) / 8)]
            ET.SubElement(nodes, 'piece', id=f'{prefix}_P{number:03}', slot=f'{prefix}_S{number:03}', row=str(row), column=str(column),
                          side='LEFT' if column < 6 else 'RIGHT', rotation='0', polygon=' '.join(f'{x:.12f},{y:.12f}' for x, y in polygon), asset_ref=tile.relative_to(root).as_posix())
    document.set('contentVersion', str(int(document.get('contentVersion', '1')) + 1))
    ET.indent(tree, space='  ')
    staged = target.with_suffix('.xml.tmp')
    tree.write(staged, encoding='utf-8', xml_declaration=True)
    staged.replace(target)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('source', type=Path)
    parser.add_argument('--id', required=True)
    for locale in ('en', 'fr', 'ar'):
        parser.add_argument(f'--theme-{locale}', required=True)
    parser.add_argument('--license', required=True)
    parser.add_argument('--attribution', required=True)
    parser.add_argument('--source-url', required=True)
    parser.add_argument('--approve', action='store_true', help='Use only after reviewing art, translations and rights; otherwise saves DRAFT.')
    args = parser.parse_args()
    add_puzzle(args.source, args.id, {locale: getattr(args, 'theme_' + locale) for locale in ('en', 'fr', 'ar')}, args.license, args.attribution, args.source_url, args.approve)
    print('Added three localized puzzle records. Run content_pipeline.py validate before building.')
