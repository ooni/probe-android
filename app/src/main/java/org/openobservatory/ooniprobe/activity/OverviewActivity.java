package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Test;

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
	private Test test;

	public static Intent newIntent(Context context, Test test) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, test);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		test = (Test) getIntent().getSerializableExtra(TEST);
		setTheme(test.getThemeLight());
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		icon.setImageResource(test.getIcon());
		title.setText(test.getTitle());
		InternalStyleSheet css = new Github();
		css.addFontFace("FiraSans", "regular", "italic", "normal", "url('firasans_regular_normal.otf')");
		desc.addStyleSheet(css);
		desc.loadMarkdown(getString(test.getDesc1()) + "\n\n" + getString(test.getDesc1()));
	}

	@OnClick(R.id.configure) void onConfigureClick() {
		startActivity(PreferenceActivity.newIntent(this, test.getPref()));
	}

	@OnClick(R.id.run) void onRunClick() {
		startActivity(RunningActivity.newIntent(this, test));
	}
}
