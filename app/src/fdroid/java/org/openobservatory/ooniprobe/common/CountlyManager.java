package org.openobservatory.ooniprobe.common;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

public class CountlyManager {

    //TODO remove all the non needed methods
    public static void register(Context ctx, PreferenceManager preferenceManager){
    }

    public static void reloadConsent(Context ctx, PreferenceManager preferenceManager){
    }

    public static void recordEvent(String title) {
    }

    public static void recordEvent(String title, HashMap<String, Object> segmentation) {
    }

    public static void recordView(String title) {
    }

    public static void onStart(Activity activity){
    }

    public static void onStop(){
    }

    public static void setToken(String token){
    }

    public static void initPush(Application app){
    }

    public static void reloadCrashConsent(Application app, PreferenceManager preferenceManager) {
    }

    }
