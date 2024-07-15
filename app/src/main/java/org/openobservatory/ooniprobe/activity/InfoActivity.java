package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ActivityInfoBinding;
import io.noties.markwon.Markwon;

public class InfoActivity extends AbstractActivity {

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityInfoBinding binding = ActivityInfoBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		binding.version.setText(getString(R.string.version, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME));

		Markwon.builder(this)
				.build()
				.setMarkdown(binding.desc, getString(R.string.Settings_About_Content_Paragraph));
		binding.blog.setOnClickListener(v -> onBlogClick());
		binding.reports.setOnClickListener(v -> onReportsClick());
		binding.learnMore.setOnClickListener(v -> onLearnMoreClick());
		binding.dataPolicy.setOnClickListener(v -> onDataPolicyClick());
	}

	void onBlogClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/blog/")));
	}

	void onReportsClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/reports/")));
	}

	void onLearnMoreClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/")));
	}

	void onDataPolicyClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.org/about/data-policy/")));
	}
}
