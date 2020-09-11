# OONI Probe Android [![Slack Channel](https://slack.openobservatory.org/badge.svg)](https://slack.openobservatory.org/) ![Emulator Tests Status](https://github.com/ooni/probe-android/workflows/emulator/badge.svg)

OONI Probe is free and open source software designed to measure internet
censorship and other forms of network interference.

[![OONI Probe Android](assets/OONIProbeLogo.png)](https://ooni.org)

<div align="left">

<a href="https://f-droid.org/packages/org.openobservatory.ooniprobe/" target="_blank">
<img src="assets/F-Droid-badge.png" alt="Get it on F-Droid" height="60px"/>
</a>

<a href="https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe" target="_blank">
<img src="assets/Google-Play-badge.png" alt="Get it on Google Play" height="60px"/>
</a>

</div>

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
./gradlew assembleFullRelease
```

## Building the app for f-droid

Instead to build the app to stay compliant to F-Droid use `fdroidRelease`, contains small tweaks required to have the app accepted by [f-droid](https://f-droid.org/).

```
./gradlew assembleFdroidRelease
```

## Managing translations

To manage translations check out our [translation repo](https://github.com/ooni/translations) and follow the instructions there.

## Contributing

* Write some code

* Open a pull request

* Have fun!
