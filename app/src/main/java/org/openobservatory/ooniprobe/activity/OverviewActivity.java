package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
	private static final String TEST = "test";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.icon) ImageView icon;
	@BindView(R.id.runtime) TextView runtime;
	@BindView(R.id.lastTime) TextView lastTime;
	@BindView(R.id.desc) TextView desc;
	@BindView(R.id.customUrl) Button customUrl;
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
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(testSuite.getTitle());
		icon.setImageResource(testSuite.getIcon());
		customUrl.setVisibility(testSuite.getName().equals(WebsitesSuite.NAME) ? View.VISIBLE : View.GONE);
		if (testSuite.getName().equals(ExperimentalSuite.NAME)) {
			String experimentalLinks =
					"\n\n* [STUN Reachability](https://github.com/ooni/spec/blob/master/nettests/ts-025-stun-reachability.md)" +
					"\n\n* [DNS Check](https://github.com/ooni/spec/blob/master/nettests/ts-028-dnscheck.md)" +
					"\n\n* [Tor Snowflake](https://ooni.org/nettest/tor-snowflake/)" +
					"\n\n* [Vanilla Tor](https://github.com/ooni/spec/blob/master/nettests/ts-016-vanilla-tor.md)";
			Markwon.setMarkdown(desc, getString(testSuite.getDesc1(), experimentalLinks));
		}
		else
			Markwon.setMarkdown(desc, getString(testSuite.getDesc1()));
		Result lastResult = Result.getLastResult(testSuite.getName());
		if (lastResult == null)
			lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
		else
			lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
	}

	@Override protected void onResume() {
		super.onResume();
		testSuite.setTestList((AbstractTest[]) null);
		testSuite.getTestList(preferenceManager);
		runtime.setText(getString(R.string.twoParam, getString(testSuite.getDataUsage()), getString(R.string.Dashboard_Card_Seconds, testSuite.getRuntime(preferenceManager).toString())));
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@OnClick(R.id.run) void onRunClick() {
		RunningActivity.runAsForegroundService(this, testSuite.asArray(), this::bindTestService, preferenceManager);
	}

	@OnClick(R.id.customUrl) void customUrlClick() {
		startActivity(new Intent(this, CustomWebsiteActivity.class));
	}
}
