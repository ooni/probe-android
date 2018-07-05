package org.openobservatory.ooniprobe.test2;

import android.os.AsyncTask;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.AbstractJsonResult;

import java.util.ArrayList;
import java.util.List;

public class TestAsyncTask<JR extends AbstractJsonResult> extends AsyncTask<AbstractTest<JR>, String, List<JR>> implements AbstractTest.TestCallback {
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";

	public static AbstractTest.TestJsonResult[] getIMTestList(AbstractActivity activity) {
		return new AbstractTest.TestJsonResult[]{
				new Whatsapp(activity),
				new Telegram(activity),
				new FacebookMessenger(activity)
		};
	}

	public static AbstractTest.TestJsonResult[] getWCTestList(AbstractActivity activity) {
		return new AbstractTest.TestJsonResult[]{
				new WebConnectivity(activity)
		};
	}

	public static AbstractTest.TestJsonResult[] getSPTestList(AbstractActivity activity) {
		return new AbstractTest.TestJsonResult[]{
				new Ndt(activity),
				new Dash(activity)
		};
	}

	public static AbstractTest.TestJsonResultHttp[] getMBTestList(AbstractActivity activity) {
		return new AbstractTest.TestJsonResultHttp[]{
				new HttpHeaderFieldManipulation(activity),
				new HttpInvalidRequestLine(activity)
		};
	}

	@Override protected List<JR> doInBackground(AbstractTest<JR>... tests) {
		List<JR> JRS = new ArrayList<>();
		for (int i = 0; i < tests.length; i++)
			JRS.addAll(tests[i].run(i, this));
		return JRS;
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
