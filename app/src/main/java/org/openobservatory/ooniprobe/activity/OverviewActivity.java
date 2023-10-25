package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReadMorePlugin;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Locale;

import javax.inject.Inject;

import io.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
	private static final String TEST = "test";

	ActivityOverviewBinding binding;
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
		Markwon markwon = Markwon.builder(this)
				.usePlugin(new ReadMorePlugin())
				.build();
		if (testSuite.getName().equals(ExperimentalSuite.NAME)) {
			String experimentalLinks =
					"\n\n* [STUN Reachability](https://github.com/ooni/spec/blob/master/nettests/ts-025-stun-reachability.md)" +
					"\n\n* [DNS Check](https://github.com/ooni/spec/blob/master/nettests/ts-028-dnscheck.md)" +
					"\n\n* [ECH Check](https://github.com/ooni/spec/blob/master/nettests/ts-039-echcheck.md)" +
					"\n\n* [Tor Snowflake](https://ooni.org/nettest/tor-snowflake/) "+ String.format(" ( %s )",getString(R.string.Settings_TestOptions_LongRunningTest))+
					"\n\n* [Vanilla Tor](https://github.com/ooni/spec/blob/master/nettests/ts-016-vanilla-tor.md) " + String.format(" ( %s )",getString(R.string.Settings_TestOptions_LongRunningTest));
			markwon.setMarkdown(binding.desc, getString(testSuite.getDesc1(), experimentalLinks));
			if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL)
				binding.desc.setTextDirection(View.TEXT_DIRECTION_RTL);
		}
		else
			markwon.setMarkdown(binding.desc, getString(testSuite.getDesc1()));
		Result lastResult = Result.getLastResult(testSuite.getName());
		if (lastResult == null)
			binding.lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
		else
			binding.lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));

		setUpOnCLickListeners();
	}

	private void setUpOnCLickListeners() {
		binding.customUrl.setOnClickListener(view -> customUrlClick());
	}

	@Override protected void onResume() {
		super.onResume();
		testSuite.setTestList((AbstractTest[]) null);
		testSuite.getTestList(preferenceManager);
		binding.runtime.setText(getString(R.string.twoParam, getString(testSuite.getDataUsage()), getString(R.string.Dashboard_Card_Seconds, testSuite.getRuntime(preferenceManager).toString())));
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	void customUrlClick() {
		startActivity(new Intent(this, CustomWebsiteActivity.class));
	}
}
