[![ooniprobe android](assets/title.png)](https://ooni.torproject.org/)

[![Slack Channel](https://slack.openobservatory.org/badge.svg)](https://slack.openobservatory.org/)

This is the android version of [ooniprobe](https://ooni.torproject.org/).

Download it on the [Play Store](https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe).

[![](assets/play-store-badge.png)](https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe)

## Dependencies

### Using gradle

This application requires Android Studio. We use gradle and, as part of the
initial gradle sync, Android studio will download all the required
dependencies. The most important dependency is [measurement-kit](
https://github.com/measurement-kit/measurement-kit) which is fetched
from our [Bintray jcenter repository](
https://bintray.com/measurement-kit/android/android-libs).

### Manually fetching MK

As an alternative, you can manually download a version of MK
from our [Bintray jcenter repository](
https://bintray.com/measurement-kit/android/android-libs) using
the `scripts/fetch-aar.sh` script. Example usage:

```
./scripts/fetch-aar.sh 0.4.3-aar-3
```

This script will download the specified version of the AAR, verify
its digital signature, and move it inside the `libs` folder.

This script will also warn you if the version number of the file you
downloaded is different from the one inside of `app/build.gradle`.

### Forcing a specific version of MK

Just put the AAR file inside of `libs`. Then run

```
./script/check-version-consistency.sh
```

to make sure the version of the AAR inside libs is the same of
the version listed in `app/build.gradle`. Update said file in
case there is a version mismatch.

## Building an apk

* Ensure you have Android Studio and gradle installed

On macOS you can do:

```
brew cask install android-studio
```

Then you should open the project in Android Studio and click on build.

The built apk will end up inside of `app/build/outputs/apk/`.

If you wish to test the apk inside of an emulator this can be done with
(assuming you have created an emulator named
`Nexus_5_API_23_marshmallow_6.0`):

```
~/Library/Android/sdk/tools/emulator -avd Nexus_5_API_23_marshmallow_6.0
~/Library/Android/sdk/platform-tools/adb install app/build/outputs/apk/app-debug.apk
```

The app should then be installed inside of the emulator `Nexus_5_API_23_marshmallow_6.0`.

## Managing translations

To manage translations ensure you have installed the [transifex command line
tools](https://docs.transifex.com/client/installing-the-client).

### Pushing source text

To push the source of the translation run:

```
tx push -s
```

### Pulling translations

To pull in translations run:

```
tx pull
```

or

```
tx pull -l [lang_code]
```

to pull only a specific language


### Generating descriptions for market

To generate translated descriptions for the markets run:

```
python scripts/gen-descriptions.py [lang_code]
```

Where `lang_code` is the language code for the description you want to
generate.

This will print to standard output the translated text that you can then copy
and paste into the market descriptions.

If a string is not translated it will print the source for the text.
