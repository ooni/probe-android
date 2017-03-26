import os
import sys
import xml.dom.minidom as md

title_tmpl = u"{title}"
short_description_tmpl = u"{shortdescription}"

full_description_tmpl = u"""
{intro}
{intro2}

{intro3}
{intro4}

<b>{evidence-title}</b>
{evidence-description}

<b>{systems-title}</b>
{systems-description}

<b>{speed-title}</b>
{speed-description}

<b>{opendata-title}</b>
{opendata-description}

<b>{freesoftware-title}</b>
{freesoftware-description}


{social-links}


{disclaimer}
"""
screenshots_tmpl = u"""
# Screnshot 1
{screenshot1}

# Screnshot 2
{screenshot2}

# Screnshot 3
{screenshot3}

# Screnshot 4
{screenshot4}

# Screnshot 5
{screenshot5}

# Feature Graphic
{feature-graphic}
"""
keywords_tmpl = u"{keywords}"

def get_ids(lang_code='source'):
    ids = {}
    dom = md.parse('description/{lang_code}.xlf'.format(lang_code=lang_code))
    units = dom.getElementsByTagName('trans-unit')
    for unit in units:
        unit_id = unit.getAttribute('id')
        target = unit.getElementsByTagName('target')
        if len(target) == 0:
            target = unit.getElementsByTagName('source')
        assert len(target) == 1
        target = target[0]
        assert len(target.childNodes) == 1
        value = target.childNodes[0].nodeValue
        if unit_id in ids and ids[unit_id] != value:
            raise Exception("Duplicate ID")
        ids[unit_id] = value
    return ids

def print_frmt(name, value):
    title = "%s (%d chars)" % (name, len(value))
    print(title)
    print("-"*len(title))
    print(value)
    print("\n\n")

def print_tmpls(lang_code='source'):
    ids = get_ids(lang_code)

    title = title_tmpl.format(**ids)
    print_frmt("Title", title)

    short_description = short_description_tmpl.format(**ids)
    print_frmt("Short description", short_description)

    full_description = full_description_tmpl.format(**ids)
    print_frmt("Full description", full_description)

    screenshots = screenshots_tmpl.format(**ids)
    print_frmt("Screenshots", screenshots)

    keywords = keywords_tmpl.format(**ids)
    print_frmt("Keywords", keywords)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: %s [lang_code]" % sys.argv[0])
        sys.exit(1)
    if not os.path.exists(os.path.join("description/source.xlf")):
        print("ERROR")
        print("This script must be run from the root of the repository")
        sys.exit(1)

    target = sys.argv[1]
    if target == 'en':
        target = 'source'
    print_tmpls(target)
