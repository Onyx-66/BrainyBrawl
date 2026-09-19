"""Apply the reviewed launch content metadata after localization/generation."""
import xml.etree.ElementTree as E
import re
from enrich_content import ROOT,field
EXPLANATIONS={
'en':['The illustration shows a sailing vessel with a mast and sails.','The blue whale illustration shows flippers, a tail and a blowhole spout.','The tower has a light at the top to guide ships: it is a lighthouse.','The illustration shows sand dunes in a desert.','The illustration shows a round planet with rings.'],
'fr':['Le dessin montre un voilier avec un mât et des voiles.','La baleine bleue dessinée possède des nageoires, une queue et un jet au-dessus de son évent.','La tour porte une lumière qui guide les navires : c’est un phare.','Le dessin représente des dunes de sable dans un désert.','Le dessin représente une planète ronde entourée d’anneaux.'],
'ar':['يُظهر الرسم سفينة شراعية لها صارية وأشرعة.','يُظهر رسم الحوت الأزرق زعانف وذيلًا ورذاذًا فوق فتحة التنفس.','يحمل البرج ضوءًا لإرشاد السفن، فهو منارة.','يُظهر الرسم كثبانًا رملية في صحراء.','يُظهر الرسم كوكبًا مستديرًا تحيط به حلقات.']}
def main():
 for path in sorted((ROOT/'content').glob('*.xml')):
  tree=E.parse(path);root=tree.getroot()
  for item in root:
   item.set('status','APPROVED')
   if not item.get('id','').startswith('NUM_'):field(item,'review_method','Contract validation, authored translation review and launch content review 2026-09-19')
   if path.stem=='image_guess':
    field(item,'scoring_policy','correct_only')
    # Only the owning player's four selected choices are evaluated; values stay hidden until reveal.
    number=int(re.search(r'\d+',item.get('id')).group())-1
    field(item,'explanation',EXPLANATIONS[item.get('locale')][number])
   asset=item.find("field[@name='asset_ref']")
   if asset is not None:
    field(item,'source_url','project://'+asset.text)
    field(item,'license','Original Brainy Brawl project artwork; distributed as part of the game')
    field(item,'attribution','Brainy Brawl original vector illustrations')
    field(item,'asset_source','Original project SVG; no third-party image')
   if path.stem=='collaborative_puzzle':
    field(item,'notes','Original project illustration, 96 deterministic irregular pieces, equal left/right ownership')
    field(item,'geometry_source','Original deterministic 12 by 8 tessellation')
  E.indent(root,space='  ');tree.write(path,encoding='utf-8',xml_declaration=True)
 for path in (ROOT/'assets/images').glob('*.svg'):
  text=path.read_text(encoding='utf-8').replace('<title>Development illustration</title>','<title>Brainy Brawl original illustration</title>')
  path.write_text(text,encoding='utf-8')
 print('Applied reviewed launch metadata and owner-approved correct-only Image Guess scoring.')
if __name__=='__main__':main()
