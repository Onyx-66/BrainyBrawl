from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET
from PIL import Image
from puzzle_image_cutter import split_image
from add_puzzle_content import add_puzzle

class AddPuzzleContentTests(unittest.TestCase):
    def test_registration_creates_localized_exact_grid_and_never_overwrites_ids(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder);(root/'assets/puzzles').mkdir(parents=True);(root/'content').mkdir()
            source=root/'assets/puzzles/test.png';Image.new('RGB',(120,80),'blue').save(source);split_image(source)
            target=root/'content/collaborative_puzzle.xml';target.write_text('<content kind="collaborative_puzzle" schemaVersion="1" contentVersion="1"/>')
            themes={'en':'Lake','fr':'Lac','ar':'بحيرة'}
            add_puzzle(source,'PUZ_TEST',themes,'Original','Owner','project://test',root=root)
            doc=ET.parse(target).getroot();self.assertEqual(3,len(doc));self.assertEqual('2',doc.get('contentVersion'))
            for item in doc:
                self.assertEqual('DRAFT',item.get('status'));self.assertEqual(96,len(item.findall('pieces/piece')))
                self.assertEqual(themes[item.get('locale')],item.find("field[@name='theme']").text)
                self.assertEqual(48,sum(p.get('side')=='LEFT' for p in item.findall('pieces/piece')))
            before=target.read_bytes()
            with self.assertRaises(ValueError):add_puzzle(source,'PUZ_TEST',themes,'Original','Owner','project://test',root=root)
            self.assertEqual(before,target.read_bytes())
