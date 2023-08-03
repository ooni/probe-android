package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.openobservatory.engine.OONIRunNettest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.databinding.ActivityOonirunBinding;
import org.openobservatory.ooniprobe.domain.GetTestSuite;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager.FetchTestDescriptorResponse;
import org.openobservatory.ooniprobe.domain.VersionCompare;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.fragment.OoniRunListFragment;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class OoniRunActivity extends AbstractActivity {
	ActivityOonirunBinding binding;
	private ArrayList<OONIRunNettest> items;

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
		items = new ArrayList<>();
		manageIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		manageIntent(intent);
	}

	private void manageIntent(Intent intent) {
		Uri uri = intent.getData();
		if (uri == null) return;

		String host = uri.getHost();

		if ("runv2".equals(host) || "run.test.ooni.org".equals(host)) {
			try {
				long runId = Long.parseLong(uri.getPathSegments().get(0));
				FetchTestDescriptorResponse response = TestDescriptorManager.fetchDataFromRunId(runId, this);
				loadScreen(response);
			} catch (Exception exception) {
				exception.printStackTrace();
				ThirdPartyServices.logException(exception);
				loadInvalidAttributes();
			}
		} else if (isTestRunning()) {
			Toast.makeText(this, getString(R.string.OONIRun_TestRunningError), Toast.LENGTH_LONG).show();
			finish();
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			if ("nettest".equals(host) || "run.ooni.io".equals(host)) {
				String mv = uri.getQueryParameter("mv");
				String tn = uri.getQueryParameter("tn");
				String ta = uri.getQueryParameter("ta");
				loadScreen(mv, tn, ta);
			} else {
				loadInvalidAttributes();
			}
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
		} else {
			loadInvalidAttributes();
		}
	}

	private void loadScreen(FetchTestDescriptorResponse response) {

		binding.icon.setImageResource(response.suite.getIconGradient());
		binding.icon.setColorFilter(getResources().getColor(R.color.color_gray7));

		binding.author.setText(response.descriptor.getAuthor());
		binding.author.setVisibility(View.VISIBLE);

		binding.shortDesc.setText(response.descriptor.getShortDescription());
		binding.shortDesc.setVisibility(View.VISIBLE);

		binding.title.setText(response.descriptor.getName());
		binding.desc.setText(response.descriptor.getDescription());


		items.addAll(
				Lists.transform(
						Lists.newArrayList(response.suite.getTestList(null)),
						test -> new OONIRunNettest(
								test.getLabelResId() == (R.string.Test_Experimental_Fullname) ? test.getName() :getString(test.getLabelResId()),
								test.getInputs()
						)
				)
		);

		FragmentTransaction mTransactiont = getSupportFragmentManager().beginTransaction();

		mTransactiont.replace(R.id.items, OoniRunListFragment.newInstance(), OoniRunListFragment.class.getName());
		mTransactiont.commit();

		binding.iconBig.setVisibility(View.GONE);
		// TODO: 18/07/2023 (aanorbel) Add translation
		binding.run.setText("Install");
		binding.run.setOnClickListener(
				v -> {
					response.descriptor.save();
					ActivityCompat.startActivity(this, OverviewActivity.newIntent(this, response.suite), null);
				}
		);
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
		binding.title.setText(R.string.OONIRun_OONIProbeOutOfDate);
		binding.desc.setText(R.string.OONIRun_OONIProbeNewerVersion);
		binding.run.setText(R.string.OONIRun_Update);
		binding.icon.setImageResource(R.drawable.update);
		binding.iconBig.setImageResource(R.drawable.update);
		binding.iconBig.setVisibility(View.VISIBLE);
		binding.run.setOnClickListener(v -> {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
			finish();
		});
	}

	private void loadSuite(AbstractSuite suite, List<String> urls) {
		binding.icon.setImageResource(suite.getIcon());
		binding.title.setText(suite.getTestList(preferenceManager)[0].getLabelResId());
		binding.desc.setText(getString(R.string.OONIRun_YouAreAboutToRun));
		if (urls != null) {
			for (String url : urls) {
				if (URLUtil.isValidUrl(url)) {
					items.add(new OONIRunNettest(url,new ArrayList<>()));
				}
			}
			binding.iconBig.setVisibility(View.GONE);
		} else {
			binding.iconBig.setImageResource(suite.getIcon());
			binding.iconBig.setVisibility(View.VISIBLE);
		}
		FragmentTransaction mTransactiont = getSupportFragmentManager().beginTransaction();

		mTransactiont.replace(R.id.items, OoniRunListFragment.newInstance(), OoniRunListFragment.class.getName());
		mTransactiont.commit();

		binding.run.setOnClickListener(v -> {

			RunningActivity.runAsForegroundService(OoniRunActivity.this, suite.asArray(),this::finish, preferenceManager);

		});
	}

	private void loadInvalidAttributes() {
		binding.title.setText(R.string.OONIRun_InvalidParameter);
		binding.desc.setText(R.string.OONIRun_InvalidParameter_Msg);
		binding.run.setText(R.string.OONIRun_Close);
		binding.icon.setImageResource(R.drawable.question_mark);
		binding.iconBig.setImageResource(R.drawable.question_mark);
		binding.iconBig.setVisibility(View.VISIBLE);
		binding.run.setOnClickListener(v -> finish());
	}

	public ArrayList<OONIRunNettest> getItems() {
		return items;
	}
}
