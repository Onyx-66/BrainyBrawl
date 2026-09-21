"""Exact cutting and grid contracts from the supplied authoring reference."""
from pathlib import Path
import tempfile,unittest,xml.etree.ElementTree as E
from PIL import Image
from puzzle_image_cutter import split_image,validate_size
ROOT=Path(__file__).resolve().parents[1]
class PuzzleAssetTests(unittest.TestCase):
    def test_all_webp_sources_preserve_dimensions_and_reduce_size(self):
        records=[i for i in E.parse(ROOT/'content/collaborative_puzzle.xml').getroot() if i.get('status')=='APPROVED']
        assets={i.find("field[@name='asset_ref']").text for i in records}
        self.assertEqual(49,len(assets))
        for ref in assets:
            asset=ROOT/ref;original=asset.parent/'originals'/asset.with_suffix('.png').name
            with Image.open(asset) as image,Image.open(original) as source:
                self.assertEqual(source.size,image.size)
                self.assertEqual((1536,1024),image.size)
            self.assertLess(asset.stat().st_size,original.stat().st_size)
    def test_incompatible_sizes_and_overwrite_are_rejected(self):
        for size in [(941,1672),(1536,1000),(12,16),(0,0)]:
            with self.assertRaises(ValueError):validate_size(*size)
        with tempfile.TemporaryDirectory() as folder:
            source=Path(folder)/'test.png';Image.new('RGB',(120,80),'red').save(source)
            split_image(source)
            with self.assertRaises(FileExistsError):split_image(source)
            self.assertEqual(96,len(list((Path(folder)/'test_parts').glob('*.png'))))
    def test_grid_content_uses_180_seconds_and_exact_square_polygons(self):
        records=[i for i in E.parse(ROOT/'content/collaborative_puzzle.xml').getroot() if i.get('status')=='APPROVED' and i.find("field[@name='layout_type']").text=='grid_12x8']
        self.assertEqual(147,len(records))
        self.assertEqual({'en','fr','ar'},{i.get('locale') for i in records})
        for item in records:
            self.assertEqual('180',item.find("field[@name='time_limit_seconds']").text)
            pieces=item.findall('pieces/piece');self.assertEqual(96,len(pieces))
            for number,piece in enumerate(pieces,1):
                row,col=divmod(number-1,12)
                self.assertEqual((row,col),(int(piece.get('row')),int(piece.get('column'))))
                expected=[(col/12,row/8),((col+1)/12,row/8),((col+1)/12,(row+1)/8),(col/12,(row+1)/8)]
                actual=[tuple(map(float,p.split(','))) for p in piece.get('polygon').split()]
                for a,b in zip(actual,expected):
                    for x,y in zip(a,b):self.assertAlmostEqual(x,y,places=8)
                self.assertTrue((ROOT/item.find("field[@name='asset_ref']").text).is_file())
                self.assertEqual('LEFT' if col<6 else 'RIGHT',piece.get('side'))
if __name__=='__main__':unittest.main()
