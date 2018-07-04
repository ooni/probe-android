package org.openobservatory.ooniprobe.test2;

import android.os.AsyncTask;

import org.openobservatory.ooniprobe.model.AbstractJsonResult;

import java.util.ArrayList;
import java.util.List;

public class TestSuite<Result extends AbstractJsonResult> extends AsyncTask<Test<Result>, String, List<Result>> implements Test.TestCallback {
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";

	@Override protected List<Result> doInBackground(Test<Result>... tests) {
		List<Result> results = new ArrayList<>();
		for (int i = 0; i < tests.length; i++)
			results.addAll(tests[i].run(i, this));
		return results;
	}

	@Override public void onStart(String name) {
		publishProgress(RUN, name);
	}

	@Override public final void onProgress(int progress) {
		publishProgress(PRG, Integer.toString(progress));
	}

	@Override public final void onLog(String log) {
		publishProgress(LOG, log);
	}

	@Override protected void onPostExecute(List<Result> results) {
		super.onPostExecute(results);
		// TODO manage entry
	}
}
