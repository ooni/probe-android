package org.openobservatory.ooniprobe.utils;

import android.content.Context;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.swig.Error;
import org.openobservatory.measurement_kit.swig.OrchestrateClient;
import org.openobservatory.measurement_kit.swig.OrchestrateFindLocationCallback;

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
    public static Context context;

    //TODO DEPRECATED CLASS
    public static TestLists getInstance(Context ctx) {
        if (instance == null) {
            instance = new TestLists();
            context = ctx;
            probe_cc = "";
            probe_asn = "";
        }
        return instance;
    }

    public static ArrayList<String> getUrls(){
        ArrayList<String> global_urls = getURLsforAsset(readCVSFromAssetFolder("global"));
        return global_urls;
    }

    //https://inducesmile.com/android-tips/android-how-to-read-csv-file-from-remote-server-or-assets-folder-in-android/
    private static ArrayList<String[]> readCVSFromAssetFolder(String country){
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

    public static void updateCC_async() {
        String geoip_asn_path = context.getFilesDir() + "/GeoIPASNum.dat";
        String geoip_country_path = context.getFilesDir() + "/GeoIP.dat";
        final OrchestrateClient client = new OrchestrateClient();
        client.set_verbosity(LogSeverity.LOG_DEBUG);
        client.use_logcat();
        client.set_geoip_country_path(geoip_country_path);
        client.set_geoip_asn_path(geoip_asn_path);
        client.find_location(
                new OrchestrateFindLocationCallback() {
                    @Override
                    public void callback(
                            final Error error, final String asn,
                            final String cc) {
                        if (error.as_bool()) {
                            System.out.println(error.reason());
                            return;
                        }
                        System.out.println("ASN: " + cc);
                        System.out.println("CC: " + asn);
                        client.set_probe_asn(asn);
                        client.set_probe_cc(cc);
                        probe_cc = cc;
                        probe_asn = asn;
                    }
                });
    }
}
