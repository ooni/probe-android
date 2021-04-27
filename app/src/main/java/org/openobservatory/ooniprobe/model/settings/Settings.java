package org.openobservatory.ooniprobe.model.settings;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.openobservatory.engine.OONIMKTaskConfig;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.test.EngineProvider;

import java.util.Arrays;
import java.util.List;

public class Settings {
	@SerializedName("annotations")
	public final Annotations annotations;

	@SerializedName("assets_dir")
	private String assets_dir;

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

	@SerializedName("proxy")
	public String proxy;

	@SerializedName("state_dir")
	private String state_dir;

	@SerializedName("temp_dir")
	private String temp_dir;

	@SerializedName("tunnel_dir")
	private String tunnel_dir;

	@SerializedName("version")
	private Integer version;

	public Settings(Context c, PreferenceManager pm) {
		annotations = new Annotations(c);
		disabled_events = Arrays.asList("status.queued", "status.update.websites", "failure.report_close");
		log_level = pm.isDebugLogs() ? "DEBUG2" : "INFO";
		version = 1;
		options = new Options(pm);
		proxy = pm.getProxyURL();
	}

	public OONIMKTaskConfig toExperimentSettings(Gson gson, Context c) throws java.io.IOException {
		assets_dir = EngineProvider.get().getAssetsDir(c);
		state_dir = EngineProvider.get().getStateDir(c);
		temp_dir = EngineProvider.get().getTempDir(c);
		return new OONIMKTaskConfigAdapter(gson, this);
	}

	private class OONIMKTaskConfigAdapter implements OONIMKTaskConfig {
		private String serialized;
		private Settings settings;

		OONIMKTaskConfigAdapter(Gson gson, Settings settings) {
			this.serialized = gson.toJson(settings);
			this.settings = settings;
		}

		public String taskName() {
			return settings.name;
		}

		public String serialization() {
			return this.serialized;
		}
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
		@SerializedName("no_collector")
		public boolean no_collector;
		@SerializedName("software_name")
		public final String software_name;
		@SerializedName("software_version")
		public final String software_version;
		@SerializedName("max_runtime")
		public Integer max_runtime;
		@SerializedName("probe_services_base_url")
		public String probe_services_base_url;

		public Options(PreferenceManager pm) {
			no_collector = !pm.isUploadResults();
			software_name = BuildConfig.SOFTWARE_NAME;
			software_version = BuildConfig.VERSION_NAME;
		}
	}
}
