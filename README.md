![ooniprobe android](assets/title.png)

[![Slack Channel](https://slack.openobservatory.org/badge.svg)](https://slack.openobservatory.org/)

This is the android version of [ooniprobe](https://ooni.torproject.org/).

Download it on the [Play Store](https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe).

[![](assets/play-store-badge.png)](https://play.google.com/store/apps/details?id=org.openobservatory.ooniprobe)

This application requires Android Studio. In any case, before opening the
project with Android Studio, make sure you download the dependencies using
`./get-library`. Before you can do this, you should install the
GPG key with which binaries are signed. Binaries are digitally signed by
Simone Basso using a PGP key with
(`7388 77AA 6C82 9F26 A431 C5F4 80B6 9127 7733 D95B`)
or by Lorenzo Primiterra using a PGP key with
fingerprint (`1191 0C85 CD8C D493 8DFA  17F7 AA09 A57A ECEB 9D12`). You
can fetch this
key using gpg using the following command:

```bash
gpg --recv-keys 738877AA6C829F26A431C5F480B691277733D95B                       \
  11910C85CD8CD4938DFA17F7AA09A57AECEB9D12
```

After this step, you can proceed with automatically downloading binaries
and verifying their digital signatures using this command:

```
./get-library
```

If the signature is correct, the above command will install the AAR
library inside the `libs/` folder. This AAR library should be automatically
recognized by Android studio.

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
