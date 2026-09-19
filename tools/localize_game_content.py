"""Localize all existing mini-game presentation while preserving gameplay configuration."""
import xml.etree.ElementTree as E
from localize_seed_content import ROOT,field,fields,save,local_copy,THEMES
IMAGES=[
('XVIIe siècle','القرن السابع عشر','Quel sujet historique est représenté ?','ما الموضوع التاريخي الذي تمثله الصورة؟','Voilier|Locomotive à vapeur|Villa romaine|Château|Dirigeable|Sous-marin|Cathédrale|Ferme|Avion militaire|Observatoire','سفينة شراعية|قاطرة بخارية|فيلا رومانية|قلعة|منطاد|غواصة|كاتدرائية|بيت مزرعة|طائرة حربية|مرصد'),
('Vie marine','الحياة البحرية','Quel animal est représenté ?','ما الحيوان الظاهر في الصورة؟','Baleine bleue|Dauphin|Requin|Orque|Lamantin|Phoque|Morse|Thon|Raie|Pieuvre','حوت أزرق|دلفين|قرش|حوت قاتل|خروف البحر|فقمة|فظ|تونة|شفنين|أخطبوط'),
('Monuments','المعالم','Quelle structure est représentée ?','ما المنشأة الظاهرة في الصورة؟','Phare|Tour de château|Moulin à vent|Pont|Observatoire|Clocher|Château d’eau|Barrage|Pagode|Fort','منارة|برج قلعة|طاحونة هوائية|جسر|مرصد|برج أجراس|خزان مياه مرتفع|سد|معبد باغودا|حصن'),
('Désert','الصحراء','Quel environnement est représenté ?','ما البيئة التي تمثلها الصورة؟','Désert de sable|Forêt tropicale|Toundra|Récif corallien|Forêt tempérée|Zone humide|Savane|Prairie alpine|Mangrove|Taïga','صحراء رملية|غابة استوائية|تندرا|شعاب مرجانية|غابة معتدلة|أرض رطبة|سافانا|مرج جبلي|غابة مانغروف|تايغا'),
('Espace','الفضاء','Quel objet céleste est représenté ?','ما الجرم السماوي الظاهر في الصورة؟','Planète|Lune|Comète|Galaxie|Astéroïde|Amas d’étoiles|Nébuleuse|Satellite|Trou noir|Station spatiale','كوكب|قمر|مذنب|مجرة|كويكب|عنقود نجمي|سديم|قمر صناعي|ثقب أسود|محطة فضائية')]
SORTS=[('Éléphant','فيل','Mammifère','ثديي'),('Dauphin','دلفين','Mammifère','ثديي'),('Crocodile','تمساح','Reptile','زاحف'),('Lézard','سحلية','Reptile','زاحف'),('Aigle','نسر','Oiseau','طائر'),('Requin','قرش','Poisson','سمكة'),('Révolution française','الثورة الفرنسية','Avant 1900','قبل 1900'),('Premier alunissage habité','أول هبوط بشري على القمر','Après 1900','بعد 1900'),('L’eau gèle à 0 °C à la pression atmosphérique normale','يتجمد الماء عند صفر مئوي تحت الضغط الجوي القياسي','Vrai','صحيح'),('Le Soleil est une planète','الشمس كوكب','Faux','خطأ')]
REACTIONS_FR='Excellent !|Parfait !|En feu !|Inarrêtable !|Super !|Bien joué !|Correct !|Bravo !|Si près…|Presque !|De peu !|Tout près !|Oups !|Pas tout à fait…|Réessaie !|Raté !|Rapide comme l’éclair !|Le plus rapide !|À toute vitesse !|Quelle vitesse !|Belle équipe !|Synchronisés !|Combo parfait !|Belle coordination !|Ça monte !|Attention !|Du retard !|Nouveau rang !|Victoire !|Tu gagnes !|Belle partie|Si près… Réessaie !'.split('|')
REACTIONS_AR='ممتاز!|رائع!|متألق!|لا تُوقف!|عظيم!|أحسنت!|صحيح!|عمل رائع!|قريب جدًا…|تقريبًا!|فاتتك بقليل!|كنت قريبًا!|عفوًا!|ليس تمامًا…|حاول مجددًا!|فاتتك!|بسرعة البرق!|الأسرع!|سرعة مذهلة!|سريع جدًا!|عمل جماعي رائع!|متناغمون!|تناغم مثالي!|تنسيق رائع!|تتقدم!|انتبه!|تتأخر!|مرتبة جديدة!|انتصار!|فزت!|مباراة رائعة|قريب جدًا… حاول مجددًا!'.split('|')

def main():
 for kind in ['image_guess','speed_sort','reactions','collaborative_puzzle']:
  root=E.parse(ROOT/'content'/f'{kind}.xml').getroot()
  for n in list(root):
   if n.get('locale') in ('fr','ar'):root.remove(n)
  for i,item in enumerate(list(root)):
   if item.get('locale')=='global':continue
   for t,locale in enumerate(('fr','ar')):
    n=local_copy(item,locale);values=fields(n)
    if 'theme' in values:field(n,'theme',THEMES.get(values['theme'],(values['theme'],values['theme']))[t])
    if kind=='image_guess':
     data=IMAGES[i];field(n,'specification',data[t]);field(n,'prompt',data[t+2]);field(n,'explanation',('Illustration de développement : ' if t==0 else 'رسم تجريبي: ')+data[t+4].split('|')[0])
     for j,choice in enumerate(n.findall('choices/choice')):choice.text=data[t+4].split('|')[j];field(n,f'option_{j+1}',choice.text)
    elif kind=='speed_sort':
     field(n,'item_text',SORTS[i][t]);field(n,'correct_bucket',SORTS[i][t+2]);theme=values['set_theme'];field(n,'set_theme',{'Animal Class':('Classes animales','تصنيف الحيوانات'),'Historical Date':('Dates historiques','تواريخ تاريخية'),'True False':('Vrai ou faux','صحيح أم خطأ')}[theme][t])
    elif kind=='reactions':field(n,'text',[REACTIONS_FR,REACTIONS_AR][t][i]);field(n,'localization_key',values['localization_key']+'_'+locale)
    elif kind=='collaborative_puzzle':
     for piece in n.findall('pieces/piece'):piece.set('slot',piece.get('slot')+'_'+locale.upper())
    root.append(n)
  save(root,kind)
 print('Image choices, sorting items/buckets, all reactions and puzzles now cover EN/FR/AR.')
if __name__=='__main__':main()
