"""Reproducible original multilingual numeracy pack; no runtime network translation."""
from pathlib import Path
import xml.etree.ElementTree as E
import random,math,copy
ROOT=Path(__file__).resolve().parents[1]
LOCALES=('en','fr','ar')

def field(item,key,value):
 n=next((x for x in item.findall('field') if x.get('name')==key),None)
 if n is None:n=E.SubElement(item,'field',name=key)
 n.text=str(value)
def save(root,name):
 root.set('contentVersion','2');E.indent(root,space='  ')
 E.ElementTree(root).write(ROOT/'content'/f'{name}.xml',encoding='utf-8',xml_declaration=True)
def math_cases():
 for i in range(1,51):
  a=i+12;b=i%9+3
  yield 'addition',i,(f'What is {a} + {b}?',f'Combien font {a} + {b} ?',f'ما ناتج {a} + {b}؟'),a+b,f'{a} + {b} = {a+b}'
  yield 'subtraction',i,(f'What is {a*3} − {b}?',f'Combien font {a*3} − {b} ?',f'ما ناتج {a*3} − {b}؟'),a*3-b,f'{a*3} − {b} = {a*3-b}'
  yield 'multiplication',i,(f'What is {a} × {b}?',f'Combien font {a} × {b} ?',f'ما ناتج {a} × {b}؟'),a*b,f'{a} × {b} = {a*b}'
  yield 'division',i,(f'What is {a*b} ÷ {b}?',f'Combien font {a*b} ÷ {b} ?',f'ما ناتج {a*b} ÷ {b}؟'),a,f'{a*b} ÷ {b} = {a}'
  yield 'square',i,(f'What is {a} squared?',f'Quel est le carré de {a} ?',f'ما مربع العدد {a}؟'),a*a,f'{a} × {a} = {a*a}'
  yield 'cube',i,(f'What is {i+2} cubed?',f'Quel est le cube de {i+2} ?',f'ما مكعب العدد {i+2}؟'),(i+2)**3,f'{i+2} × {i+2} × {i+2} = {(i+2)**3}'
  yield 'percent',i,(f'What is {b*5}% of {a*20}?',f'Combien font {b*5} % de {a*20} ?',f'كم يساوي {b*5}٪ من {a*20}؟'),a*b,f'{b*5} × {a*20} ÷ 100 = {a*b}'
  yield 'fraction',i,(f'What is one fifth of {a*5}?',f'Combien vaut un cinquième de {a*5} ?',f'ما خُمس العدد {a*5}؟'),a,f'{a*5} ÷ 5 = {a}'
  yield 'minutes',i,(f'How many minutes are in {i+1} hours?',f'Combien de minutes font {i+1} heures ?',f'كم دقيقة في {i+1} ساعات؟'),(i+1)*60,f'{i+1} × 60 = {(i+1)*60}'
  yield 'seconds',i,(f'How many seconds are in {i+2} minutes?',f'Combien de secondes font {i+2} minutes ?',f'كم ثانية في {i+2} دقائق؟'),(i+2)*60,f'{i+2} × 60 = {(i+2)*60}'
  yield 'perimeter',i,(f'A rectangle has sides {a} cm and {b} cm. What is its perimeter in cm?',f'Un rectangle mesure {a} cm sur {b} cm. Quel est son périmètre en cm ?',f'مستطيل طولاه {a} سم و{b} سم. ما محيطه بالسنتيمتر؟'),2*(a+b),f'2 × ({a} + {b}) = {2*(a+b)}'
  yield 'area',i,(f'A rectangle measures {a} m by {b} m. What is its area in square metres?',f'Un rectangle mesure {a} m sur {b} m. Quelle est son aire en mètres carrés ?',f'مستطيل أبعاده {a} م و{b} م. ما مساحته بالمتر المربع؟'),a*b,f'{a} × {b} = {a*b}'
  yield 'triangle',i,(f'A triangle has base {a*2} cm and height {b} cm. What is its area in square cm?',f'Un triangle a une base de {a*2} cm et une hauteur de {b} cm. Quelle est son aire en cm carrés ?',f'مثلث قاعدته {a*2} سم وارتفاعه {b} سم. ما مساحته بالسنتيمتر المربع؟'),a*b,f'{a*2} × {b} ÷ 2 = {a*b}'
  yield 'mean',i,(f'What is the mean of {a-4}, {a}, and {a+4}?',f'Quelle est la moyenne de {a-4}, {a} et {a+4} ?',f'ما المتوسط الحسابي للأعداد {a-4} و{a} و{a+4}؟'),a,f'({a-4} + {a} + {a+4}) ÷ 3 = {a}'
  yield 'range',i,(f'What is the range of {b}, {a}, {a+b}?',f'Quelle est l’étendue de {b}, {a}, {a+b} ?',f'ما المدى للأعداد {b} و{a} و{a+b}؟'),a,f'{a+b} − {b} = {a}'
  yield 'median',i,(f'What is the median of {a+3}, {a-2}, {a}, {a+6}, {a-5}?',f'Quelle est la médiane de {a+3}, {a-2}, {a}, {a+6}, {a-5} ?',f'ما الوسيط للأعداد {a+3} و{a-2} و{a} و{a+6} و{a-5}؟'),a,f'{a-5} < {a-2} < {a} < {a+3} < {a+6}'
  yield 'algebra',i,(f'Solve for x: {b}x + {i} = {a*b+i}.',f'Trouvez x : {b}x + {i} = {a*b+i}.',f'أوجد x: ‏{b}x + {i} = {a*b+i}.'),a,f'({a*b+i} − {i}) ÷ {b} = {a}'
  yield 'distance',i,(f'At {a} km/h, how many km are travelled in {b} hours?',f'À {a} km/h, combien de km parcourt-on en {b} heures ?',f'بسرعة {a} كم/ساعة، كم كيلومترًا تُقطع في {b} ساعات؟'),a*b,f'{a} × {b} = {a*b}'
  yield 'proportion',i,(f'{b} identical notebooks cost {a*b} coins. How many coins do 2 cost?',f'{b} cahiers identiques coûtent {a*b} pièces. Combien coûtent 2 cahiers ?',f'ثمن {b} دفاتر متماثلة هو {a*b} قطعة نقدية. ما ثمن دفترين؟'),a*2,f'{a*b} ÷ {b} × 2 = {a*2}'
  yield 'binary',i,(f'Convert binary {a:b} to decimal.',f'Convertissez le nombre binaire {a:b} en décimal.',f'حوّل العدد الثنائي {a:b} إلى النظام العشري.'),a,f'({a:b})₂ = ({a})₁₀'
  yield 'root',i,(f'What is the positive square root of {a*a}?',f'Quelle est la racine carrée positive de {a*a} ?',f'ما الجذر التربيعي الموجب للعدد {a*a}؟'),a,f'{a}² = {a*a}'
  yield 'sequence',i,(f'Continue this arithmetic sequence: {a}, {a+b}, {a+2*b}, …',f'Continuez cette suite arithmétique : {a}, {a+b}, {a+2*b}, …',f'أكمل المتتالية الحسابية: {a}، {a+b}، {a+2*b}، …'),a+3*b,f'{a+2*b} + {b} = {a+3*b}'
  yield 'sum',i,(f'What is the sum of integers from 1 to {a}, inclusive?',f'Quelle est la somme des entiers de 1 à {a}, inclus ?',f'ما مجموع الأعداد الصحيحة من 1 إلى {a} شاملًا؟'),a*(a+1)//2,f'{a} × {a+1} ÷ 2 = {a*(a+1)//2}'
  yield 'discount',i,(f'An item costs {a*10} coins. After a 20% discount, what is its price?',f'Un article coûte {a*10} pièces. Quel est son prix après une réduction de 20 % ?',f'ثمن سلعة {a*10} قطعة نقدية. ما ثمنها بعد خصم ٢٠٪؟'),a*8,f'{a*10} × 80 ÷ 100 = {a*8}'

