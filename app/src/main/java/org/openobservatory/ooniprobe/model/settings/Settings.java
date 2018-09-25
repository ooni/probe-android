package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.utils.ConnectionState;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Settings {
	@SerializedName("annotations")
	public HashMap<String, String> annotations;
	@SerializedName("disabled_events")
	public ArrayList<String> disabled_events;
	@SerializedName("inputs")
	public ArrayList<String> inputs;
	@SerializedName("log_level")
	public String log_level;
	@SerializedName("name")
	public String name;
	@SerializedName("options")
	public Options options;

	public Settings(Context c, PreferenceManager pm) {
		this.annotations = new HashMap<>();
		this.annotations.put("network_type", ConnectionState.getInstance(c).getNetworkType());
		this.disabled_events = new ArrayList<>(Arrays.asList("status.queued", "status.update.websites", "failure.report_close"));
		this.log_level = BuildConfig.DEBUG ? "DEBUG" : "INFO";
		this.options = new Options(c, pm);
	}

	public static class Options {
		@SerializedName("geoip_asn_path")
		public String geoip_asn_path;
		@SerializedName("geoip_country_path")
		public String geoip_country_path;
		@SerializedName("max_runtime")
		public Integer max_runtime;
		@SerializedName("no_collector")
		public Integer no_collector;
		@SerializedName("save_real_probe_asn")
		public String save_real_probe_asn;
		@SerializedName("save_real_probe_cc")
		public String save_real_probe_cc;
		@SerializedName("save_real_probe_ip")
		public String save_real_probe_ip;
		@SerializedName("software_name")
		public String software_name;
		@SerializedName("software_version")
		public String software_version;
		@SerializedName("server")
		public String server;
		@SerializedName("port")
		public Integer port;
		@SerializedName("all_endpoints")
		public Integer all_endpoints;

		public Options(Context c, PreferenceManager pm) {
			geoip_asn_path = c.getCacheDir() + "/" + Application.GEO_IPASNUM;
			geoip_country_path = c.getCacheDir() + "/" + Application.GEO_IP;
			no_collector = pm.getNoUploadResults();
			save_real_probe_asn = pm.getIncludeAsn();
			save_real_probe_cc = pm.getIncludeCc();
			save_real_probe_ip = pm.getIncludeIp();
			software_name = "ooniprobe-android";
			software_version = VersionUtils.get_software_version();
		}
	}
}
