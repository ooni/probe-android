#usage ./changelog.sh 69 "Release notes"
for i in $(ls metadata/android); do echo "$2" > metadata/android/$i/changelogs/$1.txt; done
