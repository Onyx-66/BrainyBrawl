"""Provision the requested administrator using server credentials from ignored .env.
Dry-run by default. Never packages credentials or grants privilege from user metadata.
"""
import argparse,json,sys,urllib.request,urllib.error
from local_config import read_env
from pathlib import Path

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apply',action='store_true')
    args=parser.parse_args()
    env=read_env()
    env['SUPABASE_SERVICE_ROLE_KEY']=env.get('SUPABASE_SECRET_KEY') or env.get('SUPABASE_SERVICE_ROLE_KEY','')
    required=['SUPABASE_URL','SUPABASE_SERVICE_ROLE_KEY','BRAWL_ADMIN_USERNAME','BRAWL_ADMIN_EMAIL','BRAWL_ADMIN_PASSWORD']
    missing=[key for key in required if not env.get(key)]
    if missing:print('Required settings: '+', '.join(missing));return 2
    base=env['SUPABASE_URL'].rstrip('/')
    if not base.startswith('https://'):raise ValueError('An HTTPS Supabase project URL is required')
    if not args.apply:print('Configuration available. After approving the target project and applying migrations, run with --apply.');return 0
    key=env['SUPABASE_SERVICE_ROLE_KEY']
    def request(path,data=None):
        headers={'apikey':key,'Content-Type':'application/json'}
        if not key.startswith('sb_secret_'):headers['Authorization']='Bearer '+key
        req=urllib.request.Request(base+path,data=None if data is None else json.dumps(data).encode(),headers=headers)
        with urllib.request.urlopen(req,timeout=30) as response:
            body=response.read();return json.loads(body) if body else None
    account=None
    for page in range(1,1001):
        users=request('/auth/v1/admin/users?page='+str(page)+'&per_page=100').get('users',[])
        account=next((u for u in users if u.get('email','').lower()==env['BRAWL_ADMIN_EMAIL'].lower()),None)
        if account or len(users)<100:break
    else:raise RuntimeError('User enumeration limit reached; no account created')
    if account is None:
        account=request('/auth/v1/admin/users',{'email':env['BRAWL_ADMIN_EMAIL'],'password':env['BRAWL_ADMIN_PASSWORD'],'email_confirm':True,'user_metadata':{'username':env['BRAWL_ADMIN_USERNAME']}})
    request('/rest/v1/rpc/bootstrap_admin',{'p_user':account['id']})
    print('Administrator provisioned. Existing account passwords were preserved.')
    return 0
if __name__=='__main__':
    try:sys.exit(main())
    except urllib.error.HTTPError as error:print('Provisioning rejected: HTTP '+str(error.code));sys.exit(1)
    except Exception:print('Provisioning failed; verify configuration, connectivity and applied migrations.');sys.exit(1)
