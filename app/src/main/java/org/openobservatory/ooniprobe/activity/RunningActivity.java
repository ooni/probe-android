package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;
import org.openobservatory.ooniprobe.test2.AbstractTest;
import org.openobservatory.ooniprobe.test2.TestAsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunningActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.log) TextView log;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.icon) ImageView icon;

	public static Intent newIntent(Context context, Test test) {
		return new Intent(context, RunningActivity.class).putExtra(TEST, test);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Test test = (Test) getIntent().getSerializableExtra(TEST);
		setTheme(test.getThemeDark());
		setContentView(R.layout.activity_running);
		ButterKnife.bind(this);
		icon.setImageResource(test.getIcon());
		AbstractTest[] testList = TestAsyncTask.getIMTestList(this);
		progress.setMax(testList.length * 100);
		new TestAsyncTaskImpl(this).execute(testList);

	/*	switch (test.getTitle()) {
			case R.string.Test_Websites_Fullname:
				NetworkTest.WCNetworkTest wcTest = new NetworkTest.WCNetworkTest(this);
				wcTest.run();
				break;
			case R.string.Test_InstantMessaging_Fullname:
				NetworkTest.IMNetworkTest imTest = new NetworkTest.IMNetworkTest(this);
				imTest.run();
				break;
			case R.string.Test_Middleboxes_Fullname:
				NetworkTest.MBNetworkTest mbTest = new NetworkTest.MBNetworkTest(this);
				mbTest.run();
				break;
			case R.string.Test_Performance_Fullname:
				NetworkTest.SPNetworkTest spTest = new NetworkTest.SPNetworkTest(this);
				spTest.run();
				break;
		}*/
	}

	private static class TestAsyncTaskImpl extends TestAsyncTask {
		private WeakReference<RunningActivity> ref;

		TestAsyncTaskImpl(RunningActivity act) {
			ref = new WeakReference<>(act);
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

		}
	}
}
