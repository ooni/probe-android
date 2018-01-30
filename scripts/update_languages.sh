#! /bin/bash -u
tx pull -f
tx pull -f -l zh_CN
tx pull -f -l zh_TW
cp app/src/main/res/values-zh_CN/strings.xml app/src/main/res/values-zh-rCN/strings.xml 
cp app/src/main/res/values-zh_TW/strings.xml app/src/main/res/values-zh-rTW/strings.xml 
rm -Rf app/src/main/res/values-zh_CN app/src/main/res/values-zh_TW
python scripts/fix-strings.py
