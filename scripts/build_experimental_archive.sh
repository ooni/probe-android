#!/bin/sh

# Define build and output directories
gitTarget=${1:-"master"}
buildDir=${2:-"./build"}

# Create build directory and navigate into it
mkdir -p "${buildDir}" && cd "${buildDir}"

# Clone the repository
git clone -v https://github.com/ooni/probe-cli.git

# Navigate into the repository
cd probe-cli

git checkout master

git pull origin
# Checkout the target branch
git checkout -c "${gitTarget}"

# Build the probe-cli
make android

cp -v ./MOBILE/android/oonimkall.aar ../../engine-experimental/
cp -v ./MOBILE/android/oonimkall-sources.jar ../../engine-experimental/


echo "* Fetching geoip databases"