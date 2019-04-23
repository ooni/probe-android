package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.ooni.mk.MKCollectorResubmitResults;
import io.ooni.mk.MKCollectorResubmitSettings;
import localhost.toolkit.os.NetworkProgressAsyncTask;

public class MKCollectorResubmitTask<A extends AppCompatActivity> extends NetworkProgressAsyncTask<A, Integer, Void> {
	/**
	 * {@code new MKCollectorResubmitTask(activity).execute(result_id, measurement_id);}
	 *
	 * @param activity from which this task are executed
	 */
	public MKCollectorResubmitTask(A activity) {
		super(activity, true, false);
	}

	private static void perform(Context c, Measurement m) throws IOException {
		String input = FileUtils.readFileToString(Measurement.getEntryFile(c, m.id, m.test_name), StandardCharsets.UTF_8);
		MKCollectorResubmitSettings settings = new MKCollectorResubmitSettings();
		settings.setTimeout(14);
		settings.setCABundlePath(c.getCacheDir() + "/" + Application.CA_BUNDLE);
		settings.setSerializedMeasurement(input);
		MKCollectorResubmitResults results = settings.perform();
		if (results.isGood()) {
			String output = results.getUpdatedSerializedMeasurement();
			FileUtils.writeStringToFile(Measurement.getEntryFile(c, m.id, m.test_name), output, StandardCharsets.UTF_8);
			m.report_id = results.getUpdatedReportID();
			m.is_uploaded = true;
			m.is_upload_failed = false;
			m.save();
		} else {
			Log.w(MKCollectorResubmitSettings.class.getSimpleName(), results.getLogs());
			// TODO decide what to do with logs (append on log file?)
		}
	}

	@Override protected void onPreExecute() {
		super.onPreExecute();
		A activity = getActivity();
		if (activity != null)
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override protected Void doInBackground(Integer... params) {
		if (params.length != 2)
			throw new IllegalArgumentException("MKCollectorResubmitTask requires 2 nullable params: result_id, measurement_id");
		ArrayList<SQLOperator> where = new ArrayList<>();
		if (params[0] != null)
			where.add(Measurement_Table.result_id.eq(params[0]));
		if (params[1] != null)
			where.add(Measurement_Table.id.eq(params[1]));
		where.add(OperatorGroup.clause().or(Measurement_Table.is_uploaded.eq(false)).or(Measurement_Table.report_id.isNull()));
		where.add(Measurement_Table.is_failed.eq(false));
		List<Measurement> measurements = SQLite.select().from(Measurement.class).where(where.toArray(new SQLOperator[0])).queryList();
		for (int i = 0; i < measurements.size(); i++) {
			A activity = getActivity();
			if (activity != null) {
				publishProgress(activity.getString(R.string.Modal_ResultsNotUploaded_Uploading, activity.getString(R.string.paramOfParam, Integer.toString(i + 1), Integer.toString(measurements.size()))));
				Measurement m = measurements.get(i);
				m.result.load();
				try {
					perform(activity, m);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		A activity = getActivity();
		if (activity != null)
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
