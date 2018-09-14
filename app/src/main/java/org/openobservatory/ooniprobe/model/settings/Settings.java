package org.openobservatory.ooniprobe.model.settings;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.utils.ConnectionState;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings {
    //TODO NSDictionary
    @SerializedName("annotations")
    public HashMap<String,String> annotations;
    //TODO NSArray
    @SerializedName("disabled_events")
    public ArrayList disabled_events;
    //TODO NSArray
    @SerializedName("inputs")
    public ArrayList inputs;
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

    public Settings(AbstractActivity activity) {
        this.annotations = new HashMap<>();
        this.annotations.put("network_type", ConnectionState.getInstance(activity).getNetworkType());
        this.disabled_events = new ArrayList<>(Arrays.asList("status.queued", "failure.report_close"));
        //this.log_filepath = new File(activity.getFilesDir(), Measurement.getLogFileName(result.id, name)).getPath();
        this.log_level = BuildConfig.DEBUG ? "DEBUG" : "INFO";
        this.options = new Options(activity);
    }

    public static class Options {
        @SerializedName("geoip_asn_path")
        public String geoip_asn_path;
        @SerializedName("geoip_country_path")
        public String geoip_country_path;
        @SerializedName("max_runtime")
        public Double max_runtime;
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
        public Double port;
        @SerializedName("all_endpoints")
        public int all_endpoints;

        public Options(AbstractActivity activity) {
            this.geoip_asn_path = activity.getFilesDir() + "/GeoIP.dat";
            this.geoip_country_path = activity.getFilesDir() + "/GeoIPASNum.dat";
            //this.max_runtime = ;
            this.no_collector = activity.getPreferenceManager().getNoUploadResults();
            this.save_real_probe_asn = activity.getPreferenceManager().getIncludeAsn();
            this.save_real_probe_cc = activity.getPreferenceManager().getIncludeCc();
            this.save_real_probe_ip = activity.getPreferenceManager().getIncludeIp();
            this.software_name = "ooniprobe-android";
            this.software_version = VersionUtils.get_software_version();
            //this.server = ;
            //this.port = ;
            //this.all_endpoints = ;
        }

    }
}
