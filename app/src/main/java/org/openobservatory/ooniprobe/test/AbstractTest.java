package org.openobservatory.ooniprobe.test;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Network;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.io.File;
import java.io.FileOutputStream;

public abstract class AbstractTest {
	protected BaseTest test;
	protected PreferenceManager preferenceManager;
	private Gson gson;
	private Result result;
	private String name;
	private Context context;

	public AbstractTest(AbstractActivity activity, String name, BaseTest test, Result result) {
		context = activity;
		this.result = result;
		this.name = name;
		preferenceManager = activity.getPreferenceManager();
		this.test = test;
		gson = activity.getGson();
		test.use_logcat();
		test.set_error_filepath(new File(activity.getFilesDir(), result.id + "-" + name + ".log").getPath());
		test.set_verbosity(BuildConfig.DEBUG ? LogSeverity.LOG_DEBUG2 : LogSeverity.LOG_INFO);
		test.set_option("geoip_country_path", activity.getFilesDir() + "/GeoIP.dat");
		test.set_option("geoip_asn_path", activity.getFilesDir() + "/GeoIPASNum.dat");
		test.set_option("save_real_probe_ip", preferenceManager.getIncludeIp());
		test.set_option("save_real_probe_asn", preferenceManager.getIncludeAsn());
		test.set_option("save_real_probe_cc", preferenceManager.getIncludeCc());
		test.set_option("no_collector", preferenceManager.getNoUploadResults());
		test.set_option("software_name", "ooniprobe-android");
		test.set_option("software_version", VersionUtils.get_software_version());
	}

	public void run(int index, TestCallback testCallback) {
		testCallback.onStart(name);
		testCallback.onProgress(Double.valueOf(index * 100).intValue());
		test.on_progress((v, s) -> testCallback.onProgress(Double.valueOf((index + v) * 100).intValue()));
		test.on_log((l, s) -> testCallback.onLog(s));
		test.on_entry(entry -> {
			Log.d("entry", entry);
			Measurement measurement = new Measurement(result, name);
			JsonResult jr = gson.fromJson(entry, JsonResult.class);
			if (jr == null)
				measurement.is_failed = true;
			else
				onEntry(jr, measurement);
			measurement.save();
			try {
				FileOutputStream outputStream = context.openFileOutput(measurement.id + "_" + measurement.test_name, Context.MODE_PRIVATE);
				outputStream.write(entry.getBytes());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		test.run();
	}

	@CallSuper public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		if (json.test_start_time != null)
			measurement.result.start_time = json.test_start_time;
		if (json.measurement_start_time != null)
			measurement.start_time = json.measurement_start_time;
		if (json.test_runtime != null) {
			measurement.runtime = json.test_runtime;
			measurement.result.addDuration(json.test_runtime);
		}
		measurement.network = new Network();
		if (json.probe_asn != null && preferenceManager.isIncludeAsn()) {
			measurement.network.asn = json.probe_asn; //TODO-SBS asn name
			measurement.network.network_name = "Vodafone";
		}
		if (json.probe_cc != null && preferenceManager.isIncludeCc())
			measurement.network.country_code = json.probe_cc;
		if (json.probe_ip != null && preferenceManager.isIncludeIp())
			measurement.network.ip = json.probe_ip;
		Network network = Network.querySingle(measurement.network);
		if (network != null)
			measurement.network = network;
		if (json.report_id != null)
			measurement.report_id = json.report_id;
		measurement.setTestKeys(json.test_keys);
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}
}
