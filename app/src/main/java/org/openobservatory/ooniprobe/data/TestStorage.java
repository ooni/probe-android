package org.openobservatory.ooniprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestStorage {
    public static final String PREFS_NAME = "OONIPROBE_APP";
    public static final String TESTS = "Test";
    public static final String NEW_TESTS = "new_tests";

    //TODO DEPRECATED CLASS - REMOVE
    public static Boolean oldTestsDetected(Context context) {
        // used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List tests;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (!settings.contains(TESTS)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static void removeAllTests(MainActivity activity, Context context) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(NEW_TESTS);
        editor.remove(TESTS);
        editor.commit();

        File dirFiles = context.getFilesDir();
        for (String strFile : dirFiles.list())
        {
            System.out.println("oldTestsDetected FILEFOUND " + strFile);
            /*
 hosts.txt
 GeoIPASNum.dat
 GeoIP.dat
 global.txt
 .Fabric
 orchestration_secret.json
 test-1527505390226.log
 test-1527505390777.log
 test-1527505391176.log
 test-1527505391726.log
 test-1527505390226.json
 test-1527505392610.log
 test-1527505393160.log
 test-1527505390777.json
 test-1527505391176.json
 test-1527505391726.json
 test-1527505392610.json
             */
        }

    }

}

