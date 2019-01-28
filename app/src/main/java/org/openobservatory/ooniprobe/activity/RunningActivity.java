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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.utils.NotificationService;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.MessageDialogFragment;

public class RunningActivity extends AbstractActivity {
	private static final String TEST = "test";
	private static final String TEST_RUN = "TEST_RUN";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.log) TextView log;
	@BindView(R.id.eta) TextView eta;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.animation) LottieAnimationView animation;
	private AbstractSuite testSuite;
	private boolean background;
	private Integer runtime;

	public static Intent newIntent(AbstractActivity context, AbstractSuite testSuite) {
		if (context.getPreferenceManager().getNetworkType().equals(NotificationService.NO_INTERNET)) {
			MessageDialogFragment.newInstance(context.getString(R.string.Modal_Error), context.getString(R.string.Modal_Error_NoInternet), false).show(context.getSupportFragmentManager(), null);
			return null;
		} else
			return new Intent(context, RunningActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		runtime = testSuite.getRuntime(getPreferenceManager());
		setTheme(testSuite.getThemeDark());
		setContentView(R.layout.activity_running);
		ButterKnife.bind(this);
		animation.setImageAssetsFolder("anim/");
		animation.setAnimation(testSuite.getAnim());
		animation.setRepeatCount(Animation.INFINITE);
		animation.playAnimation();
		progress.setMax(testSuite.getTestList(getPreferenceManager()).length * 100);
		setTestRunning(true);
		new TestAsyncTaskImpl(this, testSuite.getResult()).execute(testSuite.getTestList(getPreferenceManager()));
	}

	@Override protected void onResume() {
		super.onResume();
		background = false;
	}

	@Override protected void onPause() {
		background = true;
		super.onPause();
	}

	@Override protected void onDestroy() {
		setTestRunning(false);
		super.onDestroy();
	}

	@Override public void onBackPressed() {
		Toast.makeText(this, getString(R.string.Modal_Error_CantCloseScreen), Toast.LENGTH_SHORT).show();
	}

	private static class TestAsyncTaskImpl extends TestAsyncTask<RunningActivity> {
		TestAsyncTaskImpl(RunningActivity activity, Result result) {
			super(activity, result);
		}

		@Override protected void onProgressUpdate(String... values) {
			RunningActivity act = ref.get();
			if (act != null && !act.isFinishing())
				switch (values[0]) {
					case RUN:
						act.name.setText(values[1]);
						break;
					case PRG:
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
				}
		}

		@Override protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			RunningActivity act = ref.get();
			if (act != null && !act.isFinishing()) {
				if (act.background) {
					NotificationManager notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
					if (notificationManager != null && act.getPreferenceManager().isNotificationsCompletion()) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
							notificationManager.createNotificationChannel(new NotificationChannel(TEST_RUN, act.getString(R.string.Settings_Notifications_OnTestCompletion), NotificationManager.IMPORTANCE_DEFAULT));
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
				} else
					act.startActivity(MainActivity.newIntent(act, R.id.testResults));
				act.finish();
			}
		}
	}
}
