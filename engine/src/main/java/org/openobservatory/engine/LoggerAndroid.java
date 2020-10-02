package org.openobservatory.engine;

import android.util.Log;

/** AndroidLogger is a logger that logs with the Android logging system. */
final class LoggerAndroid implements OONILogger {
    private String TAG_ENGINE = "engine";

    @Override
    public void debug(String message) {
        Log.d(TAG_ENGINE, message);
    }

    @Override
    public void info(String message) {
        Log.i(TAG_ENGINE, message);
    }

    @Override
    public void warn(String message) {
        Log.w(TAG_ENGINE, message);
    }
}
