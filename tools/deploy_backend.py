"""Apply reviewed migrations to the explicitly authorized project. Dry-run by default.
Requires pg8000, installed into the repository-local .local/python directory.
"""
import argparse,hashlib,ssl,sys
from urllib.parse import urlsplit,unquote
from local_config import ROOT,read_env

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apply',action='store_true')
    parser.add_argument('--content',action='store_true',help='Also import the approved content bundle prepared by prepare_backend.py')
    parser.add_argument('--confirmed-project',help='Exact Supabase project ref after owner authorization')
    args=parser.parse_args();env=read_env()
    ref=urlsplit(env.get('SUPABASE_URL','')).hostname.split('.')[0]
    url=env.get('DIRECT_URL') or env.get('DATABASE_URL','')
    if not url or '[YOUR-PASSWORD]' in url:print('Required: DIRECT_URL with the real database password.');return 2
    parsed=urlsplit(url)
    if not parsed.password or parsed.port==6543:print('Use a session/direct connection, not the transaction pooler.');return 2
    if not args.apply:print('Connection configuration is present. No database changes made.');return 0
    if args.confirmed_project!=ref:print('Exact authorized project confirmation is required.');return 2
    sys.path.insert(0,str(ROOT/'.local/python'))
    import pg8000.dbapi
    tls=ssl.create_default_context()
    if env.get('DATABASE_SSL_CA_FILE'):
        tls.load_verify_locations(str(ROOT/env['DATABASE_SSL_CA_FILE']))
        # Supabase Root 2021 predates the keyUsage requirement enforced by Python 3.13+.
        # Keep certificate-chain and hostname verification; only allow the legacy CA extension format.
        tls.verify_flags &= ~ssl.VERIFY_X509_STRICT
    connection=pg8000.dbapi.connect(user=unquote(parsed.username),password=unquote(parsed.password),host=parsed.hostname,port=parsed.port or 5432,database=parsed.path.lstrip('/'),ssl_context=tls,timeout=120)
    connection.autocommit=True;cursor=connection.cursor()
    try:
        cursor.execute("select pg_advisory_lock(672918245)")
        cursor.execute("create schema if not exists private; revoke all on schema private from public,anon; create table if not exists private.brawl_migrations(name text primary key,sha256 text not null,applied_at timestamptz not null default now()); revoke all on private.brawl_migrations from public,anon,authenticated")
        cursor.execute('select name,sha256 from private.brawl_migrations');existing=dict(cursor.fetchall())
        for path in sorted((ROOT/'supabase/migrations').glob('*.sql')):
            data=path.read_bytes();digest=hashlib.sha256(data).hexdigest()
            if path.name in existing:
                if existing[path.name]!=digest:raise ValueError('Applied migration checksum mismatch')
                continue
            sql=data.decode('utf-8');end=sql.lower().rfind('commit;')
            if end<0:raise ValueError('Migration must be transactional')
            sql=sql[:end]+"insert into private.brawl_migrations(name,sha256) values ('"+path.name+"','"+digest+"');\n"+sql[end:]
            cursor.execute(sql);print('Applied '+path.name)
        if args.content:
            path=ROOT/'.local/backend/approved-content.sql'
            data=path.read_bytes();digest=hashlib.sha256(data).hexdigest()
            cursor.execute("create table if not exists private.brawl_imports(sha256 text primary key,applied_at timestamptz not null default now()); revoke all on private.brawl_imports from public,anon,authenticated")
            cursor.execute('select 1 from private.brawl_imports where sha256=%s',(digest,))
            if not cursor.fetchone():
                sql=data.decode('utf-8');end=sql.lower().rfind('commit;')
                if end<0:raise ValueError('Content import must be transactional')
                sql=sql[:end]+"insert into private.brawl_imports(sha256) values ('"+digest+"');\n"+sql[end:]
                # The exporter emits one complete SQL statement per line. Keep one
                # transaction, but bound each network write through the hosted pooler.
                lines=sql.splitlines()
                for offset in range(0,len(lines),200):cursor.execute('\n'.join(lines[offset:offset+200]))
                print('Imported approved content; immutable IDs preserved.')
        cursor.execute("notify pgrst, 'reload schema'")
    finally:connection.close()
    return 0
if __name__=='__main__':
    try:sys.exit(main())
    except Exception as error:
        detail=error.args[0] if error.args else ''
        if isinstance(detail,dict):detail='SQLSTATE '+str(detail.get('C',''))+' '+str(detail.get('M',''))
        else:detail=str(detail)[:200]
        for value in read_env().values():
            if len(value)>=8:detail=detail.replace(value,'[redacted]')
        print('Deployment stopped: '+type(error).__name__+' '+detail+'. Inspect the project before retrying.');sys.exit(1)
