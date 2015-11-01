// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.ooni;

import io.github.measurement_kit.common.NetTest;

public class HTTPInvalidRequestLine extends NetTest {
    public HTTPInvalidRequestLine(String backend) {
        mBackend = backend;
    }

    public native long allocTest();

    private String mBackend;
}
