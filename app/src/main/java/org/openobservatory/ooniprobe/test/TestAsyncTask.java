package org.openobservatory.ooniprobe.test;

import android.os.AsyncTask;

public class TestAsyncTask extends AsyncTask<AbstractTest, String, Void> implements AbstractTest.TestCallback {
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";

	@Override protected Void doInBackground(AbstractTest... tests) {
		for (int i = 0; i < tests.length; i++)
			tests[i].run(i, this);
		return null;
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
}
