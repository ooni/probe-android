package org.openobservatory.ooniprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;

import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestStorage {
    public static final String PREFS_NAME = "OONIPROBE_APP";
    public static final String TESTS = "Test";


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
            else if (TestData.getInstance(activity).getTestWithName(current.testName) == null)
                tests.add(current);
            else if (TestData.getInstance(activity).getTestWithName(current.testName).test_id != current.test_id)
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
        NetworkMeasurement[] favoriteItems = gson.fromJson(jsonTests,NetworkMeasurement[].class);
        tests = Arrays.asList(favoriteItems);
        return new ArrayList(tests);
    }

    public static void setCompleted(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    n.completed = true;
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
                    tests.remove(i);
                    break;
                }
            }
            storeTests(context, tests);
        }
    }

    //NOT USED
    public static void removeTestObject(Context context, NetworkMeasurement test) {
        ArrayList tests = loadTests(context);
        if (tests != null) {
            tests.remove(test);
            storeTests(context, tests);
        }
    }
}

