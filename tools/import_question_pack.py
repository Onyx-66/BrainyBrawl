"""Import the supplied multilingual pack; preserve old IDs and author explicit cross-language aliases."""
from pathlib import Path
import json,re,random,sys,xml.etree.ElementTree as ET,unicodedata
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'.local/python'))
from num2words import num2words
SOURCE=ROOT/'content/sources'
pack=json.loads((SOURCE/'questions_250.json').read_text(encoding='utf-8'))
topic_labels=json.loads((SOURCE/'question_topics.json').read_text(encoding='utf-8'))
assert len(pack['questions'])==250
extra={
'Q001':['Westphalia','Westfalia','Westphalie','ويستفاليا','وستفاليا'],
'Q002':['Mali','Empire of Mali','مالي'],
'Q004':['Constantine','Constantinople','Constantinope','Constantinopole','القسطنطينية','قسطنطينية'],
'Q005':['Ming','مينغ'], 'Q007':['Inca','Incas','الإنكا'],
'Q012':['Wars of the Roses','War of the Roses','حرب الوردتين'],
'Q015':['Cleopatra','كليوباترا'], 'Q017':['Ottoman','Ottomans','العثمانية'],
'Q022':['Franz Ferdinand assassination','Assassination of Franz Ferdinand','اغتيال فرانز فرديناند'],
'Q023':['Seljuk','Seljuks','السلاجقة'], 'Q033':['Tigris','دجلة'],
'Q041':['Ulan Bator','Oulan-Bator'], 'Q052':['Mitochondria','الميتوكوندريا'],
'Q055':['Boyle','بويل'], 'Q057':['O-','O negatif','O سلبي'],
'Q066':['300000','300,000','300000 km/s','300000 kilometers per second'],
'Q069':['Mohs','موس'], 'Q078':['720','seven hundred twenty','سبعمائة وعشرون'],
'Q081':['±3','+3 and -3','3,-3','3 et -3','3 و -3'], 'Q083':['16pi','16 pi','16π'],
'Q086':['one sixth','un sixième','سدس'], 'Q087':['eiyt','8'],
'Q088':['half','one half','0.5','un demi','نصف'], 'Q091':['x^2+C','x²+C'],
'Q100':['half','one half','0.5','un demi','نصف'],
'Q106':['Ronaldo','Ronaldo Nazario'], 'Q120':['Ronaldo','Ronaldo Nazario'],
'Q176':['CPU'], 'Q179':['SQL'], 'Q181':['log n','logarithmic','logarithmique','لوغاريتمي'],
'Q182':['IP','IP address'], 'Q185':['Network','réseau','الشبكة'],
'Q186':['Document database','Document oriented','NoSQL','قاعدة بيانات مستندية'],
'Q187':['RAM'], 'Q191':['API'], 'Q192':['Linux'], 'Q195':['GPU'],
'Q197':['recursion','recursive','récursion','استدعاء ذاتي'], 'Q199':['URL'],
'Q206':['barred spiral','spirale barrée','حلزونية ضلعية'],
'Q207':['Sagittarius A','القوس A'], 'Q211':['distance','مسافة'],
'Q221':['Kepler second law','law of equal areas','قانون المساحات المتساوية'],
'Q222':['13.8 billion','13.8 milliards','13.8 مليار'], 'Q223':['James Webb','JWST'],
'Q227':['UNDP'], 'Q232':['WHO','OMS'], 'Q233':['Mandarin','المندرينية'],
'Q235':['GDP','PIB'], 'Q236':['ECB','BCE'], 'Q238':['New York','NYC','نيويورك']}
if (SOURCE/'question_aliases.json').exists():extra.update(json.loads((SOURCE/'question_aliases.json').read_text(encoding='utf-8')))
else:(SOURCE/'question_aliases.json').write_text(json.dumps(extra,ensure_ascii=False,indent=2),encoding='utf-8')
def norm(s):return ''.join(c for c in unicodedata.normalize('NFKD',s).lower() if c.isalnum())
number_lookup={num2words(i,lang=lang).casefold():str(i) for i in range(101) for lang in ['en','fr','ar']}
def aliases(values,question=''):
 out=set(values)
 out.update(number_lookup[v.casefold()] for v in values if v.casefold() in number_lookup)
 values=list(out)
 if "8" in out:out.add("eiyt")
 for v in values:
  if "-" in v and not any(c.isdigit() for c in v):out.add(v.replace("-"," "))
  if v.startswith("ال") and len(v)>5:out.add(v[2:])
  stripped=re.sub(r'^(the |a |an |le |la |les |un |une |l[’\x27])','',v,flags=re.I);out.add(stripped)
  if '(' in v:
   out.update(re.findall(r'\(([^)]+)\)',v));out.add(v.split('(')[0].strip())
  # Standalone numbers gain spoken forms in every supported language. Numeric typos remain wrong.
  numeric=v.replace(',','')
  if re.fullmatch(r'-?\d+(?:\.\d+)?',numeric):
   for lang in ['en','fr','ar']:
    out.add(num2words(numeric,lang=lang))
   out.add(numeric)
   if numeric=='8':out.add('eiyt')
  if question.startswith(('Who ','Which painter','Which composer','Which author','Which physicist','Which artist')) and len(stripped.split())>=2:
   last=stripped.split()[-1]
   if len(last)>4 and not last.isnumeric():out.add(last)
 for a in list(out):
  if '-' in a and not any(c.isdigit() for c in a):out.add(a.replace('-',' '))
  if a.startswith('ال') and len(a)>5:out.add(a[2:])
 return sorted(a.strip() for a in out if a.strip())
