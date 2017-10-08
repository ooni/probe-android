package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.model.OONITests;

public class VersionUtils {

    public static String get_software_version(){
        // FIXME: submitting the VERSION_CODE breaks OONI backend
        //return BuildConfig.VERSION_NAME + "+" + BuildConfig.VERSION_CODE;
        return BuildConfig.VERSION_NAME;
    }
}
