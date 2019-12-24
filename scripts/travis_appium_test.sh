#!/bin/bash
set -ex

emulator -avd test -no-window &
android-wait-for-emulator
adb shell input keyevent 82 &
appium &

git clone https://github.com/ooni/probe-mobile-appium.git
# ./gradlew connectedAndroidTest
./gradlew assembleDebug
adb devices
appium-doctor --android
cd probe-mobile-appium && gradle androidRegression
# cat $HOME/appium_log.txt
cat /home/travis/build/ooni/probe-android/probe-mobile-appium/build/reports/tests/androidRegression/classes/probe.mobile.appium.runners.LandingPageRunner.html
