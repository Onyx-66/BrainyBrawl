"""Prepare a reviewable deployment bundle. No database writes. Optional --incremental performs a read-only comparison."""
from pathlib import Path
from hashlib import sha256
from local_config import ROOT,read_env
from content_pipeline import export_approved,sanitized,KINDS
import argparse,json,xml.etree.ElementTree as E

def main():
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--incremental',action='store_true',help='Read existing content and export only byte-equivalent-new IDs; no database writes');args=parser.parse_args()
    excluded=set()
    if args.incremental:
        from database_connection import connect
        c=connect();cursor=c.cursor();cursor.execute('set transaction read only')
        try:
            cursor.execute('select c.id,c.kind,c.locale,c.content_version,c.payload,a.answer from public.content_items c join private.content_answers a on a.content_id=c.id')
            existing={r[0]:r[1:] for r in cursor.fetchall()}
            for kind in KINDS:
                root=E.parse(ROOT/'content'/f'{kind}.xml').getroot()
                for item in root:
                    key=item.get('id')
                    if key not in existing:continue
                    display,answer=sanitized(item,kind);stored=existing[key]
                    if list(stored)!=[kind,item.get('locale'),int(root.get('contentVersion')),display,answer]:raise ValueError('Published content changed; assign a new stable ID: '+key)
                    excluded.add(key)
        finally:c.rollback();c.close()
        print('Verified unchanged published content IDs:',len(excluded))
    output=ROOT/'.local/backend';output.mkdir(parents=True,exist_ok=True)
    migrations=sorted((ROOT/'supabase/migrations').glob('*.sql'))
    (output/'schema.sql').write_text('\n'.join('-- '+p.name+'\n'+p.read_text(encoding='utf-8') for p in migrations),encoding='utf-8')
    export_approved(output/'approved-content.sql',exclude_ids=excluded)
    (output/'PLAN.md').write_text('# Prepared backend changes\n\n'+str(len(migrations))+' versioned migrations. Includes default-deny game schema, private answer keys, multiplayer functions, levels/admin assignment, multilingual scoring and private photo storage.\n\n'+'\n'.join('- '+p.name+' '+sha256(p.read_bytes()).hexdigest() for p in migrations)+'\n\nContent: 1200 numeracy/logic questions in three languages. All launch packs passed content validation. Image Guess uses correct-only points and boosts are optional by owner decision. Solo uses the owner-delegated 20s precision, 90s sort, fifteen 30s questions schedule. This bundle is not production deployment approval.\n',encoding='utf-8')
    values=read_env();url=values.get('DIRECT_URL') or values.get('DATABASE_URL','')
    print('Prepared .local/backend/schema.sql, approved-content.sql and PLAN.md.')
    if not url or '[YOUR-PASSWORD]' in url:print('Database connection still needs the real project password in DIRECT_URL.')
if __name__=='__main__':main()
