package org.openobservatory.ooniprobe.test;

import android.os.AsyncTask;

import org.openobservatory.engine.Engine;
import org.openobservatory.engine.GeoIPLookupResults;
import org.openobservatory.engine.GeoIPLookupTask;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.ExceptionManager;
import org.openobservatory.ooniprobe.model.api.UrlList;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

public class TestAsyncTask<ACT extends AbstractActivity> extends AsyncTask<AbstractTest, String, Void> implements AbstractTest.TestCallback {
	public static final List<AbstractSuite> SUITES = Arrays.asList(new WebsitesSuite(),
			new InstantMessagingSuite(), new CircumventionSuite(), new PerformanceSuite());
	public static final String PRG = "PRG";
	public static final String LOG = "LOG";
	public static final String RUN = "RUN";
	public static final String ERR = "ERR";
	public static final String URL = "URL";
	protected final WeakReference<ACT> ref;
	private final Result result;

	protected TestAsyncTask(ACT activity, Result result) {
		this.ref = new WeakReference<>(activity);
		this.result = result;
		result.is_viewed = false;
		result.save();
	}

	@Override protected Void doInBackground(AbstractTest... tests) {
		ACT act = ref.get();
		if (act != null && !act.isFinishing())
			try {
				boolean downloadUrls = false;
				for (AbstractTest abstractTest : tests)
					if (abstractTest instanceof WebConnectivity && abstractTest.getInputs() == null) {
						downloadUrls = true;
						break;
					}
				if (downloadUrls) {
					GeoIPLookupTask geoIPLookup = Engine.newGeoIPLookupTask();
					geoIPLookup.setTimeout(act.getResources().getInteger(R.integer.default_timeout));
					boolean okay = Engine.maybeUpdateResources(act);
					if (!okay) {
						Exception e = new Exception("MKResourcesManager didn't find resources");
						ExceptionManager.logException(e);
						throw e;
					}
					geoIPLookup.setCABundlePath(Engine.getCABundlePath(act));
					geoIPLookup.setCountryDBPath(Engine.getCountryDBPath(act));
					geoIPLookup.setASNDBPath(Engine.getASNDBPath(act));
					GeoIPLookupResults results = geoIPLookup.perform();
					String probeCC = results.isGood() ? results.getProbeCC() : "XX";
					Response<UrlList> response = act.getOrchestraClient().getUrls(probeCC, act.getPreferenceManager().getEnabledCategory()).execute();
					if (response.isSuccessful() && response.body() != null && response.body().results != null) {
						ArrayList<String> inputs = new ArrayList<>();
						for (Url url : response.body().results)
							inputs.add(Url.checkExistingUrl(url.url, url.category_code, url.country_code).url);
						for (AbstractTest abstractTest : tests) {
							abstractTest.setInputs(inputs);
							abstractTest.setMax_runtime(act.getPreferenceManager().getMaxRuntime());
						}
						publishProgress(URL);
					}
				}
				for (int i = 0; i < tests.length; i++)
					if (!act.isFinishing())
						tests[i].run(act, act.getPreferenceManager(), act.getGson(), result, i, this);
			} catch (Exception e) {
				publishProgress(ERR, act.getString(R.string.Modal_Error_CantDownloadURLs));
			}
		return null;
	}

	@Override public void onStart(String name) {
		publishProgress(RUN, name);
	}

	@Override public final void onProgress(int progress) {
		publishProgress(PRG, Integer.toString(progress));
	}

	@Override public final void onLog(String log) {
		publishProgress(LOG, log);
	}
}
