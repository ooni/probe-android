package org.openobservatory.ooniprobe.reciver;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.common.collect.Lists;
import com.google.common.math.Stats;

import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.List;

public class TestRunBroadRequestReceiver extends BroadcastReceiver implements ServiceConnection {
    private final EventListener listener;
    private final PreferenceManager preferenceManager;
    public RunTestService service;
    private boolean isBound;
    private Integer runtime;

    public TestRunBroadRequestReceiver(PreferenceManager preferenceManager, EventListener listener) {
        this.preferenceManager = preferenceManager;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("key");
        String value = intent.getStringExtra("value");
        switch (key) {
            case TestAsyncTask.START:
                listener.onStart(service);
                break;
            case TestAsyncTask.RUN:
                listener.onRun(value);
                break;
            case TestAsyncTask.PRG:
                List<AbstractSuite> previousTestSuits =
                        service.task.testSuites.subList(0, service.task.testSuites.indexOf(service.task.currentSuite));
                int previousTestProgress = (int) Stats.of(Lists.transform(
                        previousTestSuits,
                        input -> input.getTestList(preferenceManager).length * 100
                )).sum();
                int previousTestRuntime = (int) Stats.of(Lists.transform(
                        previousTestSuits,
                        input -> input.getRuntime(preferenceManager)
                )).sum();
                int prgs = Integer.parseInt(value);
                int currentTestRuntime = service.task.currentSuite.getRuntime(preferenceManager);
                int currentTestMax = service.task.currentSuite.getTestList(preferenceManager).length * 100;
                double timeLeft = runtime - ((((double) prgs) / currentTestMax * currentTestRuntime) + previousTestRuntime);

                listener.onProgress(previousTestProgress + prgs, timeLeft);
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
                listener.onInt();
                break;
            case TestAsyncTask.END:
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
        runtime = (int) Stats.of(Lists.transform(service.task.testSuites, input -> input.getRuntime(preferenceManager))).sum();

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

    public interface EventListener {
        void onStart(RunTestService service);

        void onRun(String value);

        void onProgress(int state, double eta);

        void onLog(String value);

        void onError(String value);

        void onUrl();

        void onInt();

        void onEnd(Context context);
    }
}