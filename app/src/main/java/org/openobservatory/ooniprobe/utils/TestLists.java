package org.openobservatory.ooniprobe.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TestLists {
    private static TestLists instance;
    public static String probe_cc;
    public static String probe_asn;

    public static TestLists getInstance() {
        if (instance == null) {
            instance = new TestLists();
            probe_cc = "ru";
        }
        return instance;
    }

    public static ArrayList<String> getUrls(Context context){
        ArrayList<String> global_urls = getURLsforAsset(readCVSFromAssetFolder(context, "global"));
        ArrayList<String> local_urls = getURLsforAsset(readCVSFromAssetFolder(context, probe_cc));
        if (local_urls.size() > 0)
            global_urls.addAll(local_urls);
        return global_urls;
    }

    //https://inducesmile.com/android-tips/android-how-to-read-csv-file-from-remote-server-or-assets-folder-in-android/
    private static ArrayList<String[]> readCVSFromAssetFolder(Context context, String country){
        ArrayList<String[]> csvLine = new ArrayList<>();
        String[] content = null;
        try {
            InputStream inputStream = context.getAssets().open("test_lists/"+ country +".csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = br.readLine()) != null){
                content = line.split(",");
                csvLine.add(content);
            }
            br.close();
        } catch (IOException e) {
            //java.io.FileNotFoundException will return empty array
            e.printStackTrace();
        }
        return csvLine;
    }

    private static ArrayList<String> getURLsforAsset(List<String[]> result){
        ArrayList<String> urls = new ArrayList<>();
        if (result.size() > 1){
            //Skipping first line
            for (int i = 1; i < result.size(); i++){
                String [] rows = result.get(i);
                urls.add(rows[0]);
            }
        }
        return urls;
    }
}
