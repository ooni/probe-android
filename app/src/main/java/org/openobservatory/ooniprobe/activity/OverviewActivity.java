package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Test;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OverviewActivity extends AppCompatActivity {
	public static final String TEST = "test";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.icon) ImageView icon;
	@BindView(R.id.title) TextView title;
	@BindView(R.id.configure) Button configure;
	@BindView(R.id.desc1) TextView desc1;
	@BindView(R.id.desc2) TextView desc2;

	public static Intent newIntent(Context context, Test test) {
		return new Intent(context, OverviewActivity.class).putExtra(TEST, test);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Test test = (Test) getIntent().getSerializableExtra(TEST);
		setTheme(test.getTheme());
		setContentView(R.layout.activity_overview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		icon.setImageResource(test.getIcon());
		title.setText(test.getTitle());
		desc1.setText(test.getDesc1());
		desc2.setText(test.getDesc2());
	}
}
