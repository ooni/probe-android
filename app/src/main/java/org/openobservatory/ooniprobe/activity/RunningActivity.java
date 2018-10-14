package org.openobservatory.ooniprobe.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.OoniIOClient;
import org.openobservatory.ooniprobe.model.RetrieveUrlResponse;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.utils.ConnectionState;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
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
	public static final String ID_MEASUREMENT = "idMeasurement";
	public static final String TEST_RUN = "TEST_RUN";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.log) TextView log;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.animation) LottieAnimationView animation;
	private AbstractSuite testSuite;
	private AbstractTest[] testList;
	private Result result;
	private boolean background;

	public static Intent newIntent(FragmentActivity context, AbstractSuite testSuite, Integer idMeasurement) {
		if (ConnectionState.getInstance(context).getNetworkType().equals("no_internet")) {
			MessageDialogFragment.newInstance(context.getString(R.string.Modal_Error), context.getString(R.string.Modal_Error_NoInternet), false).show(context.getSupportFragmentManager(), null);
			return null;
		} else
			return new Intent(context, RunningActivity.class).putExtra(TEST, testSuite).putExtra(ID_MEASUREMENT, idMeasurement);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int idMeasurement = getIntent().getIntExtra(ID_MEASUREMENT, -1);
		if (idMeasurement != -1) {
			Measurement failedMeasurement = Measurement.querySingle(idMeasurement);
			failedMeasurement.result.load();
			testSuite = failedMeasurement.result.getTestSuite();
			AbstractTest abstractTest = failedMeasurement.getTest();
			if (abstractTest instanceof WebConnectivity)
				((WebConnectivity) abstractTest).setInputs(Collections.singletonList(failedMeasurement.url.url));
			testList = new AbstractTest[]{abstractTest};
			result = failedMeasurement.result;
			run(testList);
			failedMeasurement.is_rerun = true;
			failedMeasurement.save();
		} else {
			testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
			testList = testSuite.getTestList(getPreferenceManager());
			result = new Result(testSuite.getName());
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
						MessageDialogFragment.newInstance(getString(R.string.Modal_Error), getString(R.string.Modal_Error_CantDownloadURLs), true).show(getSupportFragmentManager(), null);
					}
				});
			} else
				run(testList);
		}
		setTheme(testSuite.getThemeDark());
		setContentView(R.layout.activity_running);
		ButterKnife.bind(this);
		animation.setImageAssetsFolder("anim/");
		animation.setAnimation(testSuite.getAnim());
		animation.setRepeatCount(Animation.INFINITE);
		animation.playAnimation();
		progress.setMax(testList.length * 100);
	}

	@Override protected void onResume() {
		super.onResume();
		background = false;
	}

	@Override protected void onPause() {
		super.onPause();
		background = true;
	}

	@Override public void onBackPressed() {
		//TODO-ALE add toast
		//TODO-LOR add string for toast
	}

	private void run(AbstractTest[] testList) {
		new TestAsyncTaskImpl(this, result).execute(testList);
	}

	private static class TestAsyncTaskImpl extends TestAsyncTask<RunningActivity> {
		public TestAsyncTaskImpl(RunningActivity activity, Result result) {
			super(activity, result);
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
			if (act != null && !act.isFinishing()) {
				if (act.background) {
					NotificationManager notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
					if (notificationManager != null) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
							notificationManager.createNotificationChannel(new NotificationChannel(TEST_RUN, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)); // TODO LOR add string
						NotificationCompat.Builder b = new NotificationCompat.Builder(act, TEST_RUN);
						b.setAutoCancel(true);
						b.setDefaults(Notification.DEFAULT_ALL);
						Drawable d = act.getResources().getDrawable(act.testSuite.getIcon());
						Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(bitmap);
						d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
						d.draw(canvas);
						b.setLargeIcon(bitmap);
						b.setSmallIcon(R.drawable.notification_icon);
						b.setContentTitle(act.getString(R.string.General_AppName));
						b.setContentText(act.getString(act.testSuite.getTitle()) + " " + act.getString(R.string.Notification_FinishedRunning));
						b.setContentIntent(PendingIntent.getActivity(act, 0, MainActivity.newIntent(act, R.id.testResults), PendingIntent.FLAG_UPDATE_CURRENT));
						notificationManager.notify(1, b.build());
					}
					act.finish();
				} else
					act.startActivity(MainActivity.newIntent(act, R.id.testResults));
			}
		}
	}
}
