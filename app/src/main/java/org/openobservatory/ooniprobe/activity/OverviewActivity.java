package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.InternalStyleSheet;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OverviewActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.icon) ImageView icon;
	@BindView(R.id.title) TextView title;
	@BindView(R.id.configure) Button configure;
	@BindView(R.id.desc) MarkdownView desc;
	private AbstractSuite testSuite;

	public static Intent newIntent(Context context, AbstractSuite testSuite) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, testSuite);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testSuite = (AbstractSuite) getIntent().getSerializableExtra(TEST);
		setTheme(testSuite.getThemeDark());
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		icon.setImageResource(testSuite.getIcon());
		title.setText(testSuite.getTitle());
		InternalStyleSheet css = new Github();
		// TODO try https://github.com/noties/Markwon
		css.addFontFace("Fira Sans", null, null, null, "url('https://fonts.googleapis.com/css?family=Fira+Sans')");
		desc.addStyleSheet(css);
		desc.loadMarkdown(getString(testSuite.getDesc1()) + "\n\n" + getString(testSuite.getDesc2()));
	}

	@OnClick(R.id.configure) void onConfigureClick() {
		startActivity(PreferenceActivity.newIntent(this, testSuite.getPref()));
	}

	@OnClick(R.id.run) void onRunClick() {
		startActivity(RunningActivity.newIntent(this, testSuite, null));
	}
}
