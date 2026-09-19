"""Deterministic XML authoring migration/validation. Standard library only; never Android runtime XLSX."""
from pathlib import Path
import argparse
import json
import re
import sys
import zipfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
KINDS = ('question_round', 'image_guess', 'collaborative_puzzle', 'precision_tap', 'roll_the_dice', 'word_scramble', 'speed_sort', 'reactions')
NS = {'m': 'http://schemas.openxmlformats.org/spreadsheetml/2006/main'}
STATUSES = {'DEV_SAMPLE', 'DRAFT', 'REVIEW', 'APPROVED', 'RETIRED'}


def workbook(path):
    result = {}
    with zipfile.ZipFile(path) as z:
        shared = []
        if 'xl/sharedStrings.xml' in z.namelist():
            shared = [''.join(x.itertext()) for x in ET.fromstring(z.read('xl/sharedStrings.xml'))]
        sheets = ET.fromstring(z.read('xl/workbook.xml')).find('m:sheets', NS)
        for index, sheet in enumerate(sheets, 1):
            rows = []
            for row in ET.fromstring(z.read(f'xl/worksheets/sheet{index}.xml')).findall('m:sheetData/m:row', NS):
                cells = {}
                for c in row:
                    col = re.match(r'[A-Z]+', c.attrib['r'])[0]
                    number = 0
                    for letter in col: number = number * 26 + ord(letter) - 64
                    v = c.find('m:v', NS)
                    cells[number-1] = shared[int(v.text)] if c.get('t') == 's' and v is not None else ''.join(c.itertext())
                rows.append([cells.get(i, '') for i in range(max(cells, default=-1)+1)])
            result[sheet.attrib['name']] = [dict(zip(rows[0], row)) for row in rows[1:]]
    return result


def field(node, key, value):
    ET.SubElement(node, 'field', name=key).text = str(value)


def record(root, row, id_key, status=None):
    item = ET.SubElement(root, 'item', id=row[id_key], locale=row.get('locale', 'global'),
                         status=status or row.get('status', 'DEV_SAMPLE'))
    for key, value in row.items():
        if key not in (id_key, 'locale', 'status'): field(item, key, value)
    return item


def development_art(asset_id):
    """Original simple vector diagrams for development only, not licensed production art."""
    sky = '<rect width="640" height="400" fill="#123a69"/><circle cx="515" cy="75" r="34" fill="#ffd34d"/>'
    sea = '<path d="M0 270Q80 250 160 270T320 270T480 270T640 270V400H0Z" fill="#167ea8"/>'
    if 'ship' in asset_id:
        art = sky+sea+'<path d="M130 270H490L440 325H190Z" fill="#c58749"/><path d="M315 80V275" stroke="#e9d4ad" stroke-width="12"/><path d="M300 95L180 240H300Z M330 115L450 235H330Z" fill="#f7f4df"/>'
    elif 'whale' in asset_id:
        art=sky+sea+'<ellipse cx="315" cy="220" rx="160" ry="90" fill="#65b8d7"/><path d="M165 215L65 155L90 245L165 260Z M340 270L420 330L395 265Z" fill="#65b8d7"/><circle cx="410" cy="195" r="9" fill="#06152f"/><path d="M370 140Q355 65 305 75M370 140Q385 65 430 90" fill="none" stroke="#b8efff" stroke-width="9"/>'
    elif 'lighthouse' in asset_id:
        art=sky+sea+'<path d="M0 360L230 300L500 360Z" fill="#607a67"/><path d="M240 315L270 120H335L370 315Z" fill="#f6efd8"/><path d="M253 230H353L346 190H259Z" fill="#e84859"/><rect x="258" y="90" width="90" height="42" fill="#ffd34d"/><path d="M248 90L303 55L360 90Z" fill="#e84859"/>'
    elif 'desert' in asset_id:
        art=sky+'<path d="M0 250Q180 100 390 290Q530 180 640 225V400H0Z" fill="#eab86c"/><path d="M0 340Q310 210 640 340V400H0Z" fill="#cc854f"/>'
    else:
        art='<rect width="640" height="400" fill="#06152f"/><circle cx="325" cy="210" r="112" fill="#7663e5"/><path d="M215 180Q320 260 425 175M225 250Q335 310 420 240" stroke="#bfa6ff" stroke-width="24" fill="none"/><ellipse cx="325" cy="215" rx="200" ry="35" transform="rotate(-20 325 215)" stroke="#ffd34d" stroke-width="15" fill="none"/><circle cx="100" cy="80" r="4" fill="white"/><circle cx="535" cy="330" r="5" fill="white"/>'
    path=ROOT/'assets'/'images'/f'{asset_id}.svg'
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(f'<svg xmlns="http://www.w3.org/2000/svg" width="640" height="400" viewBox="0 0 640 400"><title>Development illustration</title>{art}</svg>\n',encoding='utf-8')
    return path.relative_to(ROOT).as_posix()


