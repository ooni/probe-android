# OONI Probe Android

[![OONI Probe Android](assets/OONIProbeLogo.png)](https://ooni.org)

<p align="center">
  <a href="https://slack.openobservatory.org/">
        <img src="https://slack.openobservatory.org/badge.svg"
            alt="chat on Slack"></a>

  <a href="https://github.com/ooni/probe/issues?q=label%3Aooni%2Fprobe-android">
    <img src="https://img.shields.io/github/issues/ooni/probe/ooni/probe-android" alt="open issues">
  </a>

  <img src="https://github.com/ooni/probe-android/workflows/emulator/badge.svg" alt="Emulator Tests Status">

  <a href="https://twitter.com/intent/follow?screen_name=OpenObservatory">
    <img src="https://img.shields.io/twitter/follow/OpenObservatory?style=social&logo=twitter"
    alt="follow on Twitter"></a>
</p>

<div align="left">

<a href="https://f-droid.org/packages/org.openobservatory.ooniprobe/" target="_blank">
<img src="assets/F-Droid-badge.png" alt="Get it on F-Droid" height="60px"/>
</a>

<a href="https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe" target="_blank">
<img src="assets/Google-Play-badge.png" alt="Get it on Google Play" height="60px"/>
</a>

</div>

OONI Probe is free and open source software designed to measure internet
censorship and other forms of network interference.

[Click here to report a bug](https://github.com/ooni/probe/issues/new)

Other supported platforms: [iOS](https://github.com/ooni/probe-ios), [Desktop](https://github.com/ooni/probe-desktop), [CLI](https://github.com/ooni/probe-cli)

## Developer information

This application requires Android Studio. We use gradle and, as part of the
initial gradle sync, Android studio will download all the required
dependencies. The most important dependency is [measurement-kit](
https://github.com/measurement-kit/measurement-kit) which is fetched
from our [Bintray jcenter repository](
https://bintray.com/measurement-kit/android/android-libs).

## Building an apk

Ensure you have Android Studio and Android SDK installed. Build `fullRelease` variant using Android Studio or this command line:

```
./gradlew assembleDevFullRelease
```

## Building the app for f-droid

Instead to build the app to stay compliant to F-Droid use `fdroidRelease`, contains small tweaks required to have the app accepted by [f-droid](https://f-droid.org/).

```
./gradlew assembleFdroidRelease
```

## Testing

Run unit tests 

```
./gradlew testStableFullDebug
```

Run instrumented tests (requires clean state in the device) 

```
./gradlew connectedAndroidTest
```

Generate code coverage report (after all unit and instrumented tests successfully passed)

```
./gradlew jacocoAndroidTestReport
``` 

## Managing translations

To manage translations check out our [translation repo](https://github.com/ooni/translations) and follow the instructions there.

## Contributing

* Write some code

* Open a pull request

* Have fun!
