package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.ooni.mk.MKCollectorResubmitResults;
import io.ooni.mk.MKCollectorResubmitSettings;
import localhost.toolkit.os.NetworkProgressAsyncTask;

public class MKCollectorResubmitTask<A extends AppCompatActivity> extends NetworkProgressAsyncTask<A, Integer, Void> {
    /**
     * Use this class to resubmit a measurement, use result_id and measurement_id to filter list of value
     * {@code new MKCollectorResubmitTask(activity).execute(@Nullable result_id, @Nullable measurement_id);}
     *
     * @param activity from which this task are executed
     */
    public MKCollectorResubmitTask(A activity) {
        super(activity, true, false);
    }

    private static void perform(Context c, Measurement m) throws IOException {
        File file = Measurement.getEntryFile(c, m.id, m.test_name);
        String input = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        MKCollectorResubmitSettings settings = new MKCollectorResubmitSettings();
        settings.setTimeout(getTimeout(file.length()));
        settings.setCABundlePath(c.getCacheDir() + "/" + Application.CA_BUNDLE);
        settings.setSerializedMeasurement(input);
        settings.setSoftwareName(c.getString(R.string.software_name));
        settings.setSoftwareVersion(BuildConfig.VERSION_NAME);
        MKCollectorResubmitResults results = settings.perform();
        if (results.isGood()) {
            String output = results.getUpdatedSerializedMeasurement();
            FileUtils.writeStringToFile(file, output, StandardCharsets.UTF_8);
            m.report_id = results.getUpdatedReportID();
            m.is_uploaded = true;
            m.is_upload_failed = false;
            m.save();
        } else {
            Log.w(MKCollectorResubmitSettings.class.getSimpleName(), results.getLogs());
            // TODO decide what to do with logs (append on log file?)
        }
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
     * @return there is no return
     */
    @Override
    protected Void doInBackground(Integer... params) {
        if (params.length != 2)
            throw new IllegalArgumentException("MKCollectorResubmitTask requires 2 nullable params: result_id, measurement_id");
        Where<Measurement> msmQuery = SQLite.select().from(Measurement.class)
                .where(Measurement_Table.is_failed.eq(false))
                // We check on both the report_id and is_uploaded as we
                // may have some unuploaded measurements which are marked
                // as is_uploaded = true, but we always know that those with
                // report_id set to null are not uploaded
                .and(OperatorGroup.clause()
                        .or(Measurement_Table.is_uploaded.eq(false))
                        .or(Measurement_Table.report_id.isNull()));
        if (params[0] != null) {
            msmQuery.and(Measurement_Table.result_id.eq(params[0]));
        }
        if (params[1] != null) {
            msmQuery.and(Measurement_Table.id.eq(params[1]));
        }
        List<Measurement> measurements = msmQuery.queryList();
        for (int i = 0; i < measurements.size(); i++) {
            A activity = getActivity();
            if (activity == null)
                break;
            String paramOfParam = activity.getString(R.string.paramOfParam, Integer.toString(i + 1), Integer.toString(measurements.size()));
            publishProgress(activity.getString(R.string.Modal_ResultsNotUploaded_Uploading, paramOfParam));
            Measurement m = measurements.get(i);
            m.result.load();
            try {
                perform(activity, m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        A activity = getActivity();
        if (activity != null)
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
