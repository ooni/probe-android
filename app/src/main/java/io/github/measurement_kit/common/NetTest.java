// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.common;

import java.util.ArrayList;
import java.util.List;

public class NetTest {
    public void setVerbose(boolean verbose) {
        mVerbose = verbose;
    }

    public boolean getVerbose() {
        return mVerbose;
    }

    public native long allocTest();

    public synchronized List<String> getLogs() {
        return mLogs;
    }

    public synchronized void appendLogLine(String log) {
        mLogs.add(log);
    }

    public synchronized void clearLogs() {
        mLogs.clear();
    }

    private boolean mVerbose = false;
    private List<String> mLogs = new ArrayList<>();
}