def migrate():
    data=workbook(ROOT/'content'/'Brainy_Brawl_Content.xlsx')
    roots={k:ET.Element('content',kind=k,schemaVersion='1',contentVersion='1') for k in KINDS}
    for row in data['Questions']:
        item=record(roots['question_round'],row,'question_id')
        options=ET.SubElement(item,'options')
        for i in range(5):
            ET.SubElement(options,'option',id=f'{row["question_id"]}_O{i+1}',correct=str(i==int(row['correct_option_index'])).lower()).text=row[f'option_{i+1}']
    for row in data['ImageGuess']:
        item=record(roots['image_guess'],row,'image_guess_id')
        field(item,'asset_ref',development_art(row['asset_key']))
        field(item,'asset_source','Original procedural development illustration; production review required')
        field(item,'difficulty','standard')
        field(item,'selection_count',4)
        field(item,'scoring_policy','OPEN_DECISION')
        choices=ET.SubElement(item,'choices')
        for i in range(1,11):
            ET.SubElement(choices,'choice',id=f'{row["image_guess_id"]}_O{i}',correct=str(row[f'correct_{i}']=='1').lower(),points=row[f'points_{i}']).text=row[f'option_{i}']
    for row in data['Puzzles']:
        item=record(roots['collaborative_puzzle'],row,'puzzle_id')
        field(item,'asset_ref',development_art(row['asset_key']))
        field(item,'geometry_source','Development tessellation; workbook placement IDs preserved')
        pieces=ET.SubElement(item,'pieces')
        # Variable-width polygons tessellate exactly; the center split remains at x=.5.
        xs=[0,.07,.16,.24,.34,.42,.5,.58,.66,.76,.84,.93,1]
        for source in data['PuzzlePieces']:
            if source['puzzle_id']!=row['puzzle_id']: continue
            r,c=int(source['grid_row']),int(source['grid_col'])
            # Internal row boundaries are slanted, with identical shared endpoints.
            def y(edge,col):
                if edge in (0,8): return edge/8
                return edge/8 + (0.012 if (edge+col)%2 else -0.012)
            points=[(xs[c],y(r,c)),(xs[c+1],y(r,c+1)),(xs[c+1],y(r+1,c+1)),(xs[c],y(r+1,c))]
            ET.SubElement(pieces,'piece',id=source['piece_id'],slot=source['slot_id'],row=str(r),column=str(c),side=source['player_side'],rotation='0',polygon=' '.join(f'{x:.5f},{y:.5f}' for x,y in points))
    for row in data['PrecisionTap']:
        item=record(roots['precision_tap'],row,'config_id',status='DEV_SAMPLE')
        field(item,'original_status',row['status'])
        field(item,'rotation_degrees_per_second',120)
        field(item,'hot_zone_degrees',35)
        field(item,'speed_increment',12)
        field(item,'width_increment',2)
        field(item,'max_hot_zone_degrees',90)
        field(item,'streak_rule','hit_awards_current_streak')
        for f in item.findall('field'):
            if f.get('name')=='streak_bonus_at_3': f.text='2'
    roulette=ET.SubElement(roots['roll_the_dice'],'item',id='ROLL_DEV_20',locale='global',status='DEV_SAMPLE')
    field(roulette,'resolution','server_crypto_random_reroll_ties')
    slots=ET.SubElement(roulette,'slots')
    for i in range(1,21): ET.SubElement(slots,'slot',id=f'ROLL_DEV_20_{i}',value=str(i))
    for row in data['WordScramble']:
        item=record(roots['word_scramble'],row,'scramble_id')
        answers=ET.SubElement(item,'acceptedAnswers');ET.SubElement(answers,'answer').text=row['answer']
    for row in data['SpeedSort']: record(roots['speed_sort'],row,'item_id')
    for row in data['Reactions']:
        item=record(roots['reactions'],row,'reaction_id',status='DEV_SAMPLE')
        field(item,'localization_key',row['reaction_id'].lower())
        field(item,'category','preset')
    for kind,root in roots.items():
        ET.indent(root,space='  ')
        ET.ElementTree(root).write(ROOT/'content'/f'{kind}.xml',encoding='utf-8',xml_declaration=True)
    print('Migrated eight versioned XML files with stable workbook IDs. All art is DEV_SAMPLE.')


