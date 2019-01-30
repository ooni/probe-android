package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.MKOrchestraClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings {
	@SerializedName("annotations")
	public final HashMap<String, String> annotations;
	@SerializedName("disabled_events")
	public final List<String> disabled_events;
	@SerializedName("log_level")
	public final String log_level;
	@SerializedName("options")
	public final Options options;
	@SerializedName("inputs")
	public List<String> inputs;
	@SerializedName("name")
	public String name;

	public Settings(Context c, PreferenceManager pm) {
		annotations = new HashMap<>();
		annotations.put("network_type", MKOrchestraClient.getNetworkType(c));
		annotations.put("flavor", BuildConfig.FLAVOR);
		disabled_events = Arrays.asList("status.queued", "status.update.websites", "failure.report_close");
		log_level = pm.isDebugLogs() ? "DEBUG2" : "INFO";
		options = new Options(c, pm);
	}

	public static class Options {
		@SerializedName("net/ca_bundle_path")
		public final String ca_bundle_path;
		@SerializedName("geoip_asn_path")
		public final String geoip_asn_path;
		@SerializedName("geoip_country_path")
		public final String geoip_country_path;
		@SerializedName("no_collector")
		public final boolean no_collector;
		@SerializedName("save_real_probe_asn")
		public final boolean save_real_probe_asn;
		@SerializedName("save_real_probe_cc")
		public final boolean save_real_probe_cc;
		@SerializedName("save_real_probe_ip")
		public final boolean save_real_probe_ip;
		@SerializedName("software_name")
		public final String software_name;
		@SerializedName("software_version")
		public final String software_version;
		@SerializedName("randomize_input")
		public final boolean randomize_input;
		@SerializedName("no_file_report")
		public final boolean no_file_report;
		@SerializedName("max_runtime")
		public Integer max_runtime;
		@SerializedName("server")
		public String server;
		@SerializedName("port")
		public Integer port;
		@SerializedName("all_endpoints")
		public Integer all_endpoints;

		public Options(Context c, PreferenceManager pm) {
			ca_bundle_path = c.getCacheDir() + "/" + Application.CA_BUNDLE;
			geoip_asn_path = c.getCacheDir() + "/" + Application.ASN_MMDB;
			geoip_country_path = c.getCacheDir() + "/" + Application.COUNTRY_MMDB;
			no_collector = pm.isUploadResults();
			save_real_probe_asn = pm.isIncludeAsn();
			save_real_probe_cc = pm.isIncludeCc();
			save_real_probe_ip = pm.isIncludeIp();
			software_name = "ooniprobe-android";
			software_version = BuildConfig.VERSION_NAME;
			randomize_input = false;
			no_file_report = true;
		}
	}
}
