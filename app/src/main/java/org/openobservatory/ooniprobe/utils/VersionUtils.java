package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.BuildConfig;

public class VersionUtils {

    public static String get_software_version(){
        return BuildConfig.VERSION_NAME;
    }
}