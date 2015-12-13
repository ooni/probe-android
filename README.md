# Measurement Kit Android app

This application requires Android Studio. In any case, before opening the
project with Android Studio, make sure you download the dependencies using
the project `Makefile`. To do this, type:

```
make unpack
```

This command will download Measurement Kit jniLibs from GitHub and verify
their digital signature. If the signature is correct, it will install inside
this repository MeasurementKit jniLibs and Java files.

Specifically, the following paths are affected by that command:

- app/src/main/jniLibs: added with Measurement Kit jniLibs compiled for
  several Android architectures inside it

- app/src/main/java/io/github/measurement_kit/jni/: added with Java files
  corresponding to the jniLibs

All the added files should be recognized by Android Studio.
