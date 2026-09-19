"""Independent QA from the published English prompts, not generator outputs."""
import re,statistics,unittest
from fractions import Fraction
from pathlib import Path
import xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]

def expected(item):
    prompt=item.find("field[@name='question']").text
    n=list(map(int,re.findall(r"\d+",prompt)))
    category=item.get('id').split('_')[1]
    a=n[0]
    if category=='addition': return sum(n)
    if category=='subtraction': return a-n[1]
    if category=='multiplication': return a*n[1]
    if category=='division': return Fraction(a,n[1])
    if category=='square': return a**2
    if category=='cube': return a**3
    if category=='percent': return Fraction(a*n[1],100)
    if category=='fraction': return Fraction(a,5)
    if category in ('minutes','seconds'): return a*60
    if category=='perimeter': return sum(n)*2
    if category=='area': return a*n[1]
    if category=='triangle': return Fraction(a*n[1],2)
    if category=='mean': return statistics.mean(n)
    if category=='range': return max(n)-min(n)
    if category=='median': return statistics.median(n)
    if category=='algebra': return Fraction(n[2]-n[1],a)
    if category=='distance': return a*n[1]
    if category=='proportion': return Fraction(n[1]*n[2],a)
    if category=='binary': return int(str(a),2)
    if category=='root': return __import__('math').isqrt(a)
    if category=='sequence':
        assert n[1]-n[0]==n[2]-n[1]
        return n[2]+n[2]-n[1]
    if category=='sum': return sum(range(a,n[1]+1))
    if category=='discount': return Fraction(a*(100-n[1]),100)
    raise AssertionError(category)

class MultilingualBankTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.items=ET.parse(ROOT/'content/question_round.xml').getroot().findall('item')
    def test_all_1200_answers_follow_the_actual_prompt(self):
        items=[i for i in self.items if i.get('locale')=='en' and i.get('id').startswith('NUM_')]
        self.assertEqual(1200,len(items))
        for item in items:
            with self.subTest(id=item.get('id')):
                actual=int(item.find("options/option[@correct='true']").text)
                self.assertEqual(expected(item),actual)
                self.assertEqual(len(set(x.text for x in item.findall('options/option'))),5)
    def test_translation_parity_and_no_correct_position_bias(self):
        groups={};positions=[]
        for item in self.items:
            concept=item.find("field[@name='concept_id']")
            if concept is not None: groups.setdefault(concept.text,{})[item.get('locale')]=item
        self.assertGreaterEqual(len(groups),1200)
        for concept,locales in groups.items():
            self.assertEqual(set(locales),{'en','fr','ar'})
            if not concept.startswith('NUM_'): continue
            en=locales['en'];options=lambda item:[(o.text,o.get('correct')) for o in item.findall('options/option')]
            for item in locales.values(): self.assertEqual(options(en),options(item))
            positions.append(next(i for i,o in enumerate(en.findall('options/option')) if o.get('correct')=='true'))
        for p in range(5): self.assertTrue(180<=positions.count(p)<=300)
    def test_every_content_kind_has_translations_or_language_independent_configuration(self):
        for file in (ROOT/'content').glob('*.xml'):
            locales={i.get('locale') for i in ET.parse(file).getroot().findall('item')}
            if file.stem in ('precision_tap','roll_the_dice'): self.assertEqual(locales,{'global'})
            else:self.assertEqual(locales,{'en','fr','ar'},file.name)
if __name__=='__main__':unittest.main()
