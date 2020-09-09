package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.raizlabs.android.dbflow.sql.language.Where;

import org.apache.commons.io.FileUtils;
import org.openobservatory.engine.OONICollectorResults;
import org.openobservatory.engine.OONICollectorTask;
import org.openobservatory.engine.Engine;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import localhost.toolkit.os.NetworkProgressAsyncTask;

public class ResubmitTask<A extends AppCompatActivity> extends NetworkProgressAsyncTask<A, Integer, Boolean> {
    private OONICollectorTask task;
    protected Integer totUploads;
    protected Integer errors;
    protected String logs;

    /**
     * Use this class to resubmit a measurement, use result_id and measurement_id to filter list of value
     * {@code new MKCollectorResubmitTask(activity).execute(@Nullable result_id, @Nullable measurement_id);}
     *
     * @param activity from which this task are executed
     */
    public ResubmitTask(A activity) {
        super(activity, true, false);
        task = Engine.newCollectorTask(
                BuildConfig.SOFTWARE_NAME,
                BuildConfig.VERSION_NAME,
                Engine.getCABundlePath(activity)
        );
    }

    private boolean perform(Context c, Measurement m) throws IOException {
        File file = Measurement.getEntryFile(c, m.id, m.test_name);
        String input = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        boolean okay = Engine.maybeUpdateResources(c);
        if (!okay) {
            ExceptionManager.logException(new Exception("MKResourcesManager didn't find resources"));
            return false;
        }
        long uploadTimeout = getTimeout(file.length());
        OONICollectorResults results = task.maybeDiscoverAndSubmit(input, uploadTimeout);
        if (results.isGood()) {
            String output = results.getUpdatedSerializedMeasurement();
            FileUtils.writeStringToFile(file, output, Charset.forName("UTF-8"));
            m.report_id = results.getUpdatedReportID();
            m.is_uploaded = true;
            m.is_upload_failed = false;
            m.save();
        } else {
            Log.w(OONICollectorTask.class.getSimpleName(), results.getLogs());
        }
        System.out.println("getReason "+ results.getReason());
        System.out.println("getLogs "+ results.getLogs());
        if (!results.isGood())
            logs += results.getReason() + "\n";
        return results.isGood();
    }

    public static long getTimeout(long length) {
        return length / 2000 + 10;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        A activity = getActivity();
        if (activity != null)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * this method is invoked when the {@code execute()} method is called
     *
     * @param params [0] is result_id. is nullable and is used to restrict measurement retrieve on a specific result.
     *               [1] is measurement_id. is nullable and is used to restrict measurement retrieve on a specific measurement.
     * @return true if success, false otherwise
     */
    @Override
    protected Boolean doInBackground(Integer... params) {
        logs = "";
        errors = 0;
        if (params.length != 2)
            throw new IllegalArgumentException("MKCollectorResubmitTask requires 2 nullable params: result_id, measurement_id");
        Where<Measurement> msmQuery = Measurement.selectUploadable();
        if (params[0] != null) {
            msmQuery.and(Measurement_Table.result_id.eq(params[0]));
        }
        if (params[1] != null) {
            msmQuery.and(Measurement_Table.id.eq(params[1]));
        }
        List<Measurement> measurements = msmQuery.queryList();
        totUploads = measurements.size();
        for (int i = 0; i < measurements.size(); i++) {
            A activity = getActivity();
            if (activity == null)
                break;
            String paramOfParam = activity.getString(R.string.paramOfParam, Integer.toString(i + 1), Integer.toString(measurements.size()));
            publishProgress(activity.getString(R.string.Modal_ResultsNotUploaded_Uploading, paramOfParam));
            Measurement m = measurements.get(i);
            m.result.load();
            try {
                if (!perform(activity, m)){
                    errors++;
                }
            } catch (IOException e) {
                errors++;
                e.printStackTrace();
            }
        }
        return errors == 0;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        A activity = getActivity();
        if (activity != null && result) {
            Toast.makeText(activity, activity.getString(R.string.Toast_ResultsUploaded), Toast.LENGTH_SHORT).show();
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
