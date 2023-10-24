package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapters.StringListRecyclerViewAdapter;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.databinding.ActivityOonirunBinding;
import org.openobservatory.ooniprobe.domain.GetTestSuite;
import org.openobservatory.ooniprobe.domain.VersionCompare;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class OoniRunActivity extends AbstractActivity {
	ActivityOonirunBinding binding;

	@Inject
	PreferenceManager preferenceManager;

	@Inject
	VersionCompare versionCompare;

	@Inject
	GetTestSuite getSuite;

	@Inject
	Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivityComponent().inject(this);
		binding = ActivityOonirunBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		manageIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		manageIntent(intent);
	}

	private void manageIntent(Intent intent) {
		if (isTestRunning()) {
			Toast.makeText(this, getString(R.string.OONIRun_TestRunningError), Toast.LENGTH_LONG).show();
			finish();
		}
		else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			String mv = uri == null ? null : uri.getQueryParameter("mv");
			String tn = uri == null ? null : uri.getQueryParameter("tn");
			String ta = uri == null ? null : uri.getQueryParameter("ta");
			loadScreen(mv, tn, ta);
		}
		else if (Intent.ACTION_SEND.equals(intent.getAction())) {
			String url = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (url != null && Patterns.WEB_URL.matcher(url).matches()) {
				List<String> urls = Collections.singletonList(url);
				AbstractSuite suite = getSuite.get("web_connectivity", urls);
				if (suite != null) {
					loadSuite(suite, urls);
				} else {
					loadInvalidAttributes();
				}
			} else {
				loadInvalidAttributes();
			}
		}
	}

	private void loadScreen(String mv, String tn, String ta){
		String[] split = BuildConfig.VERSION_NAME.split("-");
		String version_name = split[0];
		if (mv != null && tn != null) {
			if (versionCompare.compare(version_name, mv) >= 0) {
				try {
					Attribute attribute = gson.fromJson(ta, Attribute.class);
					List<String> urls = (attribute!=null && attribute.urls != null) ? attribute.urls : Collections.emptyList();
					AbstractSuite suite = getSuite.get(tn, urls);
					if (suite != null) {
						loadSuite(suite, urls);
					} else {
						loadInvalidAttributes();
					}
				} catch (Exception e) {
					loadInvalidAttributes();
				}
			} else {
				loadOutOfDate();
			}
		} else {
			loadInvalidAttributes();
		}
	}

	private void loadOutOfDate() {
		setTextColor(getResources().getColor(R.color.color_black));
		binding.title.setText(R.string.OONIRun_OONIProbeOutOfDate);
		binding.desc.setText(R.string.OONIRun_OONIProbeNewerVersion);
		binding.run.setText(R.string.OONIRun_Update);
		binding.icon.setImageResource(R.drawable.update);
		binding.run.setOnClickListener(v -> {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
			finish();
		});
	}

	private void loadSuite(AbstractSuite suite, List<String> urls) {
		ArrayList<String> items = new ArrayList<>();
		binding.icon.setImageResource(suite.getIcon());
		binding.title.setText(suite.getTestList(preferenceManager)[0].getLabelResId());
		binding.desc.setText(getString(R.string.OONIRun_YouAreAboutToRun));
		if (urls != null) {
			for (String url : urls) {
				if (URLUtil.isValidUrl(url))
					items.add(url);
			}
			binding.recycler.setLayoutManager(new LinearLayoutManager(this));
			binding.recycler.setAdapter(new StringListRecyclerViewAdapter(items));
		}
		setThemeColor(getResources().getColor(suite.getColor()));
		binding.run.setOnClickListener(v -> {

			RunningActivity.runAsForegroundService(OoniRunActivity.this, suite.asArray(),this::finish, preferenceManager);

		});
	}

	public void setThemeColor(int color) {
		Window window = getWindow();
		window.setStatusBarColor(color);
		binding.appbarLayout.setBackgroundColor(color);
		if (ColorUtils.calculateLuminance(color) > 0.5) {
			setTextColor(Color.BLACK);
		} else {
			binding.title.setTextColor(Color.BLACK);
		}
	}

	public void setTextColor(int color){
		binding.title.setTextColor(color);
		binding.icon.setColorFilter(color);
		binding.desc.setTextColor(color);
		binding.run.setTextColor(color);
		binding.run.setStrokeColor(ColorStateList.valueOf(color));
	}

	private void loadInvalidAttributes() {
		setTextColor(getResources().getColor(R.color.color_black));
		binding.title.setText(R.string.OONIRun_InvalidParameter);
		binding.desc.setText(R.string.OONIRun_InvalidParameter_Msg);
		binding.run.setText(R.string.OONIRun_Close);
		binding.icon.setImageResource(R.drawable.question_mark);
		binding.run.setOnClickListener(v -> finish());
	}
}
