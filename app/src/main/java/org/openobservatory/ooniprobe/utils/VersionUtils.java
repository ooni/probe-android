package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.BuildConfig;

public class VersionUtils {

    public static String get_software_version(){
        String release_name = "-rc.5";
        return BuildConfig.VERSION_NAME + release_name + "+" +BuildConfig.VERSION_CODE;
    }
}
