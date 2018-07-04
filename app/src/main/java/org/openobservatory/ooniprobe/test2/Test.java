package org.openobservatory.ooniprobe.test2;

import android.util.Log;

import com.google.gson.Gson;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.measurement_kit.nettests.FacebookMessengerTest;
import org.openobservatory.measurement_kit.nettests.TelegramTest;
import org.openobservatory.measurement_kit.nettests.WhatsappTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.AbstractJsonResult;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test<Result extends AbstractJsonResult> {
	private BaseTest test;
	private Class<Result> classOfResult;
	private Gson gson;

	public Test(AbstractActivity activity, BaseTest test, Class<Result> classOfResult) {
		this.test = test;
		this.classOfResult = classOfResult;
		PreferenceManager preferenceManager = activity.getPreferenceManager();
		gson = activity.getGson();
		String filesDir = activity.getFilesDir().toString();
		test.use_logcat();
		test.set_output_filepath(filesDir + "/" + new Random().nextInt() + ".json"); // TODO
		test.set_error_filepath(filesDir + "/" + new Random().nextInt() + ".log"); // TODO
		test.set_verbosity(BuildConfig.DEBUG ? LogSeverity.LOG_DEBUG2 : LogSeverity.LOG_INFO);
		test.set_option("geoip_country_path", filesDir + "/GeoIP.dat");
		test.set_option("geoip_asn_path", filesDir + "/GeoIPASNum.dat");
		test.set_option("save_real_probe_ip", preferenceManager.getIncludeIp());
		test.set_option("save_real_probe_asn", preferenceManager.getIncludeAsn());
		test.set_option("save_real_probe_cc", preferenceManager.getIncludeCc());
		test.set_option("no_collector", preferenceManager.getNoUploadResults());
		test.set_option("software_name", "ooniprobe-android");
		test.set_option("software_version", VersionUtils.get_software_version());
	}

	public static Test.TestJsonResult[] getIMTestList(AbstractActivity activity) {
		return new Test.TestJsonResult[]{
				new Test.TestJsonResult(activity, new WhatsappTest()),
				new Test.TestJsonResult(activity, new TelegramTest()),
				new Test.TestJsonResult(activity, new FacebookMessengerTest())
		};
	}

	public List<Result> run(int index, TestCallback testCallback) {
		List<Result> results = new ArrayList<>();
		test.on_progress((v, s) -> testCallback.onProgress(Double.valueOf((index + v) * 100).intValue()));
		test.on_log((l, s) -> testCallback.onLog(s));
		test.on_entry(entry -> {
			Log.d("entry", entry);
			results.add(gson.fromJson(entry, classOfResult));
		});
		testCallback.onStart("test: " + index);
		test.run();
		return results;
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}

	public static class TestJsonResult extends Test<JsonResult> {
		TestJsonResult(AbstractActivity activity, BaseTest test) {
			super(activity, test, JsonResult.class);
		}
	}
}
