package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.openobservatory.measurement_kit.Version;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

public class InfoActivity extends AppCompatActivity {
	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		findViewById(R.id.learn_more_button).setOnClickListener(v -> {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/")));
		});
		findViewById(R.id.privacy_policy_button).setOnClickListener(v -> {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/about/data-policy/")));
		});
		TextView version_text = findViewById(R.id.ooniprobe_version);
		version_text.setText("ooniprobe: " + BuildConfig.VERSION_NAME + "\n" + "measurement-kit: " + Version.getVersion());
	}
}
