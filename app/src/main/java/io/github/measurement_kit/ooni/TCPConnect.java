// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.ooni;

import io.github.measurement_kit.common.NetTest;

public class TCPConnect extends NetTest {
    public TCPConnect(String filePath, String port) {
        mFilePath = filePath;
        mPort = port;
    }

    public native long allocTest();

    private String mFilePath;
    private String mPort;
}