def require(condition,message):
    if not condition: raise ValueError(message)


def parse(path):
    raw=path.read_bytes()
    require(len(raw)<=12_000_000,f'{path.name}: too large')
    text=raw.decode('utf-8-sig')
    require('<!DOCTYPE' not in text.upper() and '<!ENTITY' not in text.upper(),f'{path.name}: DTD/entity forbidden')
    return ET.fromstring(text)


def validate(directory=None,production=False):
    directory=Path(directory or ROOT/'content')
    ids=set();count=0
    for kind in KINDS:
        root=parse(directory/f'{kind}.xml')
        require(root.tag=='content' and root.get('kind')==kind,f'{kind}: root contract')
        require(root.get('schemaVersion')=='1' and int(root.get('contentVersion','0'))>0,f'{kind}: version')
        require(len(root)>0,f'{kind}: empty file')
        for item in root:
            key=item.get('id','');require(re.fullmatch(r'[A-Za-z0-9_.-]+',key) and key not in ids,f'{kind}: duplicate/invalid ID {key}')
            ids.add(key);count+=1
            require(item.get('locale') in ('en','fr','ar','global'),f'{key}: locale')
            require(item.get('status') in STATUSES,f'{key}: approval status')
            if production: require(item.get('status')=='APPROVED',f'{key}: unapproved release content')
            fields={f.get('name'):f.text or '' for f in item.findall('field')}
            require(len(fields)==len(item.findall('field')),f'{key}: duplicate field')
            def has(*names):
                for name in names: require(fields.get(name,'').strip(),f'{key}: missing {name}')
            for f in fields:
                require(not re.search(r'service.?role|password|secret',f,re.I),f'{key}: forbidden field')
            if kind=='question_round':
                has('question','theme','difficulty','explanation')
                options=item.findall('options/option')
                require(len(options)==5 and sum(o.get('correct')=='true' for o in options)==1,f'{key}: five options/one correct required')
                require(int(fields['time_limit_seconds'])==20,f'{key}: timer')
            elif kind=='image_guess':
                has('theme','specification','prompt','explanation','asset_ref','difficulty')
                options=item.findall('choices/choice')
                require(len(options)==10 and int(fields['selection_count'])==4 and int(fields['time_limit_seconds'])==30,f'{key}: image contract')
                require(all(0<=int(o.get('points','-1'))<=250 for o in options),f'{key}: points')
                if production: require(fields['scoring_policy'] in ('all_selected','correct_only'),f'{key}: scoring approval')
            elif kind=='collaborative_puzzle':
                has('asset_ref');options=[]
                pieces=item.findall('pieces/piece')
                require(len(pieces)==96 and fields['columns']=='12' and fields['rows']=='8' and fields['piece_count']=='96',f'{key}: puzzle size')
                positions=set();slots=set()
                for piece in pieces:
                    pid=piece.get('id');require(pid and pid not in ids,f'{key}: duplicate piece');ids.add(pid)
                    r,c=int(piece.get('row')),int(piece.get('column'))
                    require(0<=r<8 and 0<=c<12 and (r,c) not in positions,f'{pid}: position');positions.add((r,c))
                    require(piece.get('slot') not in slots,f'{pid}: duplicate slot');slots.add(piece.get('slot'))
                    require(piece.get('side')==('LEFT' if c<6 else 'RIGHT'),f'{pid}: owner')
                    points=[tuple(map(float,p.split(','))) for p in piece.get('polygon','').split()]
                    require(len(points)>=4 and all(0<=x<=1 and 0<=y<=1 for x,y in points),f'{pid}: geometry')
                require(fields['time_limit_seconds']=='120',f'{key}: puzzle timer')
            elif kind=='precision_tap':
                options=[];has('rotation_degrees_per_second','hot_zone_degrees','streak_rule')
                require(fields['turn_seconds']=='20' and fields['streak_bonus_at_3']=='2',f'{key}: written precision rules')
                require(0<float(fields['hot_zone_degrees'])<360 and float(fields['rotation_degrees_per_second'])>0,f'{key}: precision configuration')
            elif kind=='roll_the_dice':
                options=item.findall('slots/slot')
                require(len(options)==20 and {int(o.get('value')) for o in options}==set(range(1,21)),f'{key}: twenty-slot roulette')
            elif kind=='word_scramble':
                options=[];has('answer','theme','difficulty','shuffled_letters')
                require(10<=int(fields['time_limit_seconds'])<=15,f'{key}: timer')
                normalize=lambda x: sorted(c.casefold() for c in x if c.isalnum())
                require(normalize(fields['answer'])==normalize(fields['shuffled_letters']),f'{key}: scramble letters')
                require(any(a.text and a.text.strip() for a in item.findall('acceptedAnswers/answer')),f'{key}: accepted answer')
            elif kind=='speed_sort':
                options=[];has('item_text','correct_bucket','set_theme','difficulty')
                require(fields['points']=='1',f'{key}: sort score')
            else:
                options=[];has('text','localization_key','trigger','category')
            for option in options:
                oid=option.get('id');require(oid and oid not in ids,f'{key}: duplicate option');ids.add(oid)
                if kind!='roll_the_dice': require(option.text and option.text.strip(),f'{key}: empty option')
            if 'asset_ref' in fields:
                asset=(ROOT/fields['asset_ref']).resolve()
                require(asset.is_relative_to(ROOT/'assets') and asset.is_file(),f'{key}: missing/unsafe asset')
                if production: has('source_url','license','attribution')
    print(f'Validated {len(KINDS)} XML files, {count} records, {len(ids)} unique record/option/piece IDs.')
    return count



