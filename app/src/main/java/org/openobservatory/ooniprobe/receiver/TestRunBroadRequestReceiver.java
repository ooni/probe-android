package org.openobservatory.ooniprobe.receiver;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.common.ListUtility;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.TestProgressRepository;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.List;

/**
 * Code that receives and handles broadcast intents sent by {@link RunTestService}.
 *
 * @see RunTestService.ActionReceiver#onReceive(Context, Intent) RunTestService.ActionReceiver#onReceive(Context, Intent)ActionReceiver#onReceive.
 */
public class TestRunBroadRequestReceiver extends BroadcastReceiver implements ServiceConnection {
    private final EventListener listener;
    private final PreferenceManager preferenceManager;
    private final TestProgressRepository testProgressRepository;
    public RunTestService service;
    private boolean isBound;
    private Integer runtime;

    /**
     * Instantiates a new Test run broad request receiver.
     *  @param preferenceManager the preference manager
     * @param listener          the listener
     * @param testProgressRepository
     */
    public TestRunBroadRequestReceiver(PreferenceManager preferenceManager, EventListener listener, TestProgressRepository testProgressRepository) {
        this.preferenceManager = preferenceManager;
        this.listener = listener;
        this.testProgressRepository = testProgressRepository;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("key");
        String value = intent.getStringExtra("value");
        // If either key is null, do nothing
        if (key == null) {
            return;
        }
        switch (key) {
            case TestAsyncTask.START:
                listener.onStart(service);
                break;
            case TestAsyncTask.RUN:
                listener.onRun(value);
                break;
            case TestAsyncTask.PRG:
                try {
                    if (service != null && service.task.testSuites.contains(service.task.currentSuite)) {
                        List<AbstractSuite> previousTestSuites =
                                service.task.testSuites.subList(0, service.task.testSuites.indexOf(service.task.currentSuite));
                        int previousTestProgress = ListUtility.sum(Lists.transform(
                                previousTestSuites,
                                input -> input.getTestList(preferenceManager).length * 100
                        ));
                        int previousTestRuntime = ListUtility.sum(Lists.transform(
                                previousTestSuites,
                                input -> input.getRuntime(preferenceManager)
                        ));
                        int prgs = Integer.parseInt(value);
                        int currentTestRuntime = service.task.currentSuite.getRuntime(preferenceManager);
                        int currentTestMax = service.task.currentSuite.getTestList(preferenceManager).length * 100;
                        double timeLeft = runtime - ((((double) prgs) / currentTestMax * currentTestRuntime) + previousTestRuntime);
                        int progress = previousTestProgress + prgs;
                        testProgressRepository.updateProgress(progress);
                        testProgressRepository.updateEta(timeLeft);
                        listener.onProgress(progress, timeLeft);
                    }
                } catch (Exception e) {
                    ThirdPartyServices.logException(e);
                }
                break;
            case TestAsyncTask.LOG:
                listener.onLog(value);
                break;
            case TestAsyncTask.ERR:
                listener.onError(value);
                break;
            case TestAsyncTask.URL:
                listener.onUrl();
                break;
            case TestAsyncTask.INT:
                listener.onInterrupt();
                break;
            case TestAsyncTask.END:
                testProgressRepository.updateProgress(null);
                testProgressRepository.updateEta(null);
                listener.onEnd(context);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName cname, IBinder binder) {
        //Bind the service to this activity
        RunTestService.TestBinder b = (RunTestService.TestBinder) binder;
        service = b.getService();
        isBound = true;
        listener.onStart(service);
        runtime = ListUtility.sum(Lists.transform(service.task.testSuites, input -> input.getRuntime(preferenceManager)));
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

    /**
     * Callback for {@link RunTestService} events.
     */
    public interface EventListener {
        /**
         * On test suite started.
         *
         * @param service the service
         */
        void onStart(RunTestService service);

        /**
         * Experiment {@code status.started} received from {@link org.openobservatory.ooniprobe.test.EngineProvider} event.
         *
         * @param value the value
         */
        void onRun(String value);

        /**
         * Experiment {@code status.progress} received from {@link org.openobservatory.ooniprobe.test.EngineProvider} event.
         *
         * @param state the state
         * @param eta   the eta
         */
        void onProgress(int state, double eta);

        /**
         * Experiment {@code log} received from {@link org.openobservatory.ooniprobe.test.EngineProvider} event.
         *
         * @param value the value
         */
        void onLog(String value);

        /**
         * Error running experiment.
         *
         * @param value the value
         */
        void onError(String value);

        /**
         * Invoked when {@link TestAsyncTask#downloadURLs()} completes successfully.
         */
        void onUrl();

        /**
         * Called when background tasked is interrupted.
         *
         * when {@link TestAsyncTask#interrupt()} is called.
         */
        void onInterrupt();

        /**
         * Called when background task is completed.
         *
         * when {@link TestAsyncTask#onPostExecute(Void)} is called.
         *
         * @param context the context
         */
        void onEnd(Context context);
    }
}