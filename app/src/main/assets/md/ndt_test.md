# Network Diagnostic Test (NDT)

NDT (Network Diagnostic Test) is designed to measure the *speed* and
*performance* of tested networks.

This network performance test was originally developed by The Internet2 Project
and is currently maintained by [Measurement Lab (mLab)](http://www.measurementlab.net/tools/ndt/). NDT is designed to measure
the speed and performance of networks by connecting to mLab servers close to the
user, and by subsequently uploading and downloading random data. In doing so,
NDT collects TCP/IP low level information that is useful to examining and
characterizing the quality of the network path between the user and the mLab
server.

OONI utilizes an *[implementation of NDT](https://github.com/measurement-kit/measurement-kit/tree/master/src/libmeasurement_kit/ndt)* for [measurement-kit](https://github.com/measurement-kit/measurement-kit), which is a network
measurement library for running both desktop and mobile network measurement
tests. This NDT implementation, for example, is included as a test that can be
run via OONI's mobile app.

As OONI's core software tests are *not* designed to collect PCAPs or low level
TCP/IP information, running NDT can be useful as the type of information that it
collects can potentially be used to examine cases of throttling.