def sanitized(item,kind):
    """Separate display content from server-only scoring/explanations."""
    fields={f.get('name'):f.text or '' for f in item.findall('field')}
    public={k:fields[k] for k in ('theme','difficulty','asset_ref','specification') if k in fields}
    secret={}
    if kind=='question_round':
        public['prompt']=fields['question']
        public['options']=[{'id':o.get('id'),'label':o.text} for o in item.findall('options/option')]
        secret={'correct_option_id':next(o.get('id') for o in item.findall('options/option') if o.get('correct')=='true'),'explanation':fields['explanation'],'accepted_answers':[a.text for a in item.findall('acceptedAnswers/answer')] or [next(o.text for o in item.findall('options/option') if o.get('correct')=='true')]}
    elif kind=='image_guess':
        public['prompt']=fields['prompt'];public['selection_count']=4
        public['options']=[{'id':o.get('id'),'label':o.text} for o in item.findall('choices/choice')]
        secret={'scoring_policy':fields['scoring_policy'],'explanation':fields['explanation'],
                'choices':[{'id':o.get('id'),'correct':o.get('correct')=='true','points':int(o.get('points'))} for o in item.findall('choices/choice')]}
    elif kind=='word_scramble':
        public['letters']=fields['shuffled_letters']
        secret={'accepted_answers':[a.text for a in item.findall('acceptedAnswers/answer')],
                'full_points':int(fields['full_points']),'reduced_points':int(fields['reduced_points'])}
    elif kind=='speed_sort':
        public.update(label=fields['item_text'],theme=fields['set_theme'])
        secret={'bucket':fields['correct_bucket'],'points':1}
    elif kind=='collaborative_puzzle':
        public['piece_count']=96
        secret['pieces']=[dict(p.attrib) for p in item.findall('pieces/piece')]
    elif kind=='precision_tap':
        public.update({k:fields[k] for k in ('rotation_degrees_per_second','hot_zone_degrees','speed_increment','width_increment','max_hot_zone_degrees')})
        secret['streak_rule']='hit_awards_current_streak'
    elif kind=='roll_the_dice':
        public['slots']=[int(slot.get('value')) for slot in item.findall('slots/slot')]
        secret['resolution']='server_crypto_random_reroll_ties'
    elif kind=='reactions':
        public.update(localization_key=fields['localization_key'],text=fields['text'],trigger=fields['trigger'],category=fields['category'])
    return public,secret


