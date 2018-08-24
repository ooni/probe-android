package org.openobservatory.ooniprobe.test;

import android.os.AsyncTask;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.lang.ref.WeakReference;

public class TestAsyncTask<ACT extends AbstractActivity> extends AsyncTask<AbstractTest, String, Void> implements AbstractTest.TestCallback {
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";
	protected WeakReference<ACT> ref;
	private Result result;

	public TestAsyncTask(ACT activity, Result result) {
		this.ref = new WeakReference<>(activity);
		this.result = result;
		result.save();
	}

	@Override protected Void doInBackground(AbstractTest... tests) {
		for (int i = 0; i < tests.length; i++) {
			ACT act = ref.get();
			if (act != null && !act.isFinishing())
				tests[i].run(ref.get(), result, i, this);
		}
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
