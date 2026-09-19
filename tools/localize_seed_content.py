"""Parallel EN/FR/AR display content and explicit accepted translations."""
from pathlib import Path
import xml.etree.ElementTree as E
import copy
from enrich_content import ROOT,field,save
# Parallel options retain semantic order; all translations are authored, not runtime machine calls.
QUESTIONS=[
("Quelle est la plus grande planète du Système solaire ?","ما أكبر كوكب في المجموعة الشمسية؟","Mercure|Terre|Jupiter|Mars|Vénus","عطارد|الأرض|المشتري|المريخ|الزهرة"),
("Quel gaz les plantes absorbent-elles principalement pour la photosynthèse ?","ما الغاز الذي تمتصه النباتات أساسًا في البناء الضوئي؟","Oxygène|Azote|Dioxyde de carbone|Hydrogène|Hélium","الأكسجين|النيتروجين|ثاني أكسيد الكربون|الهيدروجين|الهيليوم"),
("Quelle civilisation a construit les pyramides de Gizeh ?","أي حضارة بنت أهرامات الجيزة؟","Romains|Égyptiens de l’Antiquité|Vikings|Mayas|Perses","الرومان|المصريون القدماء|الفايكنغ|المايا|الفرس"),
("Quelle est la capitale du Japon ?","ما عاصمة اليابان؟","Kyoto|Osaka|Tokyo|Nara|Sapporo","كيوتو|أوساكا|طوكيو|نارا|سابورو"),
("Quel organe pompe le sang dans le corps humain ?","ما العضو الذي يضخ الدم في جسم الإنسان؟","Foie|Poumon|Cœur|Rein|Estomac","الكبد|الرئة|القلب|الكلية|المعدة"),
("Quel est le plus grand océan ?","ما أكبر محيط؟","Atlantique|Indien|Arctique|Pacifique|Austral","الأطلسي|الهندي|المتجمد الشمالي|الهادئ|الجنوبي"),
("Quel est le nom courant de H₂O ?","ما الاسم الشائع للمادة H₂O؟","Sel|Eau|Oxygène|Hydrogène|Dioxyde de carbone","الملح|الماء|الأكسجين|الهيدروجين|ثاني أكسيد الكربون"),
("Qui a écrit Roméo et Juliette ?","من كتب روميو وجولييت؟","William Shakespeare|Jane Austen|Charles Dickens|Mark Twain|Homère","ويليام شكسبير|جين أوستن|تشارلز ديكنز|مارك توين|هوميروس"),
("Combien font 12 × 8 ?","ما ناتج 12 × 8؟","86|96|108|88|92","86|96|108|88|92"),
("Sur quel continent se trouve le Sahara ?","في أي قارة تقع الصحراء الكبرى؟","Asie|Amérique du Sud|Afrique|Europe|Australie","آسيا|أمريكا الجنوبية|أفريقيا|أوروبا|أستراليا"),
("Quelle force attire les objets vers la Terre ?","ما القوة التي تجذب الأجسام نحو الأرض؟","Magnétisme|Frottement|Gravité|Électricité|Pression","المغناطيسية|الاحتكاك|الجاذبية|الكهرباء|الضغط"),
("Dans quelle ville se trouve le Colisée ?","في أي مدينة يقع الكولوسيوم؟","Athènes|Rome|Paris|Le Caire|Istanbul","أثينا|روما|باريس|القاهرة|إسطنبول"),
("Quel pays a une forme qui évoque une botte ?","أي دولة يشبه شكلها الحذاء الطويل؟","Grèce|Italie|Chili|Portugal|Norvège","اليونان|إيطاليا|تشيلي|البرتغال|النرويج"),
("Quelle partie de la plante absorbe généralement l’eau du sol ?","أي جزء من النبات يمتص الماء عادة من التربة؟","Fleur|Feuille|Racine|Fruit|Tige","الزهرة|الورقة|الجذر|الثمرة|الساق"),
("Combien de jours compte une année bissextile ?","كم يومًا في السنة الكبيسة؟","364|365|366|367|360","364|365|366|367|360"),
("Quel fleuve traverse l’Égypte ?","أي نهر يمر عبر مصر؟","Amazone|Nil|Danube|Yangtsé|Mississippi","الأمازون|النيل|الدانوب|اليانغتسي|المسيسيبي"),
("Quelle planète est connue pour ses anneaux remarquables ?","أي كوكب يشتهر بحلقاته البارزة؟","Mars|Vénus|Saturne|Mercure|Terre","المريخ|الزهرة|زحل|عطارد|الأرض"),
("Quelle invention est associée à Johannes Gutenberg ?","أي اختراع يرتبط باسم يوهانس غوتنبرغ؟","Machine à vapeur|Presse à imprimer|Téléphone|Avion|Boussole","المحرك البخاري|المطبعة|الهاتف|الطائرة|البوصلة"),
("Combien font 15 % de 200 ?","كم يساوي 15٪ من 200؟","15|20|25|30|35","15|20|25|30|35"),
("Quel instrument possède généralement 88 touches ?","أي آلة موسيقية تحتوي عادة على 88 مفتاحًا؟","Violon|Piano|Flûte|Trompette|Harpe","الكمان|البيانو|الناي|البوق|القيثارة"),
("Dans quelle chaîne de montagnes se trouve l’Everest ?","في أي سلسلة جبلية يقع إيفرست؟","Andes|Alpes|Himalaya|Rocheuses|Atlas","الأنديز|الألب|الهيمالايا|روكي|الأطلس"),
("À quelle température Celsius l’eau bout-elle à la pression atmosphérique normale ?","عند أي درجة مئوية يغلي الماء تحت الضغط الجوي القياسي؟","50|75|90|100|120","50|75|90|100|120"),
("Quelle ville a été ensevelie lors de l’éruption du Vésuve en 79 ?","أي مدينة طمرها ثوران فيزوف عام 79 ميلاديًا؟","Pompéi|Sparte|Carthage|Alexandrie|Troie","بومبي|إسبرطة|قرطاج|الإسكندرية|طروادة"),
("Quelle couleur obtient-on généralement en mélangeant de la peinture bleue et jaune ?","ما اللون الناتج عادة من مزج الطلاء الأزرق والأصفر؟","Violet|Orange|Vert|Rouge|Noir","البنفسجي|البرتقالي|الأخضر|الأحمر|الأسود"),
("Quelle est la plus petite unité d’un élément qui conserve ses propriétés chimiques ?","ما أصغر وحدة من العنصر تحتفظ بخواصه الكيميائية؟","Cellule|Atome|Molécule|Tissu|Cristal","الخلية|الذرة|الجزيء|النسيج|البلورة")]
THEMES={'General Knowledge':('Culture générale','ثقافة عامة'),'Science':('Sciences','العلوم'),'History':('Histoire','التاريخ'),'Geography':('Géographie','الجغرافيا'),'Nature':('Nature','الطبيعة'),'Architecture':('Architecture','العمارة'),'Mathematics':('Mathématiques','الرياضيات'),'Art':('Art','الفن'),'Arts':('Arts','الفنون'),'Music':('Musique','الموسيقى'),'Literature':('Littérature','الأدب'),'Math':('Mathématiques','الرياضيات')}
def fields(item):return {f.get('name'):f.text or '' for f in item.findall('field')}
def local_copy(item,locale):
 n=copy.deepcopy(item);n.set('locale',locale);suffix='_'+locale.upper();n.set('id',item.get('id')+suffix)
 for child in n.iter():
  if child is not n and child.get('id'):child.set('id',child.get('id')+suffix)
 return n

