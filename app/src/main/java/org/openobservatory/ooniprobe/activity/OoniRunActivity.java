package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.domain.GetTestSuite;
import org.openobservatory.ooniprobe.domain.VersionCompare;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.item.TextItem;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class OoniRunActivity extends AbstractActivity {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.icon) ImageView icon;
	@BindView(R.id.iconBig) ImageView iconBig;
	@BindView(R.id.title) TextView title;
	@BindView(R.id.desc) TextView desc;
	@BindView(R.id.run) Button run;
	@BindView(R.id.recycler) RecyclerView recycler;
	private ArrayList<HeterogeneousRecyclerItem> items;
	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;

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
		setContentView(R.layout.activity_oonirun);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
		items = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(this, items);
		recycler.setAdapter(adapter);
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
					if (attribute!=null){
						AbstractSuite suite = getSuite.get(tn, attribute.urls);
						if (suite != null) {
							loadSuite(suite, attribute.urls);
						} else {
							loadInvalidAttributes();
						}
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
		title.setText(R.string.OONIRun_OONIProbeOutOfDate);
		desc.setText(R.string.OONIRun_OONIProbeNewerVersion);
		run.setText(R.string.OONIRun_Update);
		icon.setImageResource(R.drawable.update);
		iconBig.setImageResource(R.drawable.update);
		iconBig.setVisibility(View.VISIBLE);
		run.setOnClickListener(v -> {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
			finish();
		});
	}

	private void loadSuite(AbstractSuite suite, List<String> urls) {
		icon.setImageResource(suite.getIcon());
		title.setText(suite.getTestList(preferenceManager)[0].getLabelResId());
		desc.setText(getString(R.string.OONIRun_YouAreAboutToRun));
		if (urls != null) {
			for (String url : urls) {
				if (URLUtil.isValidUrl(url))
					items.add(new TextItem(url));
			}
			adapter.notifyTypesChanged();
			iconBig.setVisibility(View.GONE);
		} else {
			iconBig.setImageResource(suite.getIcon());
			iconBig.setVisibility(View.VISIBLE);
		}
		run.setOnClickListener(v -> {

			RunningActivity.runAsForegroundService(OoniRunActivity.this, suite.asArray(),this::finish, preferenceManager);

		});
	}

	private void loadInvalidAttributes() {
		title.setText(R.string.OONIRun_InvalidParameter);
		desc.setText(R.string.OONIRun_InvalidParameter_Msg);
		run.setText(R.string.OONIRun_Close);
		icon.setImageResource(R.drawable.question_mark);
		iconBig.setImageResource(R.drawable.question_mark);
		iconBig.setVisibility(View.VISIBLE);
		run.setOnClickListener(v -> finish());
	}
}
