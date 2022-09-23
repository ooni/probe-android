package org.openobservatory.ooniprobe.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.velmurugan.inapplogger.InAppLogger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppLogger {

    private final InAppLogger logger;

    @Inject
    public AppLogger(@NonNull Context context) {
        logger = new InAppLogger(context);
        /*TODO(aanorbel): 01/06/2022  Check log retention policy and clear logs appropriately.
            logs only exist for an app session. Log file grows with each test run making
            time to load logs to increase.*/
        logger.deleteOldLog();
    }

    public void e(String tag, String message) {
        logger.e(String.format("%s : %s", tag, message));
    }

    public void w(String tag, String message) {
        logger.w(String.format("%s: %s", tag, message));
    }

    public void i(String tag, String message) {
        logger.i(String.format("%s : %s", tag, message));
    }

    public void deleteOldLog() {
        logger.deleteOldLog();
    }

    public File getLogFile() {
        return logger.getLogFile();
    }

    public List<String> getLog(String tag) {
        return Arrays.asList(logger.getLog(tag).toString().split("\n"));
    }

    public StringBuilder getLogText(String tag) {
        return logger.getLog(tag);
    }
}
