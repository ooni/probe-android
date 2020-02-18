package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.common.ExceptionManager;
import org.openobservatory.ooniprobe.common.MKException;
import org.openobservatory.ooniprobe.common.OrchestraTask;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import io.ooni.mk.MKAsyncTask;
import io.ooni.mk.MKResourcesManager;

public abstract class AbstractTest implements Serializable {
    private static final String UNUSED_KEY = "UNUSED_KEY";
    private static final String TAG = "MK_EVENT";
    private final String name;
    private final String mkName;
    private final int labelResId;
    private final int iconResId;
    private final int urlResId;
    private final int runtime;
    private List<String> inputs;
    private Integer max_runtime;
    private SparseArray<Measurement> measurements;
    private String reportId;
    private String origin;

    AbstractTest(String name, String mkName, @StringRes int labelResId, @DrawableRes int iconResId, @StringRes int urlResId, int runtime) {
        this.name = name;
        this.mkName = mkName;
        this.labelResId = labelResId;
        this.iconResId = iconResId;
        this.urlResId = urlResId;
        this.runtime = runtime;
    }

    public abstract void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback);

    void run(Context c, PreferenceManager pm, Gson gson, Settings settings, Result result, int index, TestCallback testCallback) {
        //Checking for resources before running any test
        try {
            boolean okay = MKResourcesManager.maybeUpdateResources(c);
            if (!okay) {
                throw new Exception("MKResourcesManager didn't find resources");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionManager.logException(e);
            return;
        }
        settings.name = mkName;
        settings.inputs = inputs;
        settings.options.max_runtime = max_runtime;
        settings.annotations.origin = origin;
        measurements = new SparseArray<>();
        MKAsyncTask task = MKAsyncTask.start(gson.toJson(settings));
        while (!task.isDone())
            try {
                String json = task.waitForNextEvent();
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
                        File logFile = Measurement.getLogFile(c, result.id, name);
                        logFile.getParentFile().mkdirs();
                        FileUtils.writeStringToFile(
                                logFile,
                                event.value.message + "\n",
                                Charset.forName("UTF-8"),
                                /*append*/true
                        );
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
                            File entryFile = Measurement.getEntryFile(c, m.id, m.test_name);
                            entryFile.getParentFile().mkdirs();
                            FileUtils.writeStringToFile(
                                    entryFile,
                                    event.value.json_str,
                                    Charset.forName("UTF-8")
                            );
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
                        setFailureMsg(event.value, result);
                        break;
                    case "bug.json_dump":
                        ExceptionManager.logException(new MKException(event));
                        break;
                    default:
                        Log.w(UNUSED_KEY, event.key);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ExceptionManager.logException(e);
            }
    }

    @CallSuper
    void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
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
            result.network = Network.getNetwork(value.probe_network_name, value.probe_ip, value.probe_asn, value.probe_cc, ReachabilityManager.getNetworkType(c));
            result.save();
        }
    }

    private void setUploaded(Boolean uploaded, EventResult.Value value) {
        Measurement measurement = measurements.get(value.idx);
        if (measurement != null) {
            measurement.is_uploaded = uploaded;
            if (!uploaded) {
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

    private void setFailureMsg(EventResult.Value value, Result result) {
        if (result != null) {
            result.failure_msg = value.failure;
            result.save();
        }
    }

    public int getLabelResId() {
        return labelResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getUrlResId() {
        return urlResId;
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

    Integer getMax_runtime() {
        return max_runtime;
    }

    public void setMax_runtime(Integer max_runtime) {
        this.max_runtime = max_runtime;
    }

    public int getRuntime(PreferenceManager pm) {
        return runtime;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public interface TestCallback {
        void onStart(String name);

        void onProgress(int progress);

        void onLog(String log);
    }
}
