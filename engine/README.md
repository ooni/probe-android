# module: engine

This directory contains a Gradle module that wraps the
`oonimkall.aar` measurement library produced by `ooni/probe-cli`.

Depending on the configuration you're using, we'll download
`oonimkall.aar` from Maven Central, or we're using a local
file `oonimkall.aar` inside of the `../engine-experimental`
Gradle module. See [build.gradle](build.gradle) for more details.

