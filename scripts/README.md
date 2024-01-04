## Prepare Icons

### Method 1: Clone the react-icons repository, build and copy the icons to desired directory

```shell
#!/bin/sh

# Define build and output directories
buildDir=${1:-"./build"}
outputDir=${2:-"./app/src/main/res/drawable"}

# Create build directory and navigate into it
mkdir -p "${buildDir}" && cd "${buildDir}"

### Method 1: Clone the react-icons repository, build and copy the icons to desired directory ###
# Clone the react-icons repository
git clone -v https://github.com/ooni/react-icons.git

# Navigate into the cloned repository and install dependencies
cd react-icons && yarn install

# Fetch the icons
cd packages/react-icons && yarn fetch

# Build the SVGs
yarn build-svgs

# Define the source of the icons and the .gitignore file
from="${buildDir}/react-icons/packages/_react-icons_all-xmls"
ignoreFile="${outputDir}/.gitignore"

# Define the supported icon packs
supportedIcons=("fa" "md")

# Copy the icons from the supported packs into the output directory
for pac in "${supportedIcons[@]}";do
    for file in "${from}"/"${pac}"/*.xml; do
        cp -v "$file" "${outputDir}"
        # Add the copied file to the .gitignore
        echo "$(basename "${file}")" >> "${ignoreFile}"
    done
done

```

### Method 2: Download the prebuild icon archive and unarchive into the desired directory

```shell
#!/bin/sh

# Create build directory and navigate into it
mkdir -p "${buildDir}" && cd "${buildDir}"

### Method 2: Download the prebuild icon archive and unarchive into the desired directory ###
# Download the prebuild icon archive
curl -L -o probe-icons.tar.gz https://github.com/aanorbel/react-icons/releases/download/ooni-v5.0.0/probe-icons.tar.gz

# Unarchive the downloaded archive into the desired directory
tar -zxf probe-icons.tar.gz --directory "${outputDir}"
```