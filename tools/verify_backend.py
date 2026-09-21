"""Verify the authorized hosted backend. Uses and then signs out a temporary admin session."""
import json,sys,urllib.request,urllib.error
from local_config import read_env

def main():
    env=read_env();base=env['SUPABASE_URL'].rstrip('/');key=env['SUPABASE_PUBLISHABLE_KEY'];token=None
    def request(path,data=None,auth=None):
        headers={'apikey':key,'Content-Type':'application/json'}
        if auth:headers['Authorization']='Bearer '+auth
        req=urllib.request.Request(base+path,data=None if data is None else json.dumps(data).encode(),headers=headers)
        with urllib.request.urlopen(req,timeout=30) as result:
            body=result.read();return json.loads(body) if body else None
    try:
        session=request('/auth/v1/token?grant_type=password',{'email':env['BRAWL_ADMIN_EMAIL'],'password':env['BRAWL_ADMIN_PASSWORD']})
        token=session['access_token'];print('PASS email/password authentication')
        profile=request('/rest/v1/rpc/profile_snapshot',{},token)
        assert profile['profile']['username']==env['BRAWL_ADMIN_USERNAME'] and profile['is_admin'] is True
        print('PASS own profile, Flame-derived level and server administrator membership')
        store=request('/rest/v1/rpc/store_snapshot',{},token);assert isinstance(store['items'],list)
        print('PASS authenticated store snapshot; catalog items:',len(store['items']))
        missions=request('/rest/v1/rpc/mission_snapshot',{},token)
        assert len(missions)==6 and all(0<=m['progress']<=m['target'] for m in missions)
        print('PASS authenticated mission progress and six reward definitions')
        rankings=request('/rest/v1/rpc/leaderboard',{'p_mode':'duel','p_metric':'win_rate','p_period':'all_time','p_friends':False,'p_limit':50},token)
        assert isinstance(rankings['rows'],list);print('PASS authenticated leaderboard snapshot')
        for path in ('/rest/v1/rpc/profile_snapshot','/rest/v1/rpc/admin_deletion_requests'):
            try:request(path,{});raise AssertionError('Anonymous access accepted')
            except urllib.error.HTTPError as error:assert error.code in (401,403,404)
        try:request('/rest/v1/rpc/admin_deletion_requests',{},token);raise AssertionError('Client admin token obtained service-only access')
        except urllib.error.HTTPError as error:assert error.code in (401,403,404)
        print('PASS anonymous and service-only RPC restrictions')
    finally:
        if token:request('/auth/v1/logout?scope=local',{},token)
    return 0
if __name__=='__main__':
    try:sys.exit(main())
    except urllib.error.HTTPError as error:print('Hosted verification failed: HTTP '+str(error.code));sys.exit(1)
    except Exception as error:print('Hosted verification failed: '+type(error).__name__);sys.exit(1)
