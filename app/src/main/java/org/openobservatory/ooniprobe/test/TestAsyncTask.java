package org.openobservatory.ooniprobe.test;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.openobservatory.engine.Engine;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ExceptionManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.model.api.UrlList;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

public class TestAsyncTask extends AsyncTask<Void, String, Void> implements AbstractTest.TestCallback {
	public static final List<AbstractSuite> SUITES = Arrays.asList(new WebsitesSuite(),
			new InstantMessagingSuite(), new CircumventionSuite(), new PerformanceSuite());
	public static final String START = "START";
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";
	public static final String ERR = "ERR";
	public static final String END = "END";
	public static final String URL = "URL";
	public static final String INT = "INT";
	protected final Application app;
	private Result result;
	ArrayList<AbstractSuite> testSuites;
	public AbstractSuite currentSuite;
	public AbstractTest currentTest;
	private boolean interrupt;
	RunTestService service;

	public TestAsyncTask(Application app, ArrayList<AbstractSuite> testSuites, RunTestService service) {
		this.app = app;
		this.testSuites = testSuites;
		this.service = service;
	}

	@Override
	protected Void doInBackground(Void... voids) {
		if (app != null){
			for (int suiteIdx = 0; suiteIdx < testSuites.size(); suiteIdx++){
				if (!interrupt){
					currentSuite = testSuites.get(suiteIdx);
					result = currentSuite.getResult();
					result.is_viewed = false;
					result.save();
					publishProgress(START, String.valueOf(suiteIdx));
					runTest(currentSuite.getTestList(app.getPreferenceManager()));
				}
			}
		}
		return null;
	}

	private void runTest(AbstractTest ... tests){
		try {
			boolean downloadUrls = false;
			for (AbstractTest abstractTest : tests)
				if (abstractTest instanceof WebConnectivity && abstractTest.getInputs() == null) {
					downloadUrls = true;
					break;
				}
			if (downloadUrls) {
				String probeCC = "XX";
				try {
					probeCC = Engine.resolveProbeCC(
							app,
							BuildConfig.SOFTWARE_NAME,
							BuildConfig.VERSION_NAME,
							30
					);
				}
				catch (Exception e) {
					e.printStackTrace();
					ExceptionManager.logException(e);
				}
				Response<UrlList> response = app.getOrchestraClient().getUrls(probeCC, app.getPreferenceManager().getEnabledCategory()).execute();
				if (response.isSuccessful() && response.body() != null && response.body().results != null) {
					ArrayList<String> inputs = new ArrayList<>();
					for (Url url : response.body().results)
						inputs.add(Url.checkExistingUrl(url.url, url.category_code, url.country_code).url);
					for (AbstractTest abstractTest : tests) {
						abstractTest.setInputs(inputs);
						if (abstractTest.getMax_runtime() == null)
							abstractTest.setMax_runtime(app.getPreferenceManager().getMaxRuntime());
					}
					publishProgress(URL);
				}
			}
			for (int i = 0; i < tests.length; i++) {
				if (!interrupt) {
					currentTest = tests[i];
					currentTest.run(app, app.getPreferenceManager(), app.getGson(), result, i, this);
				}
			}
		} catch (Exception e) {
			publishProgress(ERR, app.getString(R.string.Modal_Error_CantDownloadURLs));
			e.printStackTrace();
			ExceptionManager.logException(e);
		}
	}

	@Override public void onStart(String name) {
		publishProgress(RUN, name);
	}

	@Override public final void onProgress(int progress) {
		if (!isInterrupted())
			publishProgress(PRG, Integer.toString(progress));
	}

	@Override public final void onLog(String log) {
		if (!isInterrupted())
		    publishProgress(LOG, log);
	}

	@Override
	protected void onProgressUpdate(String... values) {
		sendBroadcast(values);
		String key = values[0];
		if (values.length <= 1) return;
		String value = values[1];
		switch (key) {
			case TestAsyncTask.RUN:
				service.builder.setContentText(value)
						.setProgress(currentSuite.getTestList(app.getPreferenceManager()).length * 100, 0,false);
				service.notificationManager.notify(RunTestService.NOTIFICATION_ID, service.builder.build());
				break;
			case TestAsyncTask.PRG:
				int prgs = Integer.parseInt(value);
				service.builder.setProgress(currentSuite.getTestList(app.getPreferenceManager()).length * 100, prgs,false);
				service.notificationManager.notify(RunTestService.NOTIFICATION_ID, service.builder.build());
				break;
			case TestAsyncTask.INT:
				service.builder.setContentText(app.getString(R.string.Dashboard_Running_Stopping_Title))
					.setProgress(0, 0, true);
				service.notificationManager.notify(RunTestService.NOTIFICATION_ID, service.builder.build());
				break;
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		sendBroadcast(END);
		service.stopSelf();
	}

	private void sendBroadcast(String... values){
		Intent broadcastIntent = new Intent();
		broadcastIntent.putExtra("key", values[0]);
		if (values.length > 1)
			broadcastIntent.putExtra("value", values[1]);
		broadcastIntent.setAction("org.openobservatory.ooniprobe.activity.RunningActivity");
		LocalBroadcastManager.getInstance(app).sendBroadcast(broadcastIntent);
	}

	public synchronized boolean isInterrupted() {
		return interrupt;
	}

	public synchronized void interrupt(){
		if(currentTest.canInterrupt()) {
			interrupt = true;
			currentTest.interrupt();
			sendBroadcast(INT);
		}
	}
}
