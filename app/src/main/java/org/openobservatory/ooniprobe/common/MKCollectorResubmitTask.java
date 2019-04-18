package org.openobservatory.ooniprobe.common;

import android.util.Log;
import android.view.WindowManager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
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
	 * @param activity from this task are executed
	 */
	public MKCollectorResubmitTask(A activity) {
		super(activity, true, false);
	}

	@Override protected void onPreExecute() {
		super.onPreExecute();
		if (getActivity() != null)
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override protected Void doInBackground(Integer... params) {
		ArrayList<SQLOperator> where = new ArrayList<>();
		where.add(Measurement_Table.is_uploaded.eq(false));
		where.add(OperatorGroup.clause().or(Measurement_Table.is_failed.eq(false)).or(Measurement_Table.report_id.isNull()));
		if (params.length != 2)
			throw new IllegalArgumentException("MKCollectorResubmitTask require 2 nullable params: result_id, measurement_id");
		if (params[0] != null)
			where.add(Measurement_Table.result_id.eq(params[0]));
		if (params[1] != null)
			where.add(Measurement_Table.id.eq(params[1]));
		List<Measurement> measurements = SQLite.select().from(Measurement.class).where(where.toArray(new SQLOperator[0])).queryList();
		for (int i = 0; i < measurements.size() && getActivity() != null; i++)
			try {
				Measurement m = measurements.get(i);
				m.result.load();
				publishProgress(getActivity().getString(R.string.Modal_ResultsNotUploaded_Uploading, getActivity().getString(R.string.paramOfParam, Integer.toString(i + 1), Integer.toString(measurements.size()))));
				FileInputStream is = new FileInputStream(Measurement.getEntryFile(getActivity(), m.id, m.test_name));
				String input = new GsonBuilder().disableHtmlEscaping().create().toJson(new JsonParser().parse(new InputStreamReader(is)));
				is.close();
				MKCollectorResubmitSettings settings = new MKCollectorResubmitSettings();
				settings.setTimeout(14);
				settings.setCABundlePath(getActivity().getCacheDir() + "/" + Application.CA_BUNDLE);
				settings.setSerializedMeasurement(input);
				MKCollectorResubmitResults results = settings.perform();
				if (results.isGood()) {
					Log.i(io.ooni.mk.MKCollectorResubmitSettings.class.getSimpleName(), results.getLogs());
					String output = results.getUpdatedSerializedMeasurement();
					FileOutputStream os = new FileOutputStream(Measurement.getEntryFile(getActivity(), m.id, m.test_name));
					os.write(output.getBytes());
					os.close();
					m.report_id = results.getUpdatedReportID();
					m.is_uploaded = true;
					m.is_upload_failed = false;
					m.save();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (getActivity() != null)
			getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
