package org.openobservatory.ooniprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;

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

    public static void storeTests(Context context, List tests) {
    // used for store arrayList in json format
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonTests = gson.toJson(tests);
        editor.putString(TESTS, jsonTests);
        editor.commit();
    }

    public static void newTestDetected(Context context) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean(NEW_TESTS, true);
        editor.commit();
    }

    public static void resetNewTests(Context context) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean(NEW_TESTS, false);
        editor.commit();
    }

    public static ArrayList loadTestsReverse(MainActivity activity) {
        SharedPreferences settings;
        ArrayList tests = new ArrayList<>();
        settings = activity.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (!settings.contains(TESTS)) {
            return new ArrayList();
        }
        String jsonTests = settings.getString(TESTS, null);
        Gson gson = new Gson();
        NetworkMeasurement[] favoriteItems = gson.fromJson(jsonTests,NetworkMeasurement[].class);
        for (int i = 0; i < favoriteItems.length; i++){
            NetworkMeasurement current = favoriteItems[i];
            if (!current.running)
                tests.add(current);
            else if (TestData.getInstance(activity, activity).getTestWithName(current.testName) == null)
                tests.add(current);
            else if (TestData.getInstance(activity, activity).getTestWithName(current.testName).test_id != current.test_id)
                tests.add(current);
        }
        Collections.reverse(tests);
        return tests;
    }

    public static ArrayList loadTests(Context context) {
    // used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List tests;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (!settings.contains(TESTS)) {
            return new ArrayList();
        }
        String jsonTests = settings.getString(TESTS, null);
        Gson gson = new Gson();
        NetworkMeasurement[] favoriteItems = gson.fromJson(jsonTests, NetworkMeasurement[].class);
        tests = Arrays.asList(favoriteItems);
        return new ArrayList(tests);
    }

    public static void setCompleted(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    n.running = false;
                    n.entry = true;
                    tests.set(i, n);
                    newTestDetected(context);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    public static void setEntry(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    n.entry = true;
                    tests.set(i, n);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    public static void setAnomaly(Context context, long test_id, int anomaly) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test_id) {
                    n.anomaly = anomaly;
                    tests.set(i, n);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    public static void setViewed(Context context, long test_id) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test_id) {
                    n.viewed = true;
                    tests.set(i, n);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    public static void setAllViewed(Context context) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (!n.viewed && !n.running){
                    n.viewed = true;
                    tests.set(i, n);
                }
            }
            storeTests(context, tests);
        }
    }

    public static void addTest(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests == null)
            tests = new ArrayList();
        tests.add(test);
        storeTests(context, tests);
    }

    public static void removeTest(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    File jsonFile = new File(context.getFilesDir(), n.json_file);
                    File logFile = new File(context.getFilesDir(), n.log_file);
                    jsonFile.delete();
                    logFile.delete();
                    tests.remove(i);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    public static boolean newTests(Context context){
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return settings.getBoolean(NEW_TESTS, false);
    }

    public static void removeAllTests(MainActivity activity, Context context) {
        List toRemove = loadTestsReverse(activity);
        List test_ids = new ArrayList();
        if (toRemove != null){
            for(int i = 0; i < toRemove.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)toRemove.get(i);
                test_ids.add(n.test_id);
            }
        }
        List tests = loadTests(context);
        if (tests != null && test_ids.size() > 0){
            for(int i = tests.size()-1; i >= 0; i--) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (test_ids.contains(n.test_id)) {
                    File jsonFile = new File(context.getFilesDir(), n.json_file);
                    File logFile = new File(context.getFilesDir(), n.log_file);
                    jsonFile.delete();
                    logFile.delete();
                    //System.out.println("remove "+ i + " jsonFile " + jsonFile);
                    tests.remove(i);
                }
            }
            storeTests(context, tests);
            resetNewTests(context);
            activity.updateActionBar();
        }
    }

    public static void removeUnusedFiles(Context context) {
        List tests = loadTests(context);
        Set<File> usedFiles = new HashSet<>();
        if (tests != null) {
            for (int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement) tests.get(i);
                usedFiles.add(new File(context.getFilesDir(), n.json_file));
                usedFiles.add(new File(context.getFilesDir(), n.log_file));
            }
        }

        Set<File> allFiles = new HashSet<>(usedFiles);
        allFiles.addAll(Arrays.asList(context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json") || name.toLowerCase().endsWith(".log");
            }
        })));

        allFiles.removeAll(usedFiles);

        Iterator iter = allFiles.iterator();
        while (iter.hasNext()) {
            File file = (File) iter.next();
            file.delete();
        }
    }
}

