package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import io.ooni.mk.MKTask;

public abstract class AbstractTest implements Serializable {
	public static final String UNUSED_KEY = "UNUSED_KEY";
	private final String TAG = "MK_EVENT";
	private String name;
	private String mkName;
	private List<String> inputs;
	private Integer max_runtime;
	private int labelResId;
	private int iconResId;
	private SparseArray<Measurement> measurements;
	private String reportId;
	private int runtime;

	public AbstractTest(String name, String mkName, int labelResId, int iconResId, int runtime) {
		this.name = name;
		this.mkName = mkName;
		this.labelResId = labelResId;
		this.iconResId = iconResId;
		this.runtime = runtime;
	}

	public abstract void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback);

	protected void run(Context c, PreferenceManager pm, Gson gson, Settings settings, Result result, int index, TestCallback testCallback) {
		settings.name = mkName;
		settings.inputs = inputs;
		settings.options.max_runtime = max_runtime;
		measurements = new SparseArray<>();
		MKTask task = MKTask.startNettest(gson.toJson(settings));
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
						saveNetworkInfo(event.value, result, pm);
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
							logFOS = new FileOutputStream(Measurement.getLogFile(c, result.id, name));
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
								FileOutputStream outputStream = new FileOutputStream(Measurement.getEntryFile(c, m.id, m.test_name));
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
					case "status.measurement_done":
						setDone(event.value);
						break;
					case "status.end":
						setDataUsage(event.value, result);
						break;
					case "failure.startup":
						//Run next test
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

	private void saveNetworkInfo(EventResult.Value value, Result result, PreferenceManager pm) {
		if (result != null && result.network == null) {
			result.network = Network.checkExistingNetwork(value.probe_network_name, value.probe_ip, value.probe_asn, value.probe_cc, pm.getNetworkType());
			result.save();
		}
	}

	private void setUploaded(Boolean uploaded, EventResult.Value value) {
		Measurement measurement = measurements.get(value.idx);
		if (measurement != null) {
			measurement.is_uploaded = uploaded;
			if (!uploaded){
				measurement.report_id = "";
				measurement.is_upload_failed = true;
			}
			String failure = value.failure;
			if (failure != null)
				measurement.upload_failure_msg = failure;
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

	public String getName() {
		return name;
	}

	public List<String> getInputs() {
		return inputs;
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public Integer getMax_runtime() {
		return max_runtime;
	}

	public void setMax_runtime(Integer max_runtime) {
		this.max_runtime = max_runtime;
	}

	public int getRuntime(PreferenceManager pm) {
		return runtime;
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}
}
