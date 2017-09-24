package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.model.OONITests;

public class VersionUtils {

    public static String get_software_version(){
        return BuildConfig.VERSION_NAME + BuildConfig.RELEASE_NAME + "+" +BuildConfig.VERSION_CODE;
    }
}
