package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

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
import java.io.IOException;
import java.util.HashMap;

import io.ooni.mk.Task;

public abstract class AbstractTest {
	public static final String UNUSED_KEY = "UNUSED_KEY";
	private final String TAG = "MK_EVENT";
	private String name;
	private String mkName;
	private int labelResId;
	private int iconResId;
	private SparseArray<Measurement> measurements;
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
		measurements = new SparseArray<>();
		Task task = Task.startNettest(gson.toJson(settings));
		FileOutputStream logFOS = null;
		while (!task.isDone())
			try {
				String json = task.waitForNextEvent().serialize();
				Log.d(TAG, json);
				EventResult event = gson.fromJson(json, EventResult.class);
				switch (event.key) {
					case "status.started":
						testCallback.onStart(c.getString(labelResId));
						testCallback.onProgress(Double.valueOf(index * 100).intValue());
						break;
					case "status.geoip_lookup":
						saveNetworkInfo(event.value, result, c);
						break;
					case "status.report_create":
						reportId = event.value.report_id;
						break;
					case "status.measurement_start":
						Measurement measurement = new Measurement(result, name, reportId);
						if (event.value.input.length() > 0)
							measurement.url = Url.getUrl(event.value.input);
						measurements.put(event.value.idx, measurement);
						measurement.save();
						break;
					case "log":
						if (logFOS == null)
							logFOS = c.openFileOutput(Measurement.getLogFileName(result.id, name), Context.MODE_APPEND);
						logFOS.write(event.value.message.getBytes());
						logFOS.write('\n');
						testCallback.onLog(event.value.message);
						break;
					case "status.progress":
						testCallback.onProgress(Double.valueOf((index + event.value.percentage) * 100).intValue());
						break;
					case "measurement":
						Measurement m = measurements.get(event.value.idx);
						if (m != null) {
							JsonResult jr = gson.fromJson(event.value.json_str, JsonResult.class);
							if (jr == null)
								m.is_failed = true;
							else
								onEntry(c, pm, jr, m);
							m.save();
							try {
								FileOutputStream outputStream = c.openFileOutput(Measurement.getEntryFileName(m.id, m.test_name), Context.MODE_PRIVATE);
								outputStream.write(event.value.json_str.getBytes());
								outputStream.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case "failure.report_create":
					   /* //TODO FUTURE
						every measure should be resubmitted
						int mk_submit_report(const char *report_as_json);
						"value": {"failure": "<failure_string>"}
						*/
						break;
					case "status.measurement_submission":
						setUploaded(true, event.value);
						break;
					case "failure.measurement_submission":
						setUploaded(false, event.value);
						break;
					case "failure.measurement":
						//TODO idx missing https://github.com/measurement-kit/measurement-kit/issues/1657
						//setFailed(false, event.value);
						break;
					case "status.measurement_done":
						setDone(event.value);
						break;
					case "status.end":
						setDataUsage(event.value, result);
						break;
					case "failure.startup":
						//TODO What to do? Run next test
						break;
					default:
						Log.w(UNUSED_KEY, event.key);
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (logFOS != null)
			try {
				logFOS.close();
			} catch (IOException e) {
				e.printStackTrace();
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
			String failure = value.failure;
			if (failure != null)
				measurement.upload_failure_msg = failure;
			measurement.save();
		}
	}

	private void setFailed(Boolean failed, EventResult.Value value) {
		Measurement measurement = measurements.get(value.idx);
		if (measurement != null) {
			measurement.is_failed = failed;
			String failure = value.failure;
			if (failure != null)
				measurement.failure_msg = failure;
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
