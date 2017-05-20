#!/usr/bin/env python2
# -*- coding: utf-8 -*-
import re
import os
from glob import glob
import xml.dom.minidom as md

def fix_strings(strings_path, out_file):
    out_file.write('<?xml version="1.0" encoding="utf-8"?>\n')
    out_file.write('<resources>\n')
    dom = md.parse(strings_path)
    ss = dom.getElementsByTagName('string')
    for s in ss:
        assert len(s.childNodes) == 1
        fixed_string = '    '
        name = s.getAttribute('name')
        value = s.childNodes[0].nodeValue
        # This regexp will match any unescaped ' and " also when it appears at
        # the beginning of the string.
        value = re.sub(r'([^\\])\'|^\'', '\g<1>\\\'', value)
        value = re.sub(r'([^\\])\"|^\"', '\g<1>\\\"', value)
        fixed_string += '<string name="{name}">{value}</string>\n'.format(
                name=name,
                value=value.encode('utf-8'))
        out_file.write(fixed_string)
    out_file.write('</resources>\n')

from StringIO import StringIO
def fix_all_string(base_path='app/src/main/res/'):
    ignore_dirs = ['values-v14', 'values-w820dp']
    for strings_dir in glob(os.path.join(base_path, 'values-*')):
        if os.path.basename(strings_dir) in ignore_dirs:
            continue
        strings_fixed_path = os.path.join(strings_dir, 'strings.xml.fixed')
        strings_path = os.path.join(strings_dir, 'strings.xml')

        with open(strings_fixed_path, 'w') as output:
            fix_strings(strings_path, output)
        os.rename(strings_fixed_path, strings_path)

if __name__ == "__main__":
    fix_all_string()
