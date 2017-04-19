#!/bin/sh

#
# Convenience script to fetch the AAR and validate its digital signature
# when you don't want to download it automatically using gradle.
#

set -e
if [ $# -ne 1 ]; then
    echo "usage: $0 version" 1>&2
    exit 1
fi
rm -rf libs/*.aar
version=$1
file=android-libs-$version.aar
url="https://dl.bintray.com/measurement-kit/android/org/openobservatory/measurement_kit/android-libs/$version/$file"
wget $url $url.asc
gpg --keyserver keyserver.ubuntu.com                                           \
  --recv-keys 738877AA6C829F26A431C5F480B691277733D95B
gpg --verify $file.asc
rm $file.asc
mv $file libs/
if ! grep -q $version app/build.gradle; then
    echo "WARNING: you should update the version in 'app/build.gradle'" 1>&2
fi
