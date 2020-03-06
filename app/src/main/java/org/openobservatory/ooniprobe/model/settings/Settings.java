package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;

import java.util.Arrays;
import java.util.List;

import io.ooni.mk.MKResourcesManager;

public class Settings {
	@SerializedName("annotations")
	public final Annotations annotations;
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
		annotations = new Annotations(c);
		disabled_events = Arrays.asList("status.queued", "status.update.websites", "failure.report_close");
		log_level = pm.isDebugLogs() ? "DEBUG2" : "INFO";
		options = new Options(c, pm);
	}

	public static class Annotations {
		@SerializedName("network_type")
		public final String network_type;
		@SerializedName("flavor")
		public final String flavor;
		@SerializedName("origin")
		public String origin;

		public Annotations(Context c) {
			this.network_type = ReachabilityManager.getNetworkType(c);
			this.flavor = BuildConfig.FLAVOR;
		}
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
			ca_bundle_path = MKResourcesManager.getCABundlePath(c);
			geoip_country_path = MKResourcesManager.getCountryDBPath(c);
			geoip_asn_path = MKResourcesManager.getASNDBPath(c);
			no_collector = !pm.isUploadResults();
			save_real_probe_asn = pm.isIncludeAsn();
			save_real_probe_cc = pm.isIncludeCc();
			save_real_probe_ip = pm.isIncludeIp();
			software_name = c.getString(R.string.software_name);
			software_version = BuildConfig.VERSION_NAME;
			randomize_input = false;
			no_file_report = true;
		}
	}
}
