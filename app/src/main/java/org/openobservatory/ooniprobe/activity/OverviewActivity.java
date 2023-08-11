package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.TaskExecutor;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Locale;

import javax.inject.Inject;

import ru.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
	private static final String TEST = "test";
	private static final String TAG = OverviewActivity.class.getSimpleName();

	private ActivityOverviewBinding binding;
	private AbstractSuite testSuite;

	@Inject
	PreferenceManager preferenceManager;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivityComponent().inject(this);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		setTheme(testSuite.getThemeLight());
		binding = ActivityOverviewBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(testSuite.getTitle());
		binding.icon.setImageResource(testSuite.getIcon());
		binding.customUrl.setVisibility(testSuite.getName().equals(WebsitesSuite.NAME) ? View.VISIBLE : View.GONE);
		if(testSuite.isTestEmpty(preferenceManager)){
			binding.run.setAlpha(0.5F);
			binding.run.setEnabled(false);
		}
		if (testSuite.getName().equals(ExperimentalSuite.NAME)) {
			Markwon.setMarkdown(binding.desc, testSuite.getDesc1());
			if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL)
				binding.desc.setTextDirection(View.TEXT_DIRECTION_RTL);
		} else {
			Markwon.setMarkdown(binding.desc, testSuite.getDesc1());
		}

		if (testSuite.getName().equals(OONIRunSuite.NAME)) {
			binding.author.setText(String.format("Author : %s",((OONIRunSuite)testSuite).getDescriptor().getAuthor()));
			binding.author.setVisibility(View.VISIBLE);

			binding.swipeRefresh.setOnRefreshListener(this::initiateRefresh);
		} else {
			binding.swipeRefresh.setEnabled(false);
		}
		Result lastResult = Result.getLastResult(testSuite.getName());
		if (lastResult == null)
			binding.lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
		else
			binding.lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));

		setUpOnCLickListeners();
	}

	private void setUpOnCLickListeners() {
		binding.run.setOnClickListener(view -> onRunClick());
		binding.customUrl.setOnClickListener(view -> customUrlClick());
	}

	@Override protected void onResume() {
		super.onResume();
		testSuite.setTestList((AbstractTest[]) null);
		testSuite.getTestList(preferenceManager);
		binding.runtime.setText(getString(R.string.twoParam, getString(testSuite.getDataUsage()), getString(R.string.Dashboard_Card_Seconds, testSuite.getRuntime(preferenceManager).toString())));
	}


	private void initiateRefresh() {
		Log.i(TAG, "initiateRefresh");
		TestDescriptor descriptorToUpdate = ((OONIRunSuite)testSuite).getDescriptor();

		TaskExecutor executor = new TaskExecutor();
		executor.executeTask(
				() -> TestDescriptorManager.fetchDescriptorFromRunId(
						descriptorToUpdate.getRunId(),
						OverviewActivity.this
				),
				descriptor -> {
					if (descriptor.getVersion() > descriptorToUpdate.getVersion()){
						if (descriptorToUpdate.isAutoUpdate()) {
							updateDescriptor(descriptor, descriptorToUpdate);
						} else {
							prepareForUpdates(descriptor, descriptorToUpdate);
						}
					} else {
						noUpdatesAvailable();
					}
					binding.swipeRefresh.setRefreshing(false);
					return null;
				});
	}

	private void updateDescriptor(TestDescriptor descriptor, TestDescriptor descriptorToUpdate) {
		descriptor.setAutoUpdate(descriptorToUpdate.isAutoUpdate());
		descriptor.setAutoRun(descriptorToUpdate.isAutoRun());
		descriptor.save();
		binding.refresh.setVisibility(View.GONE);
		updateViewFromDescriptor(descriptor);
		Snackbar.make(
				binding.getRoot(),
				"Update Successful",
				BaseTransientBottomBar.LENGTH_LONG
		).show();
	}

	private void prepareForUpdates(TestDescriptor descriptor, TestDescriptor descriptorToUpdate) {
		binding.refresh.setOnClickListener(v -> updateDescriptor(descriptor, descriptorToUpdate));
		binding.refresh.setVisibility(android.view.View.VISIBLE);
	}

	private void noUpdatesAvailable() {
		Snackbar.make(
				binding.getRoot(),
				"No Updates available",
				BaseTransientBottomBar.LENGTH_LONG
		).show();
	}

	private void updateViewFromDescriptor(TestDescriptor descriptor) {
		// TODO use view model to update screen
	}

	void onRunClick() {
		if(!testSuite.isTestEmpty(preferenceManager)){
			RunningActivity.runAsForegroundService(this, testSuite.asArray(), this::bindTestService, preferenceManager);
		}
	}

	void customUrlClick() {
		startActivity(new Intent(this, CustomWebsiteActivity.class));
	}
}
