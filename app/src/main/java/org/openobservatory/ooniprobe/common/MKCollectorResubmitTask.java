package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.dao.MeasurementDao;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private static void perform(Context c, Measurement m) throws IOException {
		FileInputStream is = new FileInputStream(Measurement.getEntryFile(c, m.id, m.test_name));
		String input = new GsonBuilder().disableHtmlEscaping().create().toJson(new JsonParser().parse(new InputStreamReader(is)));
		is.close();
		MKCollectorResubmitSettings settings = new MKCollectorResubmitSettings();
		settings.setTimeout(14);
		settings.setCABundlePath(c.getCacheDir() + "/" + Application.CA_BUNDLE);
		settings.setSerializedMeasurement(input);
		MKCollectorResubmitResults results = settings.perform();
		if (results.isGood()) {
			Log.i(io.ooni.mk.MKCollectorResubmitSettings.class.getSimpleName(), results.getLogs());
			String output = results.getUpdatedSerializedMeasurement();
			FileOutputStream os = new FileOutputStream(Measurement.getEntryFile(c, m.id, m.test_name));
			os.write(output.getBytes());
			os.close();
			m.report_id = results.getUpdatedReportID();
			m.is_uploaded = true;
			m.is_upload_failed = false;
			m.save();
		}
	}

	@Override protected void onPreExecute() {
		super.onPreExecute();
		if (getActivity() != null)
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override protected Void doInBackground(Integer... params) {
		if (params.length != 2)
			throw new IllegalArgumentException("MKCollectorResubmitTask require 2 nullable params: result_id, measurement_id");
		List<Measurement> measurements = MeasurementDao.queryList(params[0], params[1], false, false);
		for (int i = 0; i < measurements.size() && getActivity() != null; i++) {
			publishProgress(getActivity().getString(R.string.Modal_ResultsNotUploaded_Uploading, getActivity().getString(R.string.paramOfParam, Integer.toString(i + 1), Integer.toString(measurements.size()))));
			Measurement m = measurements.get(i);
			m.result.load();
			try {
				perform(getActivity(), m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (getActivity() != null)
			getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
