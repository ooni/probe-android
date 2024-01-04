#!/bin/sh

# Define workDir
workDir=${1:-"./app/src/main/res/drawable"}

# Delete all files contained in .gitignore
while IFS= read -r file; do
    rm -v "${workDir}/${file}"
done < "${workDir}/.gitignore" || exit 0