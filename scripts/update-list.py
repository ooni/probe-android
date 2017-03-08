import os
import sys
import csv
import shutil
import subprocess
import tempfile

TARGET_FILE = 'app/src/main/res/raw/global.txt'
LATEST_VERSION_URL = 'https://github.com/OpenObservatory/ooni-resources/releases/download/latest/version'

GLOBAL_LIST_URL = 'https://github.com/OpenObservatory/ooni-resources/releases/download/{}/citizenlab-test-lists.global.csv'

def cleanup(tmp_dir):
    shutil.rmtree(tmp_dir)

def cmd_exists(cmd):
    return subprocess.call("type " + cmd, shell=True,
        stdout=subprocess.PIPE, stderr=subprocess.PIPE) == 0

def check_requirements():
    if not os.path.exists(TARGET_FILE):
        print("ERROR")
        print("Script must be run from root of repository")
        sys.exit(1)
    if not cmd_exists('curl'):
        print("ERROR")
        print("curl needs to be installed")
        sys.exit(1)

def main():
    check_requirements()
    tmp_dir = tempfile.mkdtemp()
    try:
        subprocess.call([
            'curl', '-o', os.path.join(tmp_dir, 'version'),
            '-L',
            LATEST_VERSION_URL
        ])
        with open(os.path.join(tmp_dir, 'version')) as in_file:
            latest_version = in_file.read()
        subprocess.call([
            'curl', '-o', os.path.join(tmp_dir, 'global.csv'),
            '-L',
            GLOBAL_LIST_URL.format(latest_version)
        ])
        out_file = open(TARGET_FILE+'.tmp', 'w')
        with open(os.path.join(tmp_dir, 'global.csv')) as in_file:
            reader = csv.reader(in_file)
            reader.next() # Skip header
            for row in reader:
                out_file.write(row[0] + '\n')
        out_file.close()
        os.rename(TARGET_FILE+'.tmp', TARGET_FILE)
    except Exception as exc:
        print("FAILED")
        print(exc)
    finally:
        cleanup(tmp_dir)

if __name__ == '__main__':
    main()
