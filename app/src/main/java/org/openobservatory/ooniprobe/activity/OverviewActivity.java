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
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.util.Locale;
import java.util.Objects;

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
	@BindView(R.id.run) Button run;
	private TestDescriptor descriptor;

	@Inject
	PreferenceManager preferenceManager;

	public static Intent newIntent(Context context, TestDescriptor descriptor) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, descriptor);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivityComponent().inject(this);
		descriptor = (TestDescriptor) getIntent().getSerializableExtra(TEST);
		customizeTheme();
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		 setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(descriptor.getName());
		icon.setImageResource(getResources().getIdentifier(descriptor.getIcon(), "drawable", getPackageName()));
		customUrl.setVisibility(descriptor.getName().equals(WebsitesSuite.NAME) ? View.VISIBLE : View.GONE);
		if(!descriptor.isEnabled()){
			run.setAlpha(0.5F);
			run.setEnabled(false);
		}
		if (descriptor.getName().equals(ExperimentalSuite.NAME)) {
			String experimentalLinks =
					"\n\n* [STUN Reachability](https://github.com/ooni/spec/blob/master/nettests/ts-025-stun-reachability.md)" +
					"\n\n* [DNS Check](https://github.com/ooni/spec/blob/master/nettests/ts-028-dnscheck.md)" +
					"\n\n* [Tor Snowflake](https://ooni.org/nettest/tor-snowflake/) "+ String.format(" ( %s )",getString(R.string.Settings_TestOptions_LongRunningTest))+
					"\n\n* [Vanilla Tor](https://github.com/ooni/spec/blob/master/nettests/ts-016-vanilla-tor.md) " + String.format(" ( %s )",getString(R.string.Settings_TestOptions_LongRunningTest));
			// TODO:(aanorbel) replace links
			// Markwon.setMarkdown(desc, getString(descriptor.getDescription(), experimentalLinks));
			Markwon.setMarkdown(desc, descriptor.getDescription());
			if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL)
				desc.setTextDirection(View.TEXT_DIRECTION_RTL);
		}
		else
			Markwon.setMarkdown(desc, descriptor.getDescription());
		Result lastResult = Result.getLastResult(descriptor.getName());
		if (lastResult == null)
			lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
		else
			lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
	}

	private void customizeTheme() {
		if (Objects.equals(descriptor.getName(), getString(R.string.Test_Websites_Fullname))) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Websites);
		} else if (Objects.equals(descriptor.getName(), getString(R.string.Test_InstantMessaging_Fullname))) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_InstantMessaging);
		} else if (Objects.equals(descriptor.getName(), getString(R.string.Test_Circumvention_Fullname))) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Circumvention);
		} else if (Objects.equals(descriptor.getName(), getString(R.string.Test_Performance_Fullname))) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Performance);
		} else if (Objects.equals(descriptor.getName(), getString(R.string.Test_Experimental_Fullname))) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental);
		}else {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental);
		}
	}

	@Override protected void onResume() {
		super.onResume();
		/*descriptor.setTestList((AbstractTest[]) null);
		descriptor.getTestList(preferenceManager);*/
		runtime.setText(getString(R.string.twoParam, descriptor.getDataUsage(), getString(R.string.Dashboard_Card_Seconds, descriptor.getRuntime().toString())));
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@OnClick(R.id.run) void onRunClick() {
		if(descriptor.isEnabled()){
			// RunningActivity.runAsForegroundService(this, descriptor.asArray(), this::bindTestService, preferenceManager);
		}
	}

	@OnClick(R.id.customUrl) void customUrlClick() {
		startActivity(new Intent(this, CustomWebsiteActivity.class));
	}
}
