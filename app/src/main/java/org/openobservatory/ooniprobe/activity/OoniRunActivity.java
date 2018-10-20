package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.item.TextItem;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class OoniRunActivity extends AbstractActivity {
	public static final List<AbstractSuite> SUITES = Arrays.asList(new InstantMessagingSuite(), new MiddleBoxesSuite(), new MiddleBoxesSuite(), new PerformanceSuite(), new WebsitesSuite());
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.title) TextView title;
	@BindView(R.id.desc) TextView desc;
	@BindView(R.id.run) Button run;
	@BindView(R.id.recycler) RecyclerView recycler;
	private ArrayList<HeterogeneousRecyclerItem> items;
	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;

	/**
	 * Compares two version strings.
	 * <p>
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * @param str1 a string of ordinal numbers separated by decimal points.
	 * @param str2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2.
	 * The result is a positive integer if str1 is _numerically_ greater than str2.
	 * The result is zero if the strings are _numerically_ equal.
	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
	 */
	public static int versionCompare(String str1, String str2) {
		String[] vals1 = str1.split("\\.");
		String[] vals2 = str2.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oonirun);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
		items = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(this, items);
		recycler.setAdapter(adapter);
		gotIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		gotIntent(intent);
	}

	public void gotIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			String mv = uri == null ? null : uri.getQueryParameter("mv");
			String tn = uri == null ? null : uri.getQueryParameter("tn");
			String td = uri == null ? null : uri.getQueryParameter("td");
			String ta = uri == null ? null : uri.getQueryParameter("ta");
			String[] split = BuildConfig.VERSION_NAME.split("-");
			String version_name = split[0];
			if (mv != null && tn != null) {
				if (versionCompare(version_name, mv) >= 0) {
					Attribute attribute = new Gson().fromJson(ta, Attribute.class);
					AbstractSuite suite = getSuite(tn, attribute == null ? null : attribute.urls);
					if (suite != null) {
						title.setText(suite.getTestList(getPreferenceManager())[0].getLabelResId());
						desc.setText(td == null ? getString(R.string.OONIRun_YouAreAboutToRun) : td);
						if (attribute != null && attribute.urls != null) {
							for (String url : attribute.urls)
								items.add(new TextItem(url));
							adapter.notifyTypesChanged();
						}
						run.setOnClickListener(v -> {
							Intent runIntent = RunningActivity.newIntent(OoniRunActivity.this, suite);
							if (runIntent != null) {
								startActivity(runIntent);
								finish();
							}
						});
					} else {
						title.setText(R.string.OONIRun_InvalidParameter);
						desc.setText(R.string.OONIRun_InvalidParameter_Msg);
						run.setVisibility(View.GONE);
					}
				} else {
					title.setText(R.string.OONIRun_OONIProbeOutOfDate);
					desc.setText(R.string.OONIRun_OONIProbeNewerVersion);
					run.setOnClickListener(v -> {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
						finish();
					});
				}
			} else {
				title.setText(R.string.OONIRun_InvalidParameter);
				desc.setText(R.string.OONIRun_InvalidParameter_Msg);
				run.setVisibility(View.GONE);
			}
		}
	}

	public AbstractSuite getSuite(String tn, List<String> urls) {
		for (AbstractSuite suite : SUITES)
			for (AbstractTest test : suite.getTestList(getPreferenceManager()))
				if (test.getName().equals(tn)) {
					test.setInputs(urls);
					suite.setTestList(test);
					return suite;
				}
		return null;
	}

	public static class Attribute {
		List<String> urls;
	}
}
