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

Other supported platforms: [iOS](https://github.com/ooni/probe-ios),
[Desktop](https://github.com/ooni/probe-desktop), [CLI](https://github.com/ooni/probe-cli)

## Developer information

This application requires Android Studio. We use Gradle and, as part of the
initial gradle sync, Android studio will download all the required
dependencies.

The most important dependency is `oonimkall`. This dependency contains
the network measurement engine. Its sources are at
[ooni/probe-cli](https://github.com/ooni/probe-cli).

When using Gradle from the command line, you will need to set the
`ANDROID_SDK_ROOT` environment variable to point to the directory in
which you have installed the Android SDK.

## Build variants

We use the classic `debug` and `release` build types. We also
implement the following flavours:

- `stable`, `dev`, and `experimental` (dimension: `testing`);

- `full` and `fdroid` (dimension: `license`).

- `ooni` and `dw` (dimension: `brand`).

The `testing` dimension controls whether we're building a release
or a more unstable version. We build releases using the `stable`
flavour. The `dev` flavour builds the version of the app that should
be released on the store as the beta channel. The `experimental`
flavour, instead, allows a developer to build a one-off version of
the app that uses a custom build of the `oonimkall` library.

The `license` dimension controls which proprietary libraries to include
into the build. The `full` flavour includes all such dependencies,
while the `fdroid` flavour does not include any of them.

The `brand` dimension controls the branding of the application. The `ooni` flavour is the default branding, while the `dw` flavour is for the "News Media Scan" branding.

The variant names are therefore:

- `ooniExperimentalFullDebug`
- `ooniExperimentalFullRelease`
- `ooniDevFullDebug`
- `ooniDevFullRelease`
- `ooniStableFullDebug`
- `ooniStableFullRelease`
- `dwExperimentalFullDebug`
- `dwExperimentalFullRelease`
- `dwDevFullDebug`
- `dwDevFullRelease`
- `dwStableFullDebug`
- `dwStableFullRelease`

We additionally have `ooniStableFdroidDebug`, `ooniStableFdroidRelease`, `dwStableFdroidDebug` and `dwStableFdroidRelease`.

All of this is controlled by [app/build.gradle](app/build.gradle).

## Gradle modules

- [app](app) contains the mobile app;
- [engine](engine) contains wrappers for `oonimkall`, the measurement engine library;
- [engine-experimental](engine-experimental) allows us to implement the `experimental` build flavour where you put the `oonimkall.aar` file you built inside `engine-experimental` rather than downloading it from Maven Central.

## Building an apk

Ensure you have Android Studio and Android SDK installed. Build the `ooniDevFullRelease`
variant using Android Studio or this command line:

```sh
./gradlew assembleOoniDevFullRelease
```

## Building the app for f-droid

Instead to build the app to stay compliant to F-Droid use `fdroid`, which contains small tweaks required to have the app accepted by [f-droid](https://f-droid.org/).

```sh
./gradlew assembleOoniDevFullRelease
```

## Testing

Run unit tests/

```sh
./gradlew testOoniStableFullDebug
```

Run instrumented tests

_Note: To also run the automation tests (to generate screenshots), set the Build Config flag `RUN_AUTOMATION` as true._

```sh
./gradlew connectedOoniStableFullDebugAndroidTest
```

Generate code coverage report (after all unit and instrumented tests successfully passed)

```sh
./gradlew jacocoAndroidTestReport
```

## Managing translations

To manage translations check out our [translation repo](https://github.com/ooni/translations)
and follow the instructions there.

## Contributing

* Write some code

* Open a pull request

* Have fun!