def add_math(root):
 seen=set()
 for category,i,prompts,answer,explanation in math_cases():
  assert prompts[0] not in seen;seen.add(prompts[0]);assert answer>0
  rng=random.Random(f'{category}:{i}')
  candidates=sorted({answer+d for d in (-10,-5,-3,-2,-1,1,2,3,5,10) if answer+d>=0})
  choices=[answer]+rng.sample(candidates,4);rng.shuffle(choices)
  for locale,prompt,theme in zip(LOCALES,prompts,['Mathematics & logic','Mathématiques et logique','الرياضيات والمنطق']):
   key=f'NUM_{category}_{i:03}_{locale}'
   item=E.SubElement(root,'item',id=key,locale=locale,status='APPROVED')
   for k,v in {'concept_id':f'NUM_{category}_{i:03}','theme':theme,'difficulty':'standard','question':prompt,'explanation':explanation,'time_limit_seconds':20,'source_note':'Original deterministic educational pack; exact integer arithmetic','review_method':'automated exact arithmetic + authored parallel translations','base_points':1}.items():field(item,k,v)
   options=E.SubElement(item,'options')
   for j,value in enumerate(choices):E.SubElement(options,'option',id=f'{key}_O{j+1}',correct=str(value==answer).lower()).text=str(value)
   aliases=E.SubElement(item,'acceptedAnswers');E.SubElement(aliases,'answer').text=str(answer)
 return len(seen)

def main():
 root=E.parse(ROOT/'content/question_round.xml').getroot()
 for item in list(root):
  if item.get('id','').startswith('NUM_'):root.remove(item)
 count=add_math(root);save(root,'question_round')
 print(f'Generated {count} distinct numeracy/logic questions × 3 languages; not claimed as {count*3} distinct questions.')
if __name__=='__main__':main()
