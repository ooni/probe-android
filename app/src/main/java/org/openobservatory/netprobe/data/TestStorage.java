package org.openobservatory.netprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;

import org.openobservatory.netprobe.model.NetworkMeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lorenzo on 27/06/16.
 */
public class TestStorage {
    public static final String PREFS_NAME = "NETPROBE_APP";
    public static final String TESTS = "Test";

    public TestStorage() {
        super();
    }

    public void storeTests(Context context, List tests) {
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
    public ArrayList loadTests(Context context) {
    // used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List tests;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(TESTS)) {
            String jsonTests = settings.getString(TESTS, null);
            Gson gson = new Gson();
            NetworkMeasurement[] favoriteItems = gson.fromJson(jsonTests,NetworkMeasurement[].class);
            tests = Arrays.asList(favoriteItems);
            tests = new ArrayList(tests);
        } else
            return new ArrayList();
        return (ArrayList) tests;
    }

    public void setCompleted(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    n.completed = true;
                    tests.set(i, n);
                }
            }
            storeTests(context, tests);
        }
    }

    public void addTest(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests == null)
            tests = new ArrayList();
        tests.add(test);
        storeTests(context, tests);
    }

    public void removeTest(Context context, NetworkMeasurement test) {
        List tests = loadTests(context);
        if (tests != null){
            for(int i = 0; i < tests.size(); i++) {
                NetworkMeasurement n = (NetworkMeasurement)tests.get(i);
                if (n.test_id == test.test_id) {
                    tests.remove(i);
                }
            }
            storeTests(context, tests);
        }
    }
/*
    public void removeTest(Context context, NetworkMeasurement test) {
        ArrayList tests = loadTests(context);
        if (tests != null) {
            tests.remove(test);
            storeTests(context, tests);
        }
    }
    */
}

