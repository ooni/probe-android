package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings {
	@SerializedName("annotations")
	public HashMap<String, String> annotations;
	@SerializedName("disabled_events")
	public List<String> disabled_events;
	@SerializedName("inputs")
	public List<String> inputs;
	@SerializedName("log_level")
	public String log_level;
	@SerializedName("name")
	public String name;
	@SerializedName("options")
	public Options options;

	public Settings(Context c, PreferenceManager pm) {
		annotations = new HashMap<>();
		annotations.put("network_type", pm.getNetworkType());
		annotations.put("flavor", BuildConfig.FLAVOR);
		disabled_events = Arrays.asList("status.queued", "status.update.websites", "failure.report_close");
		log_level = pm.isDebugLogs() ? "DEBUG2" : "INFO";
		options = new Options(c, pm);
	}

	public static class Options {
		@SerializedName("net/ca_bundle_path")
		public String ca_bundle_path;
		@SerializedName("geoip_asn_path")
		public String geoip_asn_path;
		@SerializedName("geoip_country_path")
		public String geoip_country_path;
		@SerializedName("max_runtime")
		public Integer max_runtime;
		@SerializedName("no_collector")
		public boolean no_collector;
		@SerializedName("save_real_probe_asn")
		public boolean save_real_probe_asn;
		@SerializedName("save_real_probe_cc")
		public boolean save_real_probe_cc;
		@SerializedName("save_real_probe_ip")
		public boolean save_real_probe_ip;
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
		@SerializedName("randomize_input")
		public boolean randomize_input;
		@SerializedName("no_file_report")
		public boolean no_file_report;

		public Options(Context c, PreferenceManager pm) {
			ca_bundle_path = c.getCacheDir() + "/" + Application.CA_BUNDLE;
			geoip_asn_path = c.getCacheDir() + "/" + Application.ASN_MMDB;
			geoip_country_path = c.getCacheDir() + "/" + Application.COUNTRY_MMDB;
			no_collector = !pm.isUploadResults();
			save_real_probe_asn = pm.isIncludeAsn();
			save_real_probe_cc = pm.isIncludeCc();
			save_real_probe_ip = pm.isIncludeIp();
			software_name = "ooniprobe-android";
			software_version = VersionUtils.get_software_version();
			randomize_input = false;
			no_file_report = true;
		}
	}
}
