package org.openobservatory.ooniprobe.client;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementJsonCallback;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;

public class OONIAPIClientTest extends AbstractTest {
    private static final String EXISTING_REPORT_ID = "20190113T202156Z_AS327931_CgoC3KbgM6zKajvIIt1AxxybJ1HbjwwWJjsJnlxy9rpcGY54VH";
    private static final String NONEXISTING_REPORT_ID = "EMPTY";
    private static final String NON_EXISTING_MEASUREMENT_URL = "https://api.ooni.io/api/v1/measurement/nonexistent-measurement-url";
    private static final String CLIENT_URL = "https://ams-pg.ooni.org";

    @Test
    public void getMeasurementSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getApiClientWithUrl(CLIENT_URL).getMeasurement(EXISTING_REPORT_ID, null).enqueue(new GetMeasurementsCallback() {
            @Override
            public void onSuccess(ApiMeasurement.Result result) {
                Assert.assertNotNull(result);
                a.getOkHttpClient().newCall(new Request.Builder().url(result.measurement_url).build()).enqueue(new GetMeasurementJsonCallback() {
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
        a.getApiClientWithUrl(CLIENT_URL).getMeasurement(NONEXISTING_REPORT_ID, null).enqueue(new GetMeasurementsCallback() {
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
    public void testSelectMeasurementsWithJson() throws IOException {
        final CountDownLatch signal = new CountDownLatch(1);
        Delete.table(Measurement.class);
        addMeasurement(EXISTING_REPORT_ID, false);
        a.getApiClientWithUrl(CLIENT_URL).checkReportId(EXISTING_REPORT_ID).enqueue(new CheckReportIdCallback() {
            @Override
            public void onSuccess(Boolean found) {
                Assert.assertTrue(found);
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

    private Boolean containsMeasurement(List<Measurement> measurements, String report_id){
        for (int i = 0; i < measurements.size(); i++) {
            Measurement measurement = measurements.get(i);
            if (measurement.report_id.equals(report_id))
                return true;
        }
        return false;
    }

    @Test
    public void getMeasurementJsonError() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getOkHttpClient().newCall(new Request.Builder().url(NON_EXISTING_MEASUREMENT_URL).build()).enqueue(new GetMeasurementJsonCallback() {
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

    private Measurement addMeasurement(String report_id, Boolean write_file) throws IOException {
        //Simulating measurement done and uploaded
        Measurement measurement = new Measurement();
        measurement.report_id = report_id;
        measurement.is_done = true;
        measurement.is_uploaded = true;
        measurement.save();
        if (write_file){
            File entryFile = Measurement.getEntryFile(c, measurement.id, measurement.test_name);
            entryFile.getParentFile().mkdirs();
            FileUtils.writeStringToFile(
                    entryFile,
                    "",
                    Charset.forName("UTF-8")
            );
        }
        return measurement;
    }
}
