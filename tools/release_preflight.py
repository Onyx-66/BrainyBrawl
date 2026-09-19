"""Local release prerequisites; reports blockers without deploying or logging secrets."""
import os,sys,contextlib,io
from urllib.parse import urlsplit
from local_config import ROOT,read_env
from content_pipeline import validate

def main():
    env={**read_env(),**os.environ};blocked=[]
    for key in ('SUPABASE_URL','PRIVACY_POLICY_URL','TERMS_URL','ACCOUNT_DELETION_URL'):
        url=urlsplit(env.get(key,''))
        if url.scheme!='https' or not url.hostname or 'YOUR_' in env.get(key,''):blocked.append(key+' must point to an owner-approved HTTPS page/service')
    if not env.get('SUPABASE_PUBLISHABLE_KEY','').startswith(('sb_publishable_','eyJ')):blocked.append('Missing public client API key')
    for key in ('BRAWL_KEYSTORE_PATH','BRAWL_KEYSTORE_PASSWORD','BRAWL_KEY_ALIAS','BRAWL_KEY_PASSWORD'):
        if not os.environ.get(key):blocked.append('Missing signing environment setting: '+key)
    if os.environ.get('BRAWL_KEYSTORE_PATH') and not __import__('pathlib').Path(os.environ['BRAWL_KEYSTORE_PATH']).is_file():blocked.append('Signing keystore does not exist')
    try:
        with contextlib.redirect_stdout(io.StringIO()):validate(production=True)
    except (ValueError,OSError) as error:blocked.append('Production content gate: '+str(error))
    for issue in blocked:print('BLOCKED: '+issue)
    print('Owner verification still required: live provider/multiplayer tests, deletion processing and public request URL, moderation, age rating/data safety, signed physical-device testing and explicit submission approval.')
    return 2 if blocked else 0
if __name__=='__main__':sys.exit(main())
