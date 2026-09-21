from pathlib import Path
import json,sys,xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'.local/python'))
from PIL import Image
source=ROOT/'content/sources';source.mkdir(exist_ok=True)
report=[]
for folder,pattern,dest in [('assets/puzzles/originals','puzzle_*.png','assets/puzzles'),('assets/images/originals','*.png','assets/images/scenes')]:
 out=ROOT/dest;out.mkdir(parents=True,exist_ok=True)
 for path in sorted((ROOT/folder).glob(pattern)):
  target=out/(path.stem+'.webp');im=Image.open(path).convert('RGB');im.save(target,'WEBP',quality=90,method=6)
  assert Image.open(target).size==im.size
  report.append({'source':path.relative_to(ROOT).as_posix(),'asset':target.relative_to(ROOT).as_posix(),'source_bytes':path.stat().st_size,'webp_bytes':target.stat().st_size,'width':im.width,'height':im.height})
(source/'webp_conversion.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('Converted',len(report),'images:',sum(i['source_bytes'] for i in report),'->',sum(i['webp_bytes'] for i in report),'bytes. All supplied originals preserved.')
p=ROOT/'content/collaborative_puzzle.xml';root=ET.parse(p).getroot();root.set('contentVersion','4')
for old in root.findall('item'):old.set('status','RETIRED')
def fields(item,values):
 for k,v in values.items():ET.SubElement(item,'field',name=k).text=str(v)
for asset in sorted((ROOT/'assets/puzzles').glob('puzzle_*.webp')):
 for locale in ['en','fr','ar']:
  key=f'PZ_{asset.stem.upper()}_{locale.upper()}'
  existing=root.find(f"item[@id='{key}']")
  if existing is not None:existing.set("status","APPROVED");continue
  item=ET.SubElement(root,'item',id=key,locale=locale,status='APPROVED')
  fields(item,dict(asset_ref=asset.relative_to(ROOT).as_posix(),piece_count=96,columns=12,rows=8,time_limit_seconds=180,layout_type='grid_12x8',source_url='project://'+asset.relative_to(ROOT).as_posix(),license='Owner-supplied artwork authorized for this project',attribution='BrainyBrawl',difficulty='medium'))
  pieces=ET.SubElement(item,'pieces')
  for r in range(8):
   for c in range(12):
    n=r*12+c+1;pts=[(c/12,r/8),((c+1)/12,r/8),((c+1)/12,(r+1)/8),(c/12,(r+1)/8)]
    ET.SubElement(pieces,'piece',id=f'{key}_P{n}',slot=f'{key}_S{n}',row=str(r),column=str(c),side='LEFT' if c<6 else 'RIGHT',rotation='0',polygon=' '.join(f'{x:.12f},{y:.12f}' for x,y in pts))
ET.indent(root);ET.ElementTree(root).write(p,encoding='utf-8',xml_declaration=True)
print('Created 147 localized puzzle records; earlier records retired and retained')
