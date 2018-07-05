package org.openobservatory.ooniprobe.test2;

import android.util.Log;

import com.google.gson.Gson;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.AbstractJsonResult;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.JsonResultHttp;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public abstract class AbstractTest<JR extends AbstractJsonResult> {
	String name;
	Result result;
	Measurement measurement;
	BaseTest test;
	private Class<JR> classOfResult;
	private Gson gson;
	PreferenceManager preferenceManager;

	public AbstractTest(AbstractActivity activity, BaseTest test, Class<JR> classOfResult) {
		preferenceManager = activity.getPreferenceManager();
		this.test = test;
		this.classOfResult = classOfResult;
		gson = activity.getGson();
		String filesDir = activity.getFilesDir().toString();
		test.use_logcat();
		test.set_output_filepath(filesDir + "/" + new Random().nextInt() + ".json"); // TODO check file name
		test.set_error_filepath(filesDir + "/" + new Random().nextInt() + ".log"); // TODO check file name
		test.set_verbosity(BuildConfig.DEBUG ? LogSeverity.LOG_DEBUG2 : LogSeverity.LOG_INFO);
		test.set_option("geoip_country_path", filesDir + "/GeoIP.dat");
		test.set_option("geoip_asn_path", filesDir + "/GeoIPASNum.dat");
		test.set_option("save_real_probe_ip", preferenceManager.getIncludeIp());
		test.set_option("save_real_probe_asn", preferenceManager.getIncludeAsn());
		test.set_option("save_real_probe_cc", preferenceManager.getIncludeCc());
		test.set_option("no_collector", preferenceManager.getNoUploadResults());
		test.set_option("software_name", "ooniprobe-android");
		test.set_option("software_version", VersionUtils.get_software_version());
	}

	public List<JR> run(int index, TestCallback testCallback) {
		List<JR> jrList = new ArrayList<>();
		test.on_progress((v, s) -> testCallback.onProgress(Double.valueOf((index + v) * 100).intValue()));
		test.on_log((l, s) -> testCallback.onLog(s));
		test.on_entry(entry -> {
			Log.d("entry", entry);
			JR r = gson.fromJson(entry, classOfResult);
			onEntry(r);
			jrList.add(r);
		});
		testCallback.onStart("test: " + index);
		test.run();
		return jrList;
	}

	public void onEntry(JR json){
		if (json != null) {
			if (json.test_start_time != null)
				result.setStartTimeWithUTCstr(json.test_start_time);
			if (json.measurement_start_time != null)
				measurement.setStartTimeWithUTCstr(json.measurement_start_time);
			if (json.test_runtime != null) {
				measurement.duration = json.test_runtime;
				result.addDuration(json.test_runtime);
			}
			//if the user doesn't want to share asn leave null on the db object
			if (json.probe_asn != null && preferenceManager.isIncludeAsn()) {
				//TODO-SBS asn name
				measurement.asn = json.probe_asn;
				measurement.asnName = "Vodafone";
				if (result.asn == null){
					result.asn = json.probe_asn;
					result.asnName = "Vodafone";
				}
				else if (!measurement.asn.equals(result.asn))
					System.out.println("Something's wrong");
			}
			if (json.probe_cc != null && preferenceManager.isIncludeCc()) {
				measurement.country = json.probe_cc;
				if (result.country == null){
					result.country = json.probe_cc;
				}
				else if (!measurement.country.equals(result.country))
					System.out.println("Something's wrong");
			}
			if (json.probe_ip != null && preferenceManager.isIncludeIp()) {
				measurement.ip = json.probe_ip;
				if (result.ip == null){
					result.ip = json.probe_ip;
				}
				else if (!measurement.ip.equals(result.ip))
					System.out.println("Something's wrong");
			}

			if (json.report_id != null) {
				measurement.reportId = json.report_id;
			}
		}
		else
			measurement.state = measurementFailed;
	}

	public void updateSummary() {
		Summary summary = result.getSummary();
		if (measurement.state != measurementFailed)
			summary.failedMeasurements--;
		if (!measurement.anomaly)
			summary.okMeasurements++;
		else
			summary.anomalousMeasurements++;
		result.setSummary();
		result.save();
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}

	public abstract static class TestJsonResult extends AbstractTest<JsonResult> {
		TestJsonResult(AbstractActivity activity, BaseTest test) {
			super(activity, test, JsonResult.class);
		}
	}

	public abstract static class TestJsonResultHttp extends AbstractTest<JsonResultHttp> {
		TestJsonResultHttp(AbstractActivity activity, BaseTest test) {
			super(activity, test, JsonResultHttp.class);
		}
	}
}
