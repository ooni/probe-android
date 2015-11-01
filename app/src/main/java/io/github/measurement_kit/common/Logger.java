// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.common;

public class Logger {
    public static native void setVerbose(int verbose);

    public static native void useAndroidLogger();
}
