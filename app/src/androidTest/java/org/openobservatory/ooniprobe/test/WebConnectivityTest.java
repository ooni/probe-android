package org.openobservatory.ooniprobe.test;

import android.util.Log;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.engine.Engine;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class WebConnectivityTest extends AbstractTest {
    private static final String MK_NAME = "WebConnectivity";
    private static final String TAG = "integration-test";
    private static final String UNUSED_KEY = "UNUSED_KEY";
    private static final String CLIENT_URL = "https://ams-pg.ooni.org";

    @Test
    public void runWebConnectivity() {
        final CountDownLatch signal = new CountDownLatch(1);
        boolean submitted = false;
        String report_id_1 = "r1";
        String report_id_2 = "r2";
        OONIMKTask task = null;
        Settings settings = new Settings(c, a.getPreferenceManager());
        Gson gson = a.getGson();
        settings.name = MK_NAME;
        settings.inputs = Collections.singletonList("http://mail.google.com");
        settings.options.max_runtime = 10;
        settings.annotations.origin = TAG;
        settings.options.no_collector = false;
        settings.options.probe_services_base_url = CLIENT_URL;
        try {
            task = Engine.startExperimentTask(settings.toExperimentSettings(gson, c));
        } catch (Exception exc) {
            Assert.fail();
            signal.countDown();
        }
        while (!task.isDone()){
            try {
                String json = task.waitForNextEvent();
                Log.d(TAG, json);
                EventResult event = gson.fromJson(json, EventResult.class);
                switch (event.key) {
                    case "status.report_create":
                        report_id_1 = event.value.report_id;
                        break;
                    case "measurement":
                        JsonResult jr = gson.fromJson(event.value.json_str, JsonResult.class);
                        Assert.assertNotNull(jr);
                        report_id_2 = jr.report_id;
                        if (jr.test_keys.blocking == null) {
                            Assert.fail();
                            signal.countDown();
                        }
                        break;
                    case "failure.report_create":
                    case "failure.measurement_submission":
                    case "failure.startup":
                        Assert.fail();
                        signal.countDown();
                        break;
                    case "status.measurement_submission":
                        submitted = true;
                        break;
                    default:
                        Log.w(UNUSED_KEY, event.key);
                        break;
                }
            } catch (Exception ignored) {
            }
        }
        Assert.assertEquals(report_id_1, report_id_2);
        Assert.assertTrue(submitted);
        signal.countDown();
    }
}
