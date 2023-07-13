package org.openobservatory.ooniprobe.test;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.collect.Lists;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONIRunFetchResponse;
import org.openobservatory.engine.OONIRunNettest;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ListUtility;
import org.openobservatory.ooniprobe.common.MKException;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAsyncTask extends AsyncTask<Void, String, Void> implements AbstractTest.TestCallback {


    private static final String TAG = "TestAsyncTask";
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
    public ArrayList<AbstractSuite> testSuites;
    public AbstractSuite currentSuite;
    public AbstractTest currentTest;
    private boolean interrupt;
    private ConnectivityManager manager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private String proxy;
    private boolean store_db = true;

	public static List<AbstractSuite> getSuites(Resources resources) {
		ArrayList<AbstractSuite> testSuites = new ArrayList<>(Arrays.asList(new WebsitesSuite(resources),
			new InstantMessagingSuite(resources), new CircumventionSuite(resources), new PerformanceSuite(resources), new ExperimentalSuite(resources)));

        List<TestDescriptor> testDescriptors = TestDescriptorManager.getAll();
        testSuites.addAll(
               Lists.transform(testDescriptors,descriptor -> {
                   List<AbstractTest> tests = Lists.transform(
                           (List<OONIRunNettest>)descriptor.getNettests(),
                           nettest -> AbstractTest.getTestByName(nettest.getName())
                   );
                   return new OONIRunSuite(descriptor, tests.toArray(new AbstractTest[0]));
               })
       );
		return testSuites;
	}

    public TestAsyncTask(Application app, ArrayList<AbstractSuite> testSuites) {
        this.app = app;
        this.testSuites = testSuites;
        this.proxy = app.getPreferenceManager().getProxyURL();
    }

    public TestAsyncTask(Application app, ArrayList<AbstractSuite> testSuites, boolean store_db) {
        this(app, testSuites);
        this.store_db = store_db;
    }

    private void registerConnChange() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }
        manager = (ConnectivityManager) this.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI"));
                app.getLogger().i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI"));
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                //losing active connection stop test
                Log.i(TAG, "losing active connection");
                app.getLogger().i(TAG, "losing active connection");
                interrupt();
            }
        };
        manager.registerDefaultNetworkCallback(networkCallback);
    }

    private void unregisterConnChange() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }
        manager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (app != null && testSuites != null) {
            registerConnChange();
            for (int suiteIdx = 0; suiteIdx < testSuites.size(); suiteIdx++) {
                if (!interrupt) {
                    currentSuite = testSuites.get(suiteIdx);
                    if (store_db) {
                        result = currentSuite.getResult();
                        result.is_viewed = false;
                        result.save();
                    }

                    AbstractTest[] tests = currentSuite.getTestList(app.getPreferenceManager());
                    if (tests.length > 0){
                        publishProgress(START, String.valueOf(suiteIdx));
                        runTest(currentSuite.getTestList(app.getPreferenceManager()));
                    }
                }
            }
        }
        return null;
    }

    private void runTest(AbstractTest... tests) {
        try {
            for (int i = 0; i < tests.length; i++) {
                currentTest = tests[i];
                if (currentTest instanceof WebConnectivity && currentTest.getInputs() == null) {
                    downloadURLs();
                }
                if (!interrupt) {
                    Log.d(TAG, "run next suite: " + currentSuite.getName() + " test:" + currentTest.getName());

                    currentTest.run(app, app.getPreferenceManager(),app.getLogger(), app.getGson(), result, i, this);
                }
            }
        } catch (Exception e) {
            publishProgress(ERR, app.getString(R.string.Modal_Error_CantDownloadURLs));
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }

    //This uses the wrapper
    private void downloadURLs() {
        try {
            OONISession session = EngineProvider.get().newSession(EngineProvider.get().getDefaultSessionConfig(
                    app, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME, new LoggerArray(), proxy));
            OONIContext ooniContext = session.newContextWithTimeout(30);

            OONICheckInConfig config = app.getOONICheckInConfig();

            ThirdPartyServices.addLogExtra("config", app.getGson().toJson(config));

            OONICheckInResults results = session.checkIn(ooniContext, config);

            ThirdPartyServices.addLogExtra("results", app.getGson().toJson(results));

            OONICheckInResults.OONICheckInInfoWebConnectivity webConnectivity = results.getWebConnectivity();

            if (webConnectivity == null || webConnectivity.getUrls().size() <= 0) {
                publishProgress(ERR, app.getString(R.string.Modal_Error_CantDownloadURLs));
                ThirdPartyServices.logException(new MKException(results));
                return;
            }

            List<Url> urls = Lists.transform(
                webConnectivity.getUrls(),
                url -> new Url(url.getUrl(), url.getCategoryCode(), url.getCountryCode())
            );
            List<String> inputs = Url.saveOrUpdate(urls);

            currentTest.setInputs(inputs);

            if (currentTest.getMax_runtime() == null)
                currentTest.setMax_runtime(app.getPreferenceManager().getMaxRuntime());
            publishProgress(URL);
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }

    @Override
    public void onStart(String name) {
        publishProgress(RUN, name);
    }

    @Override
    public final void onProgress(int progress) {
        if (!isInterrupted())
            publishProgress(PRG, Integer.toString(progress));
    }

    @Override
    public final void onLog(String log) {
        if (!isInterrupted())
            publishProgress(LOG, log);
    }

    @Override
    public final void onError(String error) {
        publishProgress(ERR, error);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //Send broadcast to the RunningActivity
        sendBroadcast(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        sendBroadcast(END);
        unregisterConnChange();
    }

    private void sendBroadcast(String... values) {
        //This Broadcast is sent to the RunningActivity (if alive) to update the UI
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

    /**
     * Checks if current task can be interrupted before interrupting the task and
     * broadcast onInterrupt to listeners ( {@code sendBroadcast(INT) } )
     */
    public synchronized void interrupt() {
        if (currentTest != null && currentTest.canInterrupt()) {
            currentTest.interrupt();
        }
        interrupt = true;
        sendBroadcast(INT);
    }

    public int getMax(PreferenceManager preferenceManager) {
        return ListUtility.sum(Lists.transform(
                testSuites,
                testSuite -> testSuite.getTestList(preferenceManager).length * 100
        ));
    }
}
