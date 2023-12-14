# Test Suites.

This file exits to document the test suites that are available in OONI Probe.

There previously existed 5 test suites in OONI Probe. These were:

- [WebsitesSuite](https://github.com/ooni/probe-android/blob/7b8ec932fbccbb4f89e8101fddbe27d5a5591bf2/app/src/main/java/org/openobservatory/ooniprobe/test/suite/WebsitesSuite.java)
- [InstantMessagingSuite](https://github.com/ooni/probe-android/blob/7b8ec932fbccbb4f89e8101fddbe27d5a5591bf2/app/src/main/java/org/openobservatory/ooniprobe/test/suite/InstantMessagingSuite.java)
- [CircumventionSuite](https://github.com/ooni/probe-android/blob/7b8ec932fbccbb4f89e8101fddbe27d5a5591bf2/app/src/main/java/org/openobservatory/ooniprobe/test/suite/CircumventionSuite.java)
- [PerformanceSuite](https://github.com/ooni/probe-android/blob/7b8ec932fbccbb4f89e8101fddbe27d5a5591bf2/app/src/main/java/org/openobservatory/ooniprobe/test/suite/PerformanceSuite.java)
- [ExperimentalSuite](https://github.com/ooni/probe-android/blob/7b8ec932fbccbb4f89e8101fddbe27d5a5591bf2/app/src/main/java/org/openobservatory/ooniprobe/test/suite/ExperimentalSuite.java)

These test suites were removed and replaced with a single test suite
called [`DynamicTestSuite`](./DynamicTestSuite.kt).

The reason for this change is that we want to be able to dynamically build the `TestSuite` based on
a `Descriptor` and also decouple the actual files on disk from the UI that are available in the app.