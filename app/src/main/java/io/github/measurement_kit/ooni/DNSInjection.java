// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.ooni;

import io.github.measurement_kit.common.NetTest;

public class DNSInjection extends NetTest {
    public DNSInjection(String filePath, String nameServer) {
        mFilePath = filePath;
        mNameServer = nameServer;
    }

    public native long allocTest();

    private String mFilePath;
    private String mNameServer;
}