def export_approved(output, directory=None, exclude_ids=None):
    directory=Path(directory) if directory is not None else ROOT/'content'
    validate(directory)
    approved=[]
    for kind in KINDS:
        root=parse(directory/f'{kind}.xml')
        for item in root.findall('item'):
            if item.get('status')!='APPROVED' or item.get('id') in (exclude_ids or set()): continue
            fields={f.get('name'):f.text or '' for f in item.findall('field')}
            if kind=='image_guess': require(fields.get('scoring_policy') in ('all_selected','correct_only'),'Image scoring approval required')
            if 'asset_ref' in fields:
                require(all(fields.get(k,'').strip() for k in ('source_url','license','attribution')),'Asset licensing approval metadata required')
            display,answer=sanitized(item,kind)
            approved.append((item,kind,root.get('contentVersion'),display,answer))
    require(approved,'No approved content available; output not written')
    literal=lambda value:"'"+str(value).replace("'","''")+"'"
    lines=['-- Generated validated APPROVED content. Review before any deployment.','begin;','set local standard_conforming_strings=on;']
    for item,kind,version,display,answer in approved:
        key=literal(item.get('id'))
        # Immutable IDs: conflicting IDs must be intentionally versioned; never rewrite
        # content used by an active/completed match through a blanket upsert.
        values=','.join([key,literal(kind),literal(item.get('locale')),'1',str(int(version)),"'APPROVED'",literal(json.dumps(display,ensure_ascii=False))+'::jsonb'])
        lines.append(f'insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) values({values});')
        lines.append(f'insert into private.content_answers(content_id,answer) values({key},{literal(json.dumps(answer,ensure_ascii=False))}::jsonb);')
        if kind=='reactions':
            locale=item.get('locale')
            require(locale in ('en','fr','ar'),'Reactions require a concrete locale')
            lines.append(f"insert into public.reactions(id,localization_key,context,approved) values({key},{literal(display['localization_key'])},{literal(display['trigger'])},true);")
            lines.append(f"insert into public.localization_entries(key,locale,value) values({literal(display['localization_key'])},{literal(locale)},{literal(display['text'])});")
    lines.append('commit;')
    Path(output).write_text('\n'.join(lines)+'\n',encoding='utf-8')
    print(f'Exported {len(approved)} approved records; public payloads contain no answer keys.')


if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('command',choices=['migrate','validate','export'])
    parser.add_argument('--production',action='store_true')
    parser.add_argument('--output',type=Path)
    args=parser.parse_args()
    try:
        if args.command=='migrate': migrate()
        if args.command=='export':
            require(args.output is not None,'--output is required for export')
            export_approved(args.output)
        else: validate(production=args.production)
    except (ValueError,KeyError,ET.ParseError,OSError) as error:
        print(f'CONTENT ERROR: {error}',file=sys.stderr);sys.exit(1)
