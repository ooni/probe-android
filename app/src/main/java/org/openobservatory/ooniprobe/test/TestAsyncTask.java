package org.openobservatory.ooniprobe.test;

import android.os.AsyncTask;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class TestAsyncTask<ACT extends AbstractActivity> extends AsyncTask<AbstractTest, String, Void> implements AbstractTest.TestCallback {
	public static final List<AbstractSuite> SUITES = Arrays.asList(new InstantMessagingSuite(), new MiddleBoxesSuite(), new MiddleBoxesSuite(), new PerformanceSuite(), new WebsitesSuite());
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";
	protected final WeakReference<ACT> ref;
	private final Result result;

	protected TestAsyncTask(ACT activity, Result result) {
		this.ref = new WeakReference<>(activity);
		this.result = result;
		result.is_viewed = false;
		result.save();
	}

	@Override protected Void doInBackground(AbstractTest... tests) {
		for (int i = 0; i < tests.length; i++) {
			ACT act = ref.get();
			if (act != null && !act.isFinishing())
				tests[i].run(act, act.getPreferenceManager(), act.getGson(), result, i, this);
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