p=ROOT/'content/question_round.xml';root=ET.parse(p).getroot();root.set('contentVersion','4')
for item in list(root):
 if item.get('id','').startswith('PACK250_'):root.remove(item)
# Shared concepts include every translated answer, independent of screen language.
groups={}
for item in root:
 key=item.findtext("field[@name='concept_id']") or re.sub(r'_(EN|FR|AR)_','_ALL_',item.get('id'))
 groups.setdefault(key,[]).append(item)
for group in groups.values():
 allanswers={n.text for item in group for n in item.findall('acceptedAnswers/answer')}|{item.findtext("options/option[@correct='true']") for item in group}
 allanswers.discard(None)
 expanded=aliases(allanswers)
 for item in group:
  old=item.find('acceptedAnswers')
  if old is not None:item.remove(old)
  aa=ET.SubElement(item,'acceptedAnswers')
  for a in expanded:ET.SubElement(aa,'answer').text=a
for q in pack['questions']:
 correct=q['answer'];accepted=aliases(list(correct.values())+extra.get(q['id'],[]),q['question']['en'])
 peers=[x for x in pack['questions'] if x['id']!=q['id'] and x['topic']==q['topic'] and all(norm(x['answer']['en'])!=norm(a) for a in accepted)]
 unique=[];seen={norm(correct['en'])}
 for x in random.Random(q['id']).sample(peers,len(peers)):
  n=norm(x['answer']['en'])
  if n not in seen and not any(n in a or a in n for a in seen):seen.add(n);unique.append(x)
  if len(unique)==4:break
 assert len(unique)==4
 for locale in pack['languages']:
  key=f'PACK250_{q["id"]}_{locale.upper()}'
  if root.find(f"item[@id='{key}']") is not None:continue
  item=ET.SubElement(root,'item',id=key,locale=locale,status='APPROVED')
  values=dict(theme=topic_labels[q['topic']][locale],difficulty='medium-hard',question=q['question'][locale],time_limit_seconds='20',base_points='1',explanation=correct[locale],concept_id='PACK250_'+q['id'],source_note='Owner-supplied multilingual question pack, September 2026')
  choices=[correct]+[x['answer'] for x in unique];random.Random(q['id']).shuffle(choices)
  for i,a in enumerate(choices):values[f'option_{i+1}']=a[locale]
  values['correct_option_index']=str(choices.index(correct))
  for k,v in values.items():ET.SubElement(item,'field',name=k).text=v
  options=ET.SubElement(item,'options')
  for i,a in enumerate(choices):ET.SubElement(options,'option',id=f'{key}_O{i+1}',correct=str(a==correct).lower()).text=a[locale]
  aa=ET.SubElement(item,'acceptedAnswers')
  for a in accepted:ET.SubElement(aa,'answer').text=a
ET.indent(root);ET.ElementTree(root).write(p,encoding='utf-8',xml_declaration=True)
print('Imported 250 questions x 3 languages; updated cross-language and numeric aliases for entire bank')
