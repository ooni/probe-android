package org.openobservatory.ooniprobe.client;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
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
    private static final String EXISTING_REPORT_ID = "20200113T232417Z_AS577_JZoBZCfObPmU7bxkEKUImuWl40VEb00Q8ZYcifQj3MgchgjOVd";
    private static final String EXISTING_REPORT_ID_2 = "20200113T235535Z_AS39891_ZsM2hkmJREbadpVf0dKARRLZFsrnX2LYI9PGi7HlyXFBwRyQGP";
    private static final String NONEXISTING_REPORT_ID = "EMPTY";
    private static final String NON_EXISTING_MEASUREMENT_URL = "https://api.ooni.io/api/v1/measurement/nonexistent-measurement-url";
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
    public void testSelectMeasurementsWithJson() throws IOException {
        Delete.table(Measurement.class);
        addMeasurement(EXISTING_REPORT_ID, false);
        addMeasurement(EXISTING_REPORT_ID_2, true);
        addMeasurement(NONEXISTING_REPORT_ID, true);
        List<Measurement> measurements = Measurement.selectMeasurementsWithJson(a);
        //I create 3 measurement and only two of them have a file on disk
        Assert.assertTrue(measurements.size() == 2 &&
            containsMeasurement(measurements, NONEXISTING_REPORT_ID) &&
            containsMeasurement(measurements, EXISTING_REPORT_ID_2));
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
    public void getMeasurementJsonSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getOkHttpClient().newCall(new Request.Builder().url(JSON_URL).build()).enqueue(new GetMeasurementJsonCallback() {
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
