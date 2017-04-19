#!/bin/sh

#
# Convenience script to make sure the MK AAR in `libs` matches the
# one listed in `app/build.gradle`.
#

set -e
versions=`ls libs/android-libs-*.aar`
count=0
for version in $versions; do
    count=$((count + 1))
done
if [ $count -ne 1 ]; then
    echo "FATAL: more than one MK version in libs" 1>&2
    exit 1
fi
mk_aar=`ls libs/android-libs-*.aar`
mk_version=`echo $mk_aar|sed 's/^libs\/android-libs-//g'|sed 's/\.aar$//g'`
if ! grep -q $mk_version app/build.gradle; then
    echo "FATAL: you should update the version in 'app/build.gradle'" 1>&2
    exit 1
fi
