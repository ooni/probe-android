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
import org.openobservatory.ooniprobe.common.OoniIOClient;
import org.openobservatory.ooniprobe.model.RetrieveUrlResponse;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.MessageDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RunningActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.log) TextView log;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.animation) LottieAnimationView animation;
	private AbstractSuite testSuite;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, RunningActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
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
			boolean isWebConn = false;
			for (AbstractTest abstractTest : testList)
				if (abstractTest instanceof WebConnectivity) {
					isWebConn = true;
					break;
				}
			if (isWebConn) {
				Retrofit retrofit = new Retrofit.Builder().baseUrl("https://events.proteus.test.ooni.io/").addConverterFactory(GsonConverterFactory.create()).build();
				retrofit.create(OoniIOClient.class).getUrls("IT", getPreferenceManager().isAllCategoryEnabled() ? null : getPreferenceManager().getEnabledCategory()).enqueue(new Callback<RetrieveUrlResponse>() {
					@Override public void onResponse(Call<RetrieveUrlResponse> call, Response<RetrieveUrlResponse> response) {
						if (response.isSuccessful() && response.body() != null && response.body().results != null) {
							ArrayList<String> inputs = new ArrayList<>();
							for (Url url : response.body().results) {
								Url storedUrl = Url.checkExistingUrl(url.url, url.category_code, url.country_code);
								inputs.add(storedUrl.url);
							}
							for (AbstractTest abstractTest : testList)
								if (abstractTest instanceof WebConnectivity) {
									WebConnectivity wc = (WebConnectivity) abstractTest;
									wc.setInputs(inputs);
									wc.setMax_runtime(getPreferenceManager().getMaxRuntime());
								}
							run(testList);
						}
					}

					@Override public void onFailure(Call<RetrieveUrlResponse> call, Throwable t) {
						MessageDialogFragment.newInstance(getString(R.string.Modal_Error), getString(R.string.Modal_Error_CantDownloadUrls), true).show(getFragmentManager(), null);
					}
				});
			} else
				run(testList);
		}
	}

	@Override public void onBackPressed() {
		// TODO add toast to
	}

	private void run(AbstractTest[] testList) {
		new TestAsyncTaskImpl(this, testSuite.getName()).execute(testList);
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
