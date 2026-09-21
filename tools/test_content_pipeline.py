import contextlib
import io
from pathlib import Path
import shutil
import tempfile
import unittest
import xml.etree.ElementTree as ET
from content_pipeline import ROOT, KINDS, validate, sanitized, export_approved

class ContentValidationTest(unittest.TestCase):
    def setUp(self):
        self.temp=tempfile.TemporaryDirectory()
        self.directory=Path(self.temp.name)
        for kind in KINDS: shutil.copyfile(ROOT/'content'/f'{kind}.xml',self.directory/f'{kind}.xml')
    def tearDown(self): self.temp.cleanup()
    def run_validator(self,production=False):
        with contextlib.redirect_stdout(io.StringIO()): return validate(self.directory,production)
    def mutate(self,kind,body):
        p=self.directory/f'{kind}.xml';root=ET.parse(p).getroot();body(root);ET.ElementTree(root).write(p,encoding='utf-8')
    def test_online_question_payload_hides_answer_and_explanation(self):
        item=ET.parse(self.directory/'question_round.xml').getroot()[0]
        display,answer=sanitized(item,'question_round')
        self.assertNotIn('explanation',display)
        self.assertTrue(all(set(o)=={'id','label'} for o in display['options']))
        self.assertIn('correct_option_id',answer)
    def test_online_image_payload_hides_point_weights(self):
        item=ET.parse(self.directory/'image_guess.xml').getroot()[0]
        display,answer=sanitized(item,'image_guess')
        self.assertTrue(all(set(o)=={'id','label'} for o in display['options']))
        self.assertIn('points',answer['choices'][0])
    def test_all_files(self): self.assertEqual(self.run_validator(),4818)
    def test_development_content_cannot_be_released(self):
        self.mutate('question_round',lambda r:r[0].set('status','DEV_SAMPLE'))
        with self.assertRaisesRegex(ValueError,'unapproved'): self.run_validator(True)
    def test_duplicate_id(self):
        self.mutate('question_round',lambda r:r[1].set('id',r[0].get('id')))
        with self.assertRaisesRegex(ValueError,'duplicate'): self.run_validator()
    def test_missing_approval(self):
        self.mutate('question_round',lambda r:r[0].attrib.pop('status'))
        with self.assertRaisesRegex(ValueError,'approval'): self.run_validator()
    def test_wrong_option_count(self):
        def change(r):
            options=r[0].find('options');options.remove(options[0])
        self.mutate('question_round',change)
        with self.assertRaisesRegex(ValueError,'five options'): self.run_validator()
    def test_multiple_correct(self):
        self.mutate('question_round',lambda r:r[0].findall('options/option')[0].set('correct','true'))
        with self.assertRaisesRegex(ValueError,'one correct'): self.run_validator()
    def test_missing_asset(self):
        self.mutate('image_guess',lambda r:setattr(r[0].find("field[@name='asset_ref']"),'text','assets/missing.svg'))
        with self.assertRaisesRegex(ValueError,'missing/unsafe'): self.run_validator()
    def test_asset_path_escape(self):
        self.mutate('image_guess',lambda r:setattr(r[0].find("field[@name='asset_ref']"),'text','../secrets'))
        with self.assertRaisesRegex(ValueError,'missing/unsafe'): self.run_validator()
    def test_unsupported_schema(self):
        self.mutate('question_round',lambda r:r.set('schemaVersion','999'))
        with self.assertRaisesRegex(ValueError,'version'): self.run_validator()
    def test_invalid_locale(self):
        self.mutate('question_round',lambda r:r[0].set('locale','xx'))
        with self.assertRaisesRegex(ValueError,'locale'): self.run_validator()
    def test_puzzle_piece_count(self):
        def change(r):
            item=next(i for i in r if i.get('status')=='APPROVED');pieces=item.find('pieces');pieces.remove(pieces[0])
        self.mutate('collaborative_puzzle',change)
        with self.assertRaisesRegex(ValueError,'puzzle size'): self.run_validator()
    def test_entity_injection(self):
        p=self.directory/'question_round.xml';p.write_text('<!DOCTYPE content [<!ENTITY x SYSTEM "file:///etc/passwd">]><content>&x;</content>')
        with self.assertRaisesRegex(ValueError,'DTD/entity'): self.run_validator()
    def test_malformed_xml(self):
        (self.directory/'question_round.xml').write_text('<broken>')
        with self.assertRaises(ET.ParseError): self.run_validator()
    def test_missing_scramble_letter(self):
        self.mutate('word_scramble',lambda r:setattr(r[0].find("field[@name='shuffled_letters']"),'text','BROKEN'))
        with self.assertRaisesRegex(ValueError,'scramble letters'): self.run_validator()

    def test_reaction_export_populates_localized_catalog_and_escapes_text(self):
        self.mutate('reactions',lambda r:r[0].set('status','APPROVED'))
        self.mutate('reactions',lambda r:setattr(r[0].find("field[@name='text']"),'text',"That's good"))
        output=self.directory/'approved.sql'
        with contextlib.redirect_stdout(io.StringIO()): export_approved(output,self.directory)
        sql=output.read_text(encoding='utf-8')
        self.assertIn('insert into public.reactions',sql)
        self.assertIn('insert into public.localization_entries',sql)
        self.assertIn("That''s good",sql)
        self.assertNotIn("'DEV_SAMPLE'",sql)
    def test_unapproved_export_does_not_create_output(self):
        output=self.directory/'approved.sql'
        with contextlib.redirect_stdout(io.StringIO()):
            for path in self.directory.glob('*.xml'):
                tree=ET.parse(path)
                for item in tree.getroot():
                    if item.get('status')!='RETIRED':item.set('status','REVIEW')
                tree.write(path,encoding='utf-8',xml_declaration=True)
        with self.assertRaisesRegex(ValueError,'No approved'): export_approved(output,self.directory)
        self.assertFalse(output.exists())

if __name__=='__main__': unittest.main()
