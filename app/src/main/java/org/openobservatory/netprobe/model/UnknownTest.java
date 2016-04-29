// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.netprobe.model;

public class UnknownTest extends RuntimeException {
    public UnknownTest(String reason) {
        super(reason);
    }
}
