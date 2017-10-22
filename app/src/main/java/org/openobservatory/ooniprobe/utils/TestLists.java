package org.openobservatory.ooniprobe.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TestLists {

    public static List<String> getUrlsForCountry(Context c, String country){
        List<String> global_urls = getURLs(readCVSFromAssetFolder(c, "global"));
        List<String> local_urls = getURLs(readCVSFromAssetFolder(c, country));
        if (local_urls.size() > 0)
            global_urls.addAll(local_urls);
        return global_urls;
    }

    //https://inducesmile.com/android-tips/android-how-to-read-csv-file-from-remote-server-or-assets-folder-in-android/
    private static List<String[]> readCVSFromAssetFolder(Context c, String country){
        List<String[]> csvLine = new ArrayList<>();
        String[] content = null;
        try {
            InputStream inputStream = c.getAssets().open("test_lists/"+ country +".csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = br.readLine()) != null){
                content = line.split(",");
                csvLine.add(content);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvLine;
    }

    private static List<String> getURLs(List<String[]> result){
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < result.size(); i++){
            String [] rows = result.get(i);
            urls.add(rows[0]);
        }
        return urls;
    }
}
