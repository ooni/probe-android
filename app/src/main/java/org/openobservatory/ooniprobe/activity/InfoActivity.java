package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.noties.markwon.Markwon;

public class InfoActivity extends AbstractActivity {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.version) TextView version;
	@BindView(R.id.desc) TextView description;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		version.setText(getString(R.string.version, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME));
		Markwon.setMarkdown(description, getString(R.string.Settings_About_Content_Paragraph));
	}

	@OnClick(R.id.blog) void onBlogClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/blog/")));
	}

	@OnClick(R.id.reports) void onReportsClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/reports/")));
	}

	@OnClick(R.id.learnMore) void onLearnMoreClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/")));
	}

	@OnClick(R.id.dataPolicy) void onDataPolicyClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/about/data-policy/")));
	}
}
