package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.BindingAdapter;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.overview.OverviewViewModel;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.TaskExecutor;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import io.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
	private static final String TEST = "test";
	private static final String TAG = OverviewActivity.class.getSimpleName();

	private ActivityOverviewBinding binding;
	private AbstractSuite testSuite;

	@Inject
	PreferenceManager preferenceManager;
	@Inject
	OverviewViewModel viewModel;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivityComponent().inject(this);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		binding = ActivityOverviewBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		binding.setViewmodel(viewModel);
		binding.setLifecycleOwner(this);
		onTestSuiteChanged();
		if(testSuite.isTestEmpty(preferenceManager)){
			binding.run.setAlpha(0.5F);
			binding.run.setEnabled(false);
		}

		if (testSuite.getName().equals(OONIRunSuite.NAME)) {
			setThemeColor(((OONIRunSuite)testSuite).getDescriptor().getParsedColor());
			binding.swipeRefresh.setOnRefreshListener(this::initiateRefresh);
		} else {
			binding.swipeRefresh.setEnabled(false);
		}
		setUpOnCLickListeners();
	}

	public void setThemeColor(int color) {
		Window window = getWindow();
		window.setStatusBarColor(color);
		binding.run.setTextColor(color);
		binding.appbarLayout.setBackgroundColor(color);
	}

	private void onTestSuiteChanged() {
		setTitle(testSuite.getTitle());
		viewModel.onTestSuiteChanged(testSuite);
		binding.executePendingBindings();
	}


	private void setUpOnCLickListeners() {
		binding.run.setOnClickListener(view -> onRunClick());
		binding.customUrl.setOnClickListener(view -> customUrlClick());
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
					if (descriptorToUpdate.shouldUpdate(descriptor)){
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
		this.testSuite = descriptor.getTestSuite(this);
		this.onTestSuiteChanged();
	}

	@BindingAdapter(value = {"richText", "testSuiteName"})
	public static void setRichText(TextView view, String richText,String testSuiteName) {
		try {
			Markwon markwon = Markwon.builder(view.getContext()).build();

			if (Objects.equals(testSuiteName,ExperimentalSuite.NAME)) {
				markwon.setMarkdown(view, richText);
				if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL)
					view.setTextDirection(View.TEXT_DIRECTION_RTL);
			} else {
				markwon.setMarkdown(view, richText);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ThirdPartyServices.logException(e);
		}
	}

	@BindingAdapter({"resource"})
	public static void setImageViewResource(ImageView imageView, int resource) {
		imageView.setImageResource(resource);
	}

	@BindingAdapter({"dataUsage", "runTime"})
	public static void setDataUsage(TextView view, int dataUsage, String runTime) {
		Context context = view.getContext();
		view.setText(
				context.getString(
						R.string.twoParam,
						context.getString(dataUsage),
						context.getString(R.string.Dashboard_Card_Seconds, runTime)
				)
		);

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
