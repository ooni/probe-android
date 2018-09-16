package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.utils.ConnectionState;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings {
    @SerializedName("annotations")
    public HashMap<String,String> annotations;
    @SerializedName("disabled_events")
    public ArrayList disabled_events;
    @SerializedName("inputs")
    public ArrayList inputs;
    //TODO not used
    @SerializedName("log_filepath")
    public String log_filepath;
    @SerializedName("log_level")
    public String log_level;
    @SerializedName("name")
    public String name;
    //TODO not used
    @SerializedName("output_filepath")
    public String output_filepath;
    @SerializedName("options")
    public Options options;

    public Settings(Context c, PreferenceManager pm) {
        this.annotations = new HashMap<>();
        this.annotations.put("network_type", ConnectionState.getInstance(c).getNetworkType());
        this.disabled_events = new ArrayList<>(Arrays.asList("status.queued", "failure.report_close"));
        this.log_level = BuildConfig.DEBUG ? "DEBUG" : "INFO";
        this.options = new Options(c, pm);
    }

    public static class Options {
        @SerializedName("geoip_asn_path")
        public String geoip_asn_path;
        @SerializedName("geoip_country_path")
        public String geoip_country_path;
        @SerializedName("max_runtime")
        public int max_runtime;
        @SerializedName("no_collector")
        //TODO make it bool
        public String no_collector;
        @SerializedName("save_real_probe_asn")
        //TODO make it bool
        public String save_real_probe_asn;
        @SerializedName("save_real_probe_cc")
        //TODO make it bool
        public String save_real_probe_cc;
        @SerializedName("save_real_probe_ip")
        //TODO make it bool
        public String save_real_probe_ip;
        @SerializedName("software_name")
        public String software_name;
        @SerializedName("software_version")
        public String software_version;
        @SerializedName("server")
        public String server;
        @SerializedName("port")
        public int port;
        @SerializedName("all_endpoints")
        public int all_endpoints;

        public Options(Context c, PreferenceManager pm) {
            geoip_asn_path = c.getFilesDir() + "/GeoIP.dat";
            geoip_country_path = c.getFilesDir() + "/GeoIPASNum.dat";
            no_collector = pm.getNoUploadResults();
            save_real_probe_asn = pm.getIncludeAsn();
            save_real_probe_cc = pm.getIncludeCc();
            save_real_probe_ip = pm.getIncludeIp();
            software_name = "ooniprobe-android";
            software_version = VersionUtils.get_software_version();
        }

    }
}
