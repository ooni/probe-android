#!/bin/sh
#set -euxo pipefail

# Define build and output directories
gitTarget=${1:-"master"}
buildDir=${2:-"./build"}

if [ -d "$buildDir" ]; then rm -Rf $buildDir; fi

# Create build directory and navigate into it
mkdir -p "${buildDir}"

cd "${buildDir}"

# Clone the repository
git clone -v https://github.com/ooni/probe-cli.git

# Navigate into the repository
cd probe-cli

# Checkout the target branch
git checkout "${gitTarget}"

# Build the probe-cli
make android

cp -v ./MOBILE/android/oonimkall.aar ../../engine-experimental/
cp -v ./MOBILE/android/oonimkall-sources.jar ../../engine-experimental/


echo "Done building engine archive."
