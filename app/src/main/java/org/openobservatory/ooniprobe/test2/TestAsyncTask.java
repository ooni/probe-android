package org.openobservatory.ooniprobe.test2;

import android.os.AsyncTask;

import org.openobservatory.ooniprobe.activity.AbstractActivity;

public class TestAsyncTask extends AsyncTask<AbstractTest, String, Void> implements AbstractTest.TestCallback {
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";

	public static AbstractTest[] getIMTestList(AbstractActivity activity) {
		return new AbstractTest[]{
				new Whatsapp(activity),
				new Telegram(activity),
				new FacebookMessenger(activity)
		};
	}

	public static AbstractTest[] getWCTestList(AbstractActivity activity) {
		return new AbstractTest[]{
				new WebConnectivity(activity)
		};
	}

	public static AbstractTest[] getSPTestList(AbstractActivity activity) {
		return new AbstractTest[]{
				new Ndt(activity),
				new Dash(activity)
		};
	}

	public static AbstractTest[] getMBTestList(AbstractActivity activity) {
		return new AbstractTest[]{
				new HttpHeaderFieldManipulation(activity),
				new HttpInvalidRequestLine(activity)
		};
	}

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
