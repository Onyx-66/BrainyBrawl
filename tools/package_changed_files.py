"""Package session changes against a saved SHA-256 baseline, never against HEAD alone."""
import argparse
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import subprocess
import zipfile

ROOT=Path(__file__).resolve().parents[1]
SKIP_DIRS={'.git','.gradle','.kotlin','.idea','.local','build','node_modules','__pycache__','.cxx','.externalNativeBuild'}
SKIP_NAMES={'local.properties','BrainyBrawl_CHANGED_FILES.zip','CHANGED_FILES_MANIFEST.md'}

def permitted(name):
    path=PurePosixPath(name)
    return not (path.is_absolute() or '..' in path.parts or any(x in SKIP_DIRS for x in path.parts)
                or path.name in SKIP_NAMES or path.name.startswith('.env')
                or path.suffix.lower() in {'.jks','.keystore','.pem','.p12','.pfx','.apk','.aab','.log'})

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest().upper()

def package(baseline):
    initial=json.loads(Path(baseline).read_text(encoding='utf-8-sig'))
    if not isinstance(initial,dict):raise ValueError('Invalid baseline')
    for name,value in initial.items():
        if PurePosixPath(name).is_absolute() or '..' in PurePosixPath(name).parts or len(value)!=64:
            raise ValueError('Unsafe baseline entry')
    listed=subprocess.check_output(['git','ls-files','--cached','--others','--exclude-standard','-z'],cwd=ROOT).decode('utf-8').split('\0')
    current={name:digest(ROOT/name) for name in listed if name and permitted(name) and (ROOT/name).is_file()}
    added=sorted(name for name in current if name not in initial)
    modified=sorted(name for name in current if name in initial and current[name]!=initial[name].upper())
    deleted=sorted(name for name in initial if permitted(name) and not (ROOT/name).exists())
    lines=['# Changed files manifest','',
      'Comparison: SHA-256 inventory captured at the beginning of this continuous session.',
      'Initial branch: master; initial HEAD: 502bd53. Pre-existing user edits are not classified as session changes unless their file bytes changed during implementation.',
      '', '## Implementation and verification', '',
      'See `docs/SESSION_REPORT.md` for the current handover and verification results,',
      '`docs/IMPLEMENTATION_STATUS.md` for implementation history,',
      '`docs/RELEASE_CHECKLIST.md` for release gates, and',
      '`docs/ai-build-pack/17_DECISION_REGISTER.md` for unresolved product decisions.',
      '', 'Changes include Kotlin/Compose feature layers, server-authoritative SQL/RLS,',
      'typed realtime recovery, XML content and validation, localized UI, test coverage,',
      'CI/release configuration and safe public configuration examples.',
      'Authorized backend deployment is documented in the session report. Credentials and signing material are excluded.',
      '', '## Added files', '']
    lines += [f'- `{name}`' for name in added] or ['None.']
    lines += ['', '## Modified files', '']
    lines += [f'- `{name}`' for name in modified] or ['None.']
    lines += ['', '## Deleted files', '']
    lines += [f'- `{name}`' for name in deleted] or ['None during this session. The previously deleted build-pack README was already absent at baseline.']
    lines += ['', '## Archive rules', '',
      'The archive contains only added/modified files listed above, plus this manifest.',
      'The manifest itself is an added session artifact. Deleted files are listed only.',
      'Git history, build outputs, caches, node_modules, IDE settings, local.properties,',
      'keystores, credential files, emulator screenshots and temporary files are excluded.',
      '', '## SHA-256 of archived source files', '', '| File | SHA-256 |', '| --- | --- |']
    lines += [f'| `{name}` | `{current[name]}` |' for name in sorted(added+modified)]
    manifest=ROOT/'CHANGED_FILES_MANIFEST.md';manifest.write_text('\n'.join(lines)+'\n',encoding='utf-8')
    archive=ROOT/'BrainyBrawl_CHANGED_FILES.zip'
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as out:
        for name in sorted(added+modified):out.write(ROOT/name,name)
        out.write(manifest,manifest.name)
    with zipfile.ZipFile(archive) as check:
        assert check.testzip() is None
        assert set(check.namelist())==set(added+modified+[manifest.name])
        for name in added+modified:
            assert hashlib.sha256(check.read(name)).hexdigest().upper()==current[name]
    print(f'Packaged {len(added)} added, {len(modified)} modified, {len(deleted)} deleted files; {archive.stat().st_size} bytes. ZIP hashes verified.')

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--baseline',required=True,type=Path)
    package(parser.parse_args().baseline)
