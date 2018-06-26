package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Test;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunningActivity extends AbstractActivity {
	public static final String TEST = "test";
	@BindView(R.id.name) TextView name;
	@BindView(R.id.progress) ProgressBar progress;
	@BindView(R.id.icon) ImageView icon;

	public static Intent newIntent(Context context, Test test) {
		return new Intent(context, RunningActivity.class).putExtra(TEST, test);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Test test = (Test) getIntent().getSerializableExtra(TEST);
		setTheme(test.getThemeDark());
		setContentView(R.layout.activity_running);
		ButterKnife.bind(this);
		name.setText(test.getTitle());
		icon.setImageResource(test.getIcon());
	}
}
