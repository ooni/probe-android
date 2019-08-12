package org.openobservatory.ooniprobe.client;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementCallback;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OONIAPIClientTest extends AbstractTest {
    private static final String EXISTING_REPORT_ID = "20190113T202156Z_AS327931_CgoC3KbgM6zKajvIIt1AxxybJ1HbjwwWJjsJnlxy9rpcGY54VH";
    private static final String EXISTING_REPORT_ID_2 = "20190702T000027Z_AS5413_6FT78sjp5qnESDVWlFlm6bfxxwOEqR08ySAwigTF6C8PFCbMsM";
    private static final String NONEXISTING_REPORT_ID = "EMPTY";
    private static final String NON_PARSABLE_URL = "https://\t";
    private static final String JSON_URL = "https://api.ooni.io/api/v1/measurement/temp-id-263478291";

    @Test
    public void getMeasurementSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getApiClient().getMeasurement(EXISTING_REPORT_ID, null).enqueue(new GetMeasurementsCallback() {
            @Override
            public void onSuccess(ApiMeasurement.Result result) {
                Assert.assertNotNull(result);
                signal.countDown();
            }

            @Override
            public void onError(String msg) {
                Assert.fail();
                signal.countDown();
            }
        });
        try {
            signal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getMeasurementError() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getApiClient().getMeasurement(NONEXISTING_REPORT_ID, null).enqueue(new GetMeasurementsCallback() {
            @Override
            public void onSuccess(ApiMeasurement.Result result) {
                Assert.fail();
                signal.countDown();
            }

            @Override
            public void onError(String msg) {
                signal.countDown();
            }
        });
        try {
            signal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDeleteJsons() {
    }

    @Test
    public void getMeasurementJsonSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        new OkHttpClient().newCall(new Request.Builder().url(JSON_URL).build()).enqueue(new GetMeasurementCallback() {
            @Override
            public void onSuccess(String json) {
                Assert.assertNotNull(json);
                signal.countDown();
            }

            @Override
            public void onError(String msg) {
                Assert.fail();
                signal.countDown();
            }
        });
        try {
            signal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getMeasurementJsonError() {
        final CountDownLatch signal = new CountDownLatch(1);
        new OkHttpClient().newCall(new Request.Builder().url(NON_PARSABLE_URL).build()).enqueue(new GetMeasurementCallback() {
            @Override
            public void onSuccess(String json) {
                Assert.fail();
                signal.countDown();
            }

            @Override
            public void onError(String msg) {
                signal.countDown();
            }
        });
        try {
            signal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
