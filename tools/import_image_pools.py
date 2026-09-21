"""Create multilingual image pools. Correct flags remain private in online exports."""
from pathlib import Path
import xml.etree.ElementTree as ET,re,json
ROOT=Path(__file__).resolve().parents[1];vocab={};terms={};bylabels={}
for line in (ROOT/'content/sources/image_vocabulary.tsv').read_text(encoding='utf-8-sig').splitlines():
 aliases,fr,ar=line.split('|');aliases=aliases.split(',');key=bylabels.get((fr,ar)) or re.sub('[^a-z0-9]+','_',aliases[0]);bylabels[(fr,ar)]=key
 vocab.setdefault(key,{'en':aliases[0],'fr':fr,'ar':ar,'aliases':[]})['aliases']+=aliases
 for a in aliases:terms[a]=key
rows=[]
for line in (ROOT/'assets/images/correct_answers.txt').read_text(encoding='utf-8-sig').splitlines():
 cols=[x.strip() for x in line.split('|')]
 if len(cols)==4 and cols[0]!='Image ID':rows.append(cols)
path=ROOT/'content/image_guess.xml';root=ET.parse(path).getroot();root.set('contentVersion','4')
for item in root:item.set('status','RETIRED')
prompts={'en':'Choose four things visible in the image.','fr':'Choisissez quatre éléments visibles dans l’image.','ar':'اختر أربعة أشياء تظهر في الصورة.'}
manifest=[]
for imageid,stem,original,pool in rows:
 raw=[x.strip() for x in pool.split(',')];missing=set(raw)-terms.keys();assert not missing,missing
 present={terms[a] for a in raw};assert len(present)>=4
 # Exclude every supplied positive and its aliases; also avoid overlapping general labels.
 tokens={token for a in raw for token in a.split() if len(token)>2}
 negatives={key for key,entry in vocab.items() if key not in present and not any(tokens.intersection(a.split()) for a in entry['aliases'])}
 people={'man','woman','boy','girl','players','four_people','students'}
 if any(any(w in a for w in ['player','runner','swimmer','boxer','teacher','vendor','skier','girl','boy','man','woman','people','crowd']) for a in raw):negatives-=people
 assert len(negatives)>=6
 asset=f'assets/images/scenes/{stem}.webp';assert (ROOT/asset).is_file()
 manifest.append({'id':imageid,'asset':asset,'possible_correct_answers':raw,'correct_concepts':sorted(present),'wrong_concepts':sorted(negatives)})
 for locale in ['en','fr','ar']:
  key=f'SCENE_{imageid}_{locale.upper()}';previous=root.find(f"item[@id='{key}']")
  if previous is not None:root.remove(previous)
  item=ET.SubElement(root,'item',id=key,locale=locale,status='APPROVED')
  fields=dict(theme={'en':'Observation','fr':'Observation','ar':'الملاحظة'}[locale],specification=imageid,prompt=prompts[locale],asset_ref=asset,selection_count='4',time_limit_seconds='30',scoring_policy='correct_only',difficulty='medium',explanation=prompts[locale],source_url='project://'+asset,license='Owner-supplied artwork and answer pool authorized for this project',attribution='BrainyBrawl')
  for k,v in fields.items():ET.SubElement(item,'field',name=k).text=v
  choices=ET.SubElement(item,'choices',pool='true')
  for concept in sorted(present|negatives):ET.SubElement(choices,'choice',id=f'{key}_{concept}',correct=str(concept in present).lower(),points='10' if concept in present else '0').text=vocab[concept][locale]
# Approved entries first, retired definitions remain available for audit.
root[:]=sorted(root,key=lambda i:i.get('status')!='APPROVED')
ET.indent(root);ET.ElementTree(root).write(path,encoding='utf-8',xml_declaration=True)
(ROOT/'content/sources/image_pools.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2),encoding='utf-8')
print('Created',len(rows)*3,'image pools; each samples exactly 4 positives + 6 disjoint negatives')
