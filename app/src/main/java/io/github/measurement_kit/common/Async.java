// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.common;

public class Async {
    // We use the singleton pattern to guarantee that the Async object is
    // alive for the whole application life-cycle. This simplifies reasoning
    // about the life cycle of C++ and Java objects in MeasurementKit.

    public static Async getInstance() {
        return mInstance;
    }

    public native void runTest(NetTest test, Runnable callback);

    public native void loopOnce();

    private static native long alloc();

    private Async() {
        if ((mPointer = alloc()) == 0L) {
            throw new OutOfMemoryError();
        }
    }

    private static Async mInstance = new Async();
    private long mPointer = 0L;
}
