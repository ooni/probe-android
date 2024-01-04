#!/bin/sh

# Define build and output directories
buildDir=${1:-"./build"}
outputDir=${2:-"./app/src/main/res/drawable"}

# Create build directory and navigate into it
mkdir -p "${buildDir}" && cd "${buildDir}"

### Method 2: Download the prebuild icon archive and unarchive into the desired directory ###
# Download the prebuild icon archive
curl -L -o probe-icons.tar.gz https://github.com/aanorbel/react-icons/releases/download/ooni-v5.0.0/probe-icons.tar.gz

# Unarchive the downloaded archive into the desired directory
tar -zxf probe-icons.tar.gz --directory "${outputDir}"