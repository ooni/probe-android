package org.openobservatory.ooniprobe.common;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.ooni.mk.MKCollectorResubmitResults;
import localhost.toolkit.os.NetworkProgressAsyncTask;

/**
 * new MKCollectorResubmitSettings(activity).execute(result_id, measurement_id);
 */
public class MKCollectorResubmitSettings extends NetworkProgressAsyncTask<Integer, Void> {
	public MKCollectorResubmitSettings(AppCompatActivity activity) {
		super(activity, true, false);
	}

	@Override protected Void doInBackground(Integer... params) {
		ArrayList<SQLOperator> where = new ArrayList<>();
		where.add(Measurement_Table.is_uploaded.eq(false));
		where.add(Measurement_Table.is_failed.eq(false));
		if (params.length != 2)
			throw new IllegalArgumentException("MKCollectorResubmitSettings require 2 nullable params: result_id, measurement_id");
		if (params[0] != null)
			where.add(Measurement_Table.result_id.eq(params[0]));
		if (params[1] != null)
			where.add(Measurement_Table.id.eq(params[1]));
		List<Measurement> measurements = SQLite.select().from(Measurement.class).where(where.toArray(new SQLOperator[0])).queryList();
		for (int i = 0; i < measurements.size(); i++) {
			Measurement m = measurements.get(i);
			try {
				publishProgress("uploading " + (i + 1) + " of " + measurements.size());
				FileInputStream is = new FileInputStream(Measurement.getEntryFile(activity, m.id, m.test_name));
				String input = new GsonBuilder().disableHtmlEscaping().create().toJson(new JsonParser().parse(new InputStreamReader(is)));
				is.close();
				io.ooni.mk.MKCollectorResubmitSettings settings = new io.ooni.mk.MKCollectorResubmitSettings();
				settings.setTimeout(14);
				settings.setCABundlePath(activity.getCacheDir() + "/" + Application.CA_BUNDLE);
				settings.setSerializedMeasurement(input);
				MKCollectorResubmitResults results = settings.perform();
				if (results.isGood()) {
					Log.i(io.ooni.mk.MKCollectorResubmitSettings.class.getSimpleName(), results.getLogs());
					String output = results.getUpdatedSerializedMeasurement();
					FileOutputStream os = new FileOutputStream(Measurement.getEntryFile(activity, m.id, m.test_name));
					os.write(output.getBytes());
					os.close();
					m.is_uploaded = true;
					m.is_upload_failed = false;
					m.save();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
