package org.openobservatory.engine;

import android.util.Log;

/** AndroidLogger is a logger that logs with the Android logging system. */
public class AndroidLogger implements Logger {
    private String TAG_ENGINE = "engine";

    public void debug(String message) {
        Log.d(TAG_ENGINE, message);
    }

    public void info(String message) {
        Log.i(TAG_ENGINE, message);
    }

    public void warn(String message) {
        Log.w(TAG_ENGINE, message);
    }
}
