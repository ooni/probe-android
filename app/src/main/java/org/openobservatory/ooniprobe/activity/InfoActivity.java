package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.openobservatory.measurement_kit.Version;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoActivity extends AppCompatActivity {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.version) TextView version;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		version.setText(getString(R.string.version, BuildConfig.VERSION_NAME, Version.getVersion()));
	}

	@OnClick(R.id.learnMore) void onLearnMoreClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/")));
	}

	@OnClick(R.id.dataPolicy) void onDataPolicyClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/about/data-policy/")));
	}
}
