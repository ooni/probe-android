package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.OoniIOClient;
import org.openobservatory.ooniprobe.model.RetrieveUrlResponse;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";
	public static final String MK_NAME = "WebConnectivity";
	private ArrayList<String> inputs;

	public WebConnectivity() {
		super(NAME, MK_NAME, R.string.Test_WebConnectivity_Fullname, 0);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		if (inputs == null) {
			settings.options.max_runtime = pm.getMaxRuntime();
			settings.inputs = new ArrayList<>();
			try {
				Retrofit retrofit = new Retrofit.Builder().baseUrl("https://events.proteus.test.ooni.io/").addConverterFactory(GsonConverterFactory.create()).build();
				Response<RetrieveUrlResponse> response = retrofit.create(OoniIOClient.class).getUrls("IT", pm.isAllCategoryEnabled() ? null : pm.getEnabledCategory()).execute();
				if (response.isSuccessful() && response.body() != null && response.body().results != null)
					for (Url url : response.body().results) {
						Url storedUrl = Url.checkExistingUrl(url.url, url.category_code, url.country_code);
						settings.inputs.add(storedUrl.url);
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			settings.inputs = inputs;
		run(c, pm, gson, settings, result, index, testCallback);
	}

	public void setInputs(ArrayList<String> inputs) {
		this.inputs = inputs;
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
	}
}
