"""Exact cutting and grid contracts from the supplied authoring reference."""
from pathlib import Path
import tempfile,unittest,xml.etree.ElementTree as E
from PIL import Image
from puzzle_image_cutter import split_image,validate_size
ROOT=Path(__file__).resolve().parents[1]
class PuzzleAssetTests(unittest.TestCase):
    def test_all_exported_tiles_reconstruct_original_pixels(self):
        for source in sorted((ROOT/'assets/puzzles').glob('*.png')):
            with Image.open(source) as image:
                size=validate_size(*image.size);folder=source.parent/(source.stem+'_parts')
                self.assertEqual(96,len(list(folder.glob('*.png'))))
                canvas=Image.new(image.mode,image.size)
                for number in range(1,97):
                    with Image.open(folder/f'{source.stem}_Part_{number}.png') as tile:
                        self.assertEqual((size,size),tile.size)
                        canvas.paste(tile,(((number-1)%12)*size,((number-1)//12)*size))
                self.assertEqual(image.tobytes(),canvas.tobytes())
    def test_incompatible_sizes_and_overwrite_are_rejected(self):
        for size in [(941,1672),(1536,1000),(12,16),(0,0)]:
            with self.assertRaises(ValueError):validate_size(*size)
        with tempfile.TemporaryDirectory() as folder:
            source=Path(folder)/'test.png';Image.new('RGB',(120,80),'red').save(source)
            split_image(source)
            with self.assertRaises(FileExistsError):split_image(source)
            self.assertEqual(96,len(list((Path(folder)/'test_parts').glob('*.png'))))
    def test_grid_content_uses_180_seconds_and_exact_square_polygons(self):
        records=[i for i in E.parse(ROOT/'content/collaborative_puzzle.xml').getroot() if i.find("field[@name='layout_type']").text=='grid_12x8']
        self.assertEqual(6,len(records))
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
                self.assertTrue((ROOT/piece.get('asset_ref')).is_file())
                self.assertEqual('LEFT' if col<6 else 'RIGHT',piece.get('side'))
if __name__=='__main__':unittest.main()
