"""Prepare and optionally publish new XML records; retire replaced packs without deleting history.
Always run the backend tests first. Default is a read-only preview.
"""
import argparse,sys,json,hashlib,xml.etree.ElementTree as ET
from pathlib import Path
from urllib.parse import urlsplit
from database_connection import connect
from local_config import ROOT,read_env
from content_pipeline import export_approved,sanitized,validate,KINDS

def main():
 args=argparse.ArgumentParser(description=__doc__);args.add_argument('--apply',action='store_true');args.add_argument('--confirmed-project');opt=args.parse_args()
 env=read_env();ref=urlsplit(env['SUPABASE_URL']).hostname.split('.')[0]
 if opt.apply and opt.confirmed_project!=ref:raise ValueError('Exact project confirmation required')
 validate(production=True)
 c=connect(timeout=120);q=c.cursor()
 try:
  q.execute('select id from public.content_items');existing={r[0] for r in q.fetchall()}
  out=ROOT/'.local/backend/content-1.0.8.sql';out.parent.mkdir(parents=True,exist_ok=True)
  records=[i for k in KINDS for i in ET.parse(ROOT/'content'/f'{k}.xml').getroot()]
  if any(i.get('status')=='APPROVED' and i.get('id') not in existing for i in records):
   export_approved(out,exclude_ids=existing)
  else:out.write_text('-- No new records to insert.\n',encoding='utf-8')
  retired=[i.get('id') for i in records if i.get('status')=='RETIRED' and i.get('id') in existing]
  aliases=[(json.dumps(sanitized(i,'question_round')[1],ensure_ascii=False),i.get('id')) for i in ET.parse(ROOT/'content/question_round.xml').getroot() if i.get('id') in existing and i.get('status')=='APPROVED']
  print('Plan: new records',sum(i.get('status')=='APPROVED' and i.get('id') not in existing for i in records),'; retire',len(retired),'; refresh existing question aliases',len(aliases))
  if not opt.apply:return
  q.execute("select count(*) from public.matches where status not in ('results','closed')")
  if q.fetchone()[0]:raise ValueError('Active matches exist; publish when no match is running')
  # One transaction makes the replacement atomic. Exported IDs are immutable; old rounds remain readable.
  sql=out.read_text(encoding='utf-8');lines=[line for line in sql.splitlines() if line.strip().lower() not in ('begin;','commit;')]
  for n in range(0,len(lines),100):q.execute('\n'.join(lines[n:n+100]))
  for id in retired:q.execute("update public.content_items set status='RETIRED' where id=%s",(id,))
  for n in range(0,len(aliases),100):
   data=json.dumps([{'content_id':id,'answer':json.loads(answer)} for answer,id in aliases[n:n+100]],ensure_ascii=False)
   q.execute('update private.content_answers a set answer=x.answer from jsonb_to_recordset(%s::jsonb) as x(content_id text,answer jsonb) where a.content_id=x.content_id',(data,))
  c.commit();print('Published content and multilingual aliases atomically; existing records retained')
 finally:c.close()
if __name__=='__main__':
 try:main()
 except Exception as e:
  # Do not echo connection strings, credentials or complete SQL parameters.
  print('Content publication failed:',type(e).__name__);sys.exit(1)
