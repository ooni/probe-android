package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.icon) ImageView icon;
	@BindView(R.id.title) TextView title;
	@BindView(R.id.configure) Button configure;
	@BindView(R.id.desc) TextView desc;
	private AbstractSuite testSuite;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		setTheme(testSuite.getThemeLight());
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		icon.setImageResource(testSuite.getIcon());
		title.setText(testSuite.getTitle());
		Markwon.setMarkdown(desc, getString(testSuite.getDesc1()) + "\n\n" + getString(testSuite.getDesc2()));
	}

	@OnClick(R.id.configure) void onConfigureClick() {
		startActivity(PreferenceActivity.newIntent(this, testSuite.getPref()));
	}

	@OnClick(R.id.run) void onRunClick() {
		Intent intent = RunningActivity.newIntent(this, testSuite, null);
		if (intent != null)
			startActivity(intent);
	}
}
