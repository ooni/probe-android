#!/bin/bash
set -ex

git clone https://github.com/ooni/probe-mobile-appium.git
# ./gradlew connectedAndroidTest
./gradlew assembleDebug
adb devices
appium-doctor --android
cd probe-mobile-appium && ../gradlew androidRegression
# cat $HOME/appium_log.txt
cat /home/travis/build/ooni/probe-android/probe-mobile-appium/build/reports/tests/androidRegression/classes/probe.mobile.appium.runners.LandingPageRunner.html
