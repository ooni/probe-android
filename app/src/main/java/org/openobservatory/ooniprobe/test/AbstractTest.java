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
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import static org.openobservatory.ooniprobe.model.Measurement.State.FAILED;

public abstract class AbstractTest {
	protected Measurement measurement;
	protected BaseTest test;
	protected PreferenceManager preferenceManager;
	private Gson gson;
	private long timestamp;

	public AbstractTest(AbstractActivity activity, String name, BaseTest test, Result result) {
		//TODO-ALE managing db class
		measurement = new Measurement(result, name);
		measurement.save();
		preferenceManager = activity.getPreferenceManager();
		this.test = test;
		gson = activity.getGson();
		timestamp = System.currentTimeMillis();
		test.use_logcat();
		setFilepaths(activity, test);
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

	protected void setFilepaths(Context context, BaseTest test) {
		test.set_output_filepath(context.getFilesDir() + measurement.name + "-" + measurement.id + ".json");
		test.set_error_filepath(context.getFilesDir() + measurement.name + "-" + measurement.id + ".log");
	}

	public void run(int index, TestCallback testCallback) {
		test.on_progress((v, s) -> testCallback.onProgress(Double.valueOf((index + v) * 100).intValue()));
		test.on_log((l, s) -> testCallback.onLog(s));
		test.on_entry(entry -> {
			Log.d("entry", entry);
			JsonResult jr = gson.fromJson(entry, JsonResult.class);
			if (jr == null)
				measurement.state = FAILED;
			else
				onEntry(jr);
			updateSummary();
			measurement.save();
		});
		testCallback.onStart("test: " + index);
		measurement.state = Measurement.State.ACTIVE;
		measurement.save();
		test.run();
		measurement.save();
	}

	@CallSuper public void onEntry(@NonNull JsonResult json) {
		if (json.testStartTime != null)
			measurement.result.startTime = json.testStartTime;
		if (json.measurementStartTime != null)
			measurement.startTime = json.measurementStartTime;
		if (json.test_runtime != null) {
			measurement.duration = json.test_runtime;
			measurement.result.addDuration(json.test_runtime);
		}
		//if the user doesn't want to share asn leave null on the db object
		if (json.probeAsn != null && preferenceManager.isIncludeAsn()) {
			//TODO-SBS asn name
			measurement.asn = json.probeAsn;
			measurement.asnName = "Vodafone";
			if (measurement.result.asn == null) {
				measurement.result.asn = json.probeAsn;
				measurement.result.asnName = "Vodafone";
			} else if (!measurement.asn.equals(measurement.result.asn))
				System.out.println("Something's wrong");
		}
		if (json.probeCc != null && preferenceManager.isIncludeCc()) {
			measurement.country = json.probeCc;
			if (measurement.result.country == null) {
				measurement.result.country = json.probeCc;
			} else if (!measurement.country.equals(measurement.result.country))
				System.out.println("Something's wrong");
		}
		if (json.probeIp != null && preferenceManager.isIncludeIp()) {
			measurement.ip = json.probeIp;
			if (measurement.result.ip == null) {
				measurement.result.ip = json.probeIp;
			} else if (!measurement.ip.equals(measurement.result.ip))
				System.out.println("Something's wrong");
		}
		if (json.reportId != null) {
			measurement.reportId = json.reportId;
		}
		measurement.result.getSummary().getTestKeysMap().put(measurement.name, json.testKeys);
	}

	private void updateSummary() {
		Summary summary = measurement.result.getSummary();
		if (measurement.state != FAILED)
			summary.failedMeasurements--;
		if (!measurement.anomaly)
			summary.okMeasurements++;
		else
			summary.anomalousMeasurements++;
		measurement.result.setSummary();
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}
}
