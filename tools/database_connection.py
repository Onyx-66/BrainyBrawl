"""Verified TLS connection for explicitly invoked local backend tools."""
import ssl,sys
from urllib.parse import urlsplit,unquote
from local_config import ROOT,read_env

def connect(timeout=30):
    sys.path.insert(0,str(ROOT/'.local/python'))
    import pg8000.dbapi
    env=read_env();u=urlsplit(env.get('DIRECT_URL') or env['DATABASE_URL'])
    if not u.password or '[YOUR-PASSWORD]' in u.password or u.port==6543:raise ValueError('A real session/direct database connection is required')
    tls=ssl.create_default_context()
    if env.get('DATABASE_SSL_CA_FILE'):
        tls.load_verify_locations(str(ROOT/env['DATABASE_SSL_CA_FILE']))
        # Compatibility for the official Supabase Root 2021 CA; verification stays enabled.
        tls.verify_flags &= ~ssl.VERIFY_X509_STRICT
    assert tls.verify_mode==ssl.CERT_REQUIRED and tls.check_hostname
    return pg8000.dbapi.connect(user=unquote(u.username),password=unquote(u.password),host=u.hostname,port=u.port or 5432,database=u.path.lstrip('/'),ssl_context=tls,timeout=timeout)
