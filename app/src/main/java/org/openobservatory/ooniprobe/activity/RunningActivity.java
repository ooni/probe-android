package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunningActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.log) TextView log;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.animation) LottieAnimationView animation;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, RunningActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbstractSuite testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		setTheme(testSuite.getThemeDark());
		setContentView(R.layout.activity_running);
		ButterKnife.bind(this);
		animation.setImageAssetsFolder("anim/");
		animation.setAnimation(testSuite.getAnim());
		animation.setRepeatCount(Animation.INFINITE);
		animation.playAnimation();
		AbstractTest[] testList = testSuite.getTestList(getPreferenceManager());
		if (testList != null) {
			progress.setMax(testList.length * 100);
			new TestAsyncTaskImpl(this, testSuite.getName()).execute(testList);
		}
	}

	@Override public void onBackPressed() {
		// TODO add toast to
	}

	private static class TestAsyncTaskImpl extends TestAsyncTask<RunningActivity> {
		TestAsyncTaskImpl(RunningActivity activity, String name) {
			super(activity, new Result(name));
		}

		@Override protected void onProgressUpdate(String... values) {
			RunningActivity act = ref.get();
			if (act != null && !act.isFinishing())
				switch (values[0]) {
					case TestAsyncTask.RUN:
						act.name.setText(values[1]);
						break;
					case TestAsyncTask.PRG:
						act.progress.setProgress(Integer.parseInt(values[1]));
						break;
					case TestAsyncTask.LOG:
						act.log.setText(values[1]);
						break;
				}
		}

		@Override protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			RunningActivity act = ref.get();
			if (act != null && !act.isFinishing())
				act.startActivity(MainActivity.newIntent(act, R.id.testResults));
		}
	}
}
