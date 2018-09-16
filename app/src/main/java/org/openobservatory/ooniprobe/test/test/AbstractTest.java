package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;
import org.openobservatory.ooniprobe.utils.ConnectionState;

import io.ooni.mk.Task;
import io.ooni.mk.Event;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.concurrent.Executors;

public abstract class AbstractTest {
	private final String TAG = "AbstractTest";

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
		// Use a thread from the factory to run the test. For such a small
		// application AsyncTask would also be okay, most likely.
		Thread thread = Executors.defaultThreadFactory().newThread(
				new Runnable() {
					@Override
					public void run() {
						// Start the nettest and extract events from its queue.
						Task task = Task.startNettest(gson.toJson(settings));
						while (!task.isDone()) {
							Event evp = task.waitForNextEvent();
							if (evp == null) {
								Log.w(TAG, "Cannot wait for next event");
								break;
							}
							final String serialization = evp.serialize();
							if (serialization == null) {
								Log.w(TAG, "Cannot serialize event");
								break;
							}
							JSONObject event;
							try {
								event = new JSONObject(serialization);
							} catch (JSONException exc) {
								Log.w(TAG, "Cannot marshal event: " + exc.toString());
								break;
							}
							Log.i(TAG, "Got event: " + serialization);
							// Now that we've got the JSON event, process it.
							String key = event.optString("key");
							JSONObject value = event.optJSONObject("value");
							if (key == null || value == null) {
								break;
							}
							if (key.equals("status.started")){
								//TODO serve?
							}
							else if (key.equals("status.measurement_start")){
								Double idx = value.optDouble("key");
								String input = event.optString("key");
								if (idx == null || input == null) {
									break;
								}
								Measurement measurement = new Measurement(result, name, reportId);
								if (input.length() > 0)
									measurement.url = Url.getUrl(input);
								measurement.save();
								measurements.put(idx, measurement);
							}
							else if (key.equals("status.geoip_lookup")){
								saveNetworkInfo(value, result, c);
							}
							else if (key.equals("log")) {
								final String message = value.optString("message");
								//TODO-ALE write log line on disk file + "\n"
								//new File(c.getFilesDir(), Result.getLogFileName(result.id, name)).getPath();
								//TODO-ALE Callback log on screen
								testCallback.onLog(message);
								/*
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										logText.append(message + "\n");
									}
								});
								*/
							} else if (key.equals("status.progress")) {
								final double percentage = value.optDouble("percentage", 0.0);
								final String message = value.optString("message");
								//TODO-ALE Callback progress
								testCallback.onProgress(Double.valueOf((index + percentage) * 100).intValue());
								/*
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										logText.append(percentage * 100.0 + "% " + message + "\n");
									}
								});
								*/
							}
							else if (key.compareTo("measurement") == 0) {
								final String json_str = value.optString("json_str");
								Log.d("entry", json_str);
								Double idx = value.optDouble("idx");
								//TODO-ALE idx non dovrebbe mai essere null, ma c'è da gestire questo caso
								if (idx == null) {
									return;
								}
								Measurement measurement = measurements.get(idx);
								if (measurement != null) {
									JsonResult jr = gson.fromJson(json_str, JsonResult.class);
									if (jr == null)
										measurement.is_failed = true;
									else
										onEntry(c, pm, jr, measurement);
									measurement.save();
									try {
										FileOutputStream outputStream = c.openFileOutput(Measurement.getEntryFileName(measurement.id, measurement.test_name), Context.MODE_PRIVATE);
										outputStream.write(json_str.getBytes());
										outputStream.close();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							else if (key.equals("status.report_create")){
								//The report id is created before running measurements
								reportId = value.optString("report_id");
							}
							else if (key.equals("failure.report_create")){
                               /* //TODO FUTURE
                                every measure should be resubmitted
                                int mk_submit_report(const char *report_as_json);
                                "value": {"failure": "<failure_string>"}
                                */
							}
							else if (key.equals("status.measurement_submission")){
								setUploaded(true, value);
							}
							else if (key.equals("failure.measurement_submission")){
								setUploaded(false, value);
							}
							else if (key.equals("failure.measurement")){
								//TODO idx missing https://github.com/measurement-kit/measurement-kit/issues/1657
								//setFailed(false, value);
							}
							else if (key.equals("status.measurement_done")){
								setDone(value);
							}
							else if (key.equals("status.end")){
								setDataUsage(value, result);
							}
							else if (key.equals("failure.startup")){
								//TODO What to do? Run next test
							}
							else {
								Log.i(TAG, "Unhandled event: " + serialization);
							}
						}
						//TODO-ALE test finished, run next or close screen
					}
				}
		);

		if (thread == null) {
			//TODO-ALE some error
			Log.w(TAG, "Cannot create background thread");
			return;
		}
		thread.setDaemon(true);
		thread.start();

		//TODO-ALE OLD CODE queste vanno chiamete in qualche modo o non servono più?
		//testCallback.onStart(c.getString(labelResId));
		//testCallback.onProgress(Double.valueOf(index * 100).intValue());
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

	private void saveNetworkInfo(JSONObject value, Result result, Context c){
		String probe_ip = value.optString("probe_ip");
		String probe_asn = value.optString("probe_asn");
		String probe_cc = value.optString("probe_cc");
		String probe_network_name = value.optString("probe_network_name");

		if (result != null && result.network == null) {
			result.network = Network.checkExistingNetwork(probe_network_name, probe_ip, probe_asn, probe_cc, ConnectionState.getInstance(c).getNetworkType());
			result.save();
		}
	}

	private void setUploaded(Boolean uploaded, JSONObject value) {
		Double idx = value.optDouble("idx");
		//TODO-ALE idx non dovrebbe mai essere null, ma c'è da gestire questo caso
		if (idx == null) {
			return;
		}
		Measurement measurement = measurements.get(idx);
		if (measurement != null){
			measurement.is_uploaded = uploaded;
			String reason = value.optString("reason");
			if (reason != null)
				measurement.upload_failure_msg = reason;
			measurement.save();
		}
	}

	private void setFailed(Boolean failed, JSONObject value) {
		Double idx = value.optDouble("idx");
		//TODO-ALE idx non dovrebbe mai essere null, ma c'è da gestire questo caso
		if (idx == null) {
			return;
		}
		Measurement measurement = measurements.get(idx);
		if (measurement != null){
			measurement.is_failed = failed;
			String reason = value.optString("reason");
			if (reason != null)
				measurement.failure_msg = reason;
			measurement.save();
		}
	}

	private void setDone(JSONObject value) {
		//TODO-ALE idx non dovrebbe mai essere null, ma c'è da gestire questo caso
		Double idx = value.optDouble("idx");
		if (idx == null) {
			return;
		}
		Measurement measurement = measurements.get(idx);
		if (measurement != null){
			measurement.is_done = true;
			measurement.save();
		}
	}

	private void setDataUsage(JSONObject value, Result result) {
		Double down = value.optDouble("downloaded_kb");
		Double up = value.optDouble("uploaded_kb");
		//TODO-ALE che succede qui nel caso siano null?
		//TODO-ALE vanno bene i tipi?
		result.data_usage_down = result.data_usage_down+down.longValue();
		result.data_usage_up = result.data_usage_up+down.longValue();
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
