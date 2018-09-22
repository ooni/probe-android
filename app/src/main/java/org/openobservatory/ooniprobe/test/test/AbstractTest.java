package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;
import org.openobservatory.ooniprobe.utils.ConnectionState;

import java.io.FileOutputStream;
import java.util.HashMap;

import io.ooni.mk.Task;

public abstract class AbstractTest {
	private final String TAG = "MK_EVENT";
	private String name;
	private String mkName;
	private int labelResId;
	private int iconResId;
	private HashMap<Double, Measurement> measurements;
	private String reportId;

	public AbstractTest(String name, String mkName, int labelResId, int iconResId) {
		this.name = name;
		this.mkName = mkName;
		this.labelResId = labelResId;
		this.iconResId = iconResId;
	}

	public abstract void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback);

	protected void run(Context c, PreferenceManager pm, Gson gson, Settings settings, Result result, int index, TestCallback testCallback) {
		settings.name = mkName;
		measurements = new HashMap<>();
		Task task = Task.startNettest(gson.toJson(settings));
		while (!task.isDone()) {
			try {
				String json = task.waitForNextEvent().serialize();
				Log.d(TAG, json);
				EventResult event = gson.fromJson(json, EventResult.class);
				if (event.key.equals("status.started")) {
					testCallback.onStart(c.getString(labelResId));
					testCallback.onProgress(Double.valueOf(index * 100).intValue());
				} else if (event.key.equals("status.measurement_start")) {
					Measurement measurement = new Measurement(result, name, reportId);
					if (event.input.length() > 0)
						measurement.url = Url.getUrl(event.input);
					measurements.put(event.value.key, measurement);
					measurement.save();
				} else if (event.key.equals("status.geoip_lookup")) {
					saveNetworkInfo(event.value, result, c);
				} else if (event.key.equals("log")) {
					//TODO-ALE write log line on disk file + "\n"
					//new File(c.getFilesDir(), Result.getLogFileName(result.id, name)).getPath();
					testCallback.onLog(event.value.message);
				} else if (event.key.equals("status.progress")) {
					testCallback.onProgress(Double.valueOf((index + event.value.percentage) * 100).intValue());
				} else if (event.key.compareTo("measurement") == 0) {
					Measurement measurement = measurements.get(event.value.idx);
					if (measurement != null) {
						JsonResult jr = gson.fromJson(event.value.json_str, JsonResult.class); // TODO check if is string or object
						if (jr == null)
							measurement.is_failed = true;
						else
							onEntry(c, pm, jr, measurement);
						measurement.save();
						try {
							FileOutputStream outputStream = c.openFileOutput(Measurement.getEntryFileName(measurement.id, measurement.test_name), Context.MODE_PRIVATE);
							outputStream.write(event.value.json_str.getBytes());
							outputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (event.key.equals("status.report_create")) {
					//The report id is created before running measurements
					reportId = event.value.report_id;
				} else if (event.key.equals("failure.report_create")) {
			   /* //TODO FUTURE
				every measure should be resubmitted
				int mk_submit_report(const char *report_as_json);
				"value": {"failure": "<failure_string>"}
				*/
				} else if (event.key.equals("status.measurement_submission")) {
					setUploaded(true, event.value);
				} else if (event.key.equals("failure.measurement_submission")) {
					setUploaded(false, event.value);
				} else if (event.key.equals("failure.measurement")) {
					//TODO idx missing https://github.com/measurement-kit/measurement-kit/issues/1657
					//setFailed(false, value);
				} else if (event.key.equals("status.measurement_done")) {
					setDone(event.value);
				} else if (event.key.equals("status.end")) {
					setDataUsage(event.value, result);
				} else if (event.key.equals("failure.startup")) {
					//TODO What to do? Run next test
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@CallSuper void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		if (json.test_start_time != null)
			measurement.result.start_time = json.test_start_time;
		if (json.measurement_start_time != null)
			measurement.start_time = json.measurement_start_time;
		if (json.test_runtime != null) {
			measurement.runtime = json.test_runtime;
			measurement.result.addDuration(json.test_runtime);
		}
		measurement.setTestKeys(json.test_keys);
	}

	private void saveNetworkInfo(EventResult.Value value, Result result, Context c) {
		if (result != null && result.network == null) {
			result.network = Network.checkExistingNetwork(value.probe_network_name, value.probe_ip, value.probe_asn, value.probe_cc, ConnectionState.getInstance(c).getNetworkType());
			result.save();
		}
	}

	private void setUploaded(Boolean uploaded, EventResult.Value value) {
		Measurement measurement = measurements.get(value.idx);
		if (measurement != null) {
			measurement.is_uploaded = uploaded;
			String reason = value.reason;
			if (reason != null)
				measurement.upload_failure_msg = reason;
			measurement.save();
		}
	}

	private void setFailed(Boolean failed, EventResult.Value value) {
		Measurement measurement = measurements.get(value.idx);
		if (measurement != null) {
			measurement.is_failed = failed;
			String reason = value.reason;
			if (reason != null)
				measurement.failure_msg = reason;
			measurement.save();
		}
	}

	private void setDone(EventResult.Value value) {
		Measurement measurement = measurements.get(value.idx);
		if (measurement != null) {
			measurement.is_done = true;
			measurement.save();
		}
	}

	private void setDataUsage(EventResult.Value value, Result result) {
		result.data_usage_down = result.data_usage_down + Double.valueOf(value.downloaded_kb).longValue();
		result.data_usage_up = result.data_usage_up + Double.valueOf(value.uploaded_kb).longValue();
		result.save();
	}

	public int getLabelResId() {
		return labelResId;
	}

	public int getIconResId() {
		return iconResId;
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}
}