def questions():
 root=E.parse(ROOT/'content/question_round.xml').getroot()
 for n in list(root):
  if n.get('id','').startswith('Q_EN_') and n.get('locale')!='en':root.remove(n)
 for i,data in enumerate(QUESTIONS,1):
  original=next(n for n in root if n.get('id')==f'Q_EN_{i:04}')
  options=original.findall('options/option');correct=next(j for j,o in enumerate(options) if o.get('correct')=='true')
  texts=[o.text for o in options];translated=[data[2].split('|'),data[3].split('|')]
  answers=[texts[correct],translated[0][correct],translated[1][correct]]
  for locale,item in [('en',original),('fr',local_copy(original,'fr')),('ar',local_copy(original,'ar'))]:
   for old in item.findall('acceptedAnswers'):item.remove(old)
   aliases=E.SubElement(item,'acceptedAnswers')
   for answer in dict.fromkeys(answers):E.SubElement(aliases,'answer').text=answer
   field(item,'concept_id',f'Q_BASE_{i:04}')
   if locale!='en':
    t=0 if locale=='fr' else 1;field(item,'question',data[t]);theme=fields(item)['theme'];field(item,'theme',THEMES.get(theme,(theme,theme))[t])
    field(item,'explanation',('Réponse : ' if t==0 else 'الإجابة: ')+translated[t][correct])
    for j,o in enumerate(item.findall('options/option')):o.text=translated[t][j];field(item,f'option_{j+1}',o.text)
    root.append(item)
 save(root,'question_round')

def scrambles():
 translations=[('Pyramide','هرم'),('Jupiter','المشتري'),('Phare','منارة'),('Requin','قرش'),('Volcan','بركان'),('Boussole','بوصلة'),('Himalaya','الهيمالايا'),('Gutenberg','غوتنبرغ'),('Saturne','زحل'),('Pompéi','بومبي')]
 root=E.parse(ROOT/'content/word_scramble.xml').getroot()
 for n in list(root):
  if n.get('locale')!='en':root.remove(n)
 for item,(fr,ar) in zip(list(root),translations):
  values=fields(item);answers=[values['answer'],fr,ar]
  for locale,n in [('en',item),('fr',local_copy(item,'fr')),('ar',local_copy(item,'ar'))]:
   for old in n.findall('acceptedAnswers'):n.remove(old)
   aliases=E.SubElement(n,'acceptedAnswers')
   for a in answers:E.SubElement(aliases,'answer').text=a
   if locale!='en':
    t=0 if locale=='fr' else 1;answer=[fr,ar][t];field(n,'answer',answer);field(n,'shuffled_letters',answer[::-1]);field(n,'theme',THEMES[values['theme']][t]);root.append(n)
 save(root,'word_scramble')
if __name__=='__main__':questions();scrambles();print('Localized 25 seed questions and 10 scrambles with shared cross-language aliases.')
