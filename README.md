# ooniprobe for Android

This application requires Android Studio. In any case, before opening the
project with Android Studio, make sure you download the dependencies using
the project `Makefile`. Before you can do this, you should install the
GPG key with which binaries are signed. Binaries are digitally signed by
Simone Basso using a PGP key with ID `7733D95B` and fingerprint
(`7388 77AA 6C82 9F26 A431 C5F4 80B6 9127 7733 D95B`) or by Lorenzo Primiterra using a PGP key with ID `ECEB9D12` and
fingerprint (`1191 0C85 CD8C D493 8DFA  17F7 AA09 A57A ECEB 9D12`). You can fetch this
key using gpg using the following command:

```bash
gpg --recv-keys 7733D95B ECEB9D12
```

After this step, you can proceed with automatically downloading binaries
and verifying their digital signatures using this command:

```
make unpack
```

If the signature is correct, the above command will install the JNI
libaries inside the `src/main/` folder. Specifically, the following paths
are affected by that command:

- app/src/main/jniLibs: added with Measurement Kit jniLibs compiled for
  several Android architectures inside it

- app/src/main/java/org/openobservatory/measurement_kit/jni/: added with Java files
  corresponding to the jniLibs


All the added files should be recognized by Android Studio.
