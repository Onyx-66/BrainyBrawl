"""Read ignored local configuration; never print credential values."""
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def read_env():
    values={}
    if not (ROOT/'.env').is_file():return values
    for line in (ROOT/'.env').read_text(encoding='utf-8-sig').splitlines():
        if '=' not in line or line.lstrip().startswith('#'):continue
        key,value=line.split('=',1);value=value.strip()
        if len(value)>1 and value[0]==value[-1] and value[0] in ('"',"'"):value=value[1:-1]
        values[key.strip()]=value
    return values
