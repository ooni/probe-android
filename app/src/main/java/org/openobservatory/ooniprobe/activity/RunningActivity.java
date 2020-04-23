package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.MessageDialogFragment;

public class RunningActivity extends AbstractActivity {
    private static final String TEST = "test";
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.log)
    TextView log;
    @BindView(R.id.eta)
    TextView eta;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.animation)
    LottieAnimationView animation;
    private ArrayList<AbstractSuite> testSuites;
    private AbstractSuite testSuite;
    private boolean background;
    private Integer runtime;

    public static Intent newIntent(AbstractActivity context, ArrayList<AbstractSuite> testSuites) {
        if (ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            new MessageDialogFragment.Builder()
                    .withTitle(context.getString(R.string.Modal_Error))
                    .withMessage(context.getString(R.string.Modal_Error_NoInternet))
                    .build().show(context.getSupportFragmentManager(), null);
            return null;
        } else {
            Bundle extra = new Bundle();
            extra.putSerializable(TEST, testSuites);
            return new Intent(context, RunningActivity.class).putExtra(TEST, extra);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MaterialComponents_NoActionBar_App);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);
        Bundle extra = getIntent().getBundleExtra(TEST);
        testSuites = (ArrayList<AbstractSuite>) extra.getSerializable(TEST);
        if (testSuites == null)
            finish();
        runtTest();
    }

    private void runtTest() {
        //TODO cycle array
        if (testSuites.size() > 0) {
            testSuite = testSuites.get(0);
            testStart();
            setTestRunning(true);
            new TestAsyncTaskImpl(this, testSuite.getResult()).execute(testSuite.getTestList(getPreferenceManager()));
        }
    }

    private void testStart(){
        runtime = testSuite.getRuntime(getPreferenceManager());
        getWindow().setBackgroundDrawableResource(testSuite.getColor());
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(testSuite.getColor());
        }
        animation.setImageAssetsFolder("anim/");
        animation.setAnimation(testSuite.getAnim());
        animation.setRepeatCount(Animation.INFINITE);
        animation.playAnimation();
        progress.setIndeterminate(true);
        eta.setText(R.string.Dashboard_Running_CalculatingETA);
        progress.setMax(testSuite.getTestList(getPreferenceManager()).length * 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        background = false;
    }

    @Override
    protected void onPause() {
        background = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        setTestRunning(false);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.Modal_Error_CantCloseScreen), Toast.LENGTH_SHORT).show();
    }

    private static class TestAsyncTaskImpl extends TestAsyncTask<RunningActivity> {
        TestAsyncTaskImpl(RunningActivity activity, Result result) {
            super(activity, result);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            RunningActivity act = ref.get();
            if (act != null && !act.isFinishing())
                switch (values[0]) {
                    case RUN:
                        act.name.setText(values[1]);
                        break;
                    case PRG:
                        act.progress.setIndeterminate(false);
                        int prgs = Integer.parseInt(values[1]);
                        act.progress.setProgress(prgs);
                        act.eta.setText(act.getString(R.string.Dashboard_Running_Seconds, String.valueOf(Math.round(act.runtime - ((double) prgs) / act.progress.getMax() * act.runtime))));
                        break;
                    case LOG:
                        act.log.setText(values[1]);
                        break;
                    case ERR:
                        Toast.makeText(act, values[1], Toast.LENGTH_SHORT).show();
                        act.finish();
                        break;
                    case URL:
                        act.progress.setIndeterminate(false);
                        act.runtime = act.testSuite.getRuntime(act.getPreferenceManager());
                        break;
                }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RunningActivity act = ref.get();
            act.testSuites.remove(act.testSuite);
            if (act.testSuites.size() == 0)
                endTest(act);
            else
                act.runtTest();
        }

        private void endTest(RunningActivity act){
            if (act != null && !act.isFinishing()) {
                if (act.background) {
                    NotificationService.notifyTestEnded(act, act.testSuite);
                } else
                    act.startActivity(MainActivity.newIntent(act, R.id.testResults));
                act.finish();
            }

        }
    }
}
