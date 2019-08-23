package org.openobservatory.ooniprobe.client;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    public void testSelectMeasurementsWithJson() {
        Delete.table(Measurement.class);
        addMeasurement(EXISTING_REPORT_ID, true);
        addMeasurement(EXISTING_REPORT_ID_2, true);
        addMeasurement(NONEXISTING_REPORT_ID, true);
        addMeasurement(NONEXISTING_REPORT_ID, true);
        List<Measurement> measurements = Measurement.selectMeasurementsWithJson(a);
        if (measurements.size() == 2 &&
                containsMeasurement(measurements, EXISTING_REPORT_ID) &&
                containsMeasurement(measurements, EXISTING_REPORT_ID_2))
            Assert.assertTrue(true);

        Assert.fail();
    }

    private Boolean containsMeasurement(List<Measurement> measurements, String report_id){
        for (int i = 0; i < measurements.size(); i++) {
            Measurement measurement = measurements.get(i);
            if (measurement.report_id.equals(report_id))
                return true;
        }
        return false;
    }

    private Measurement addMeasurement(String report_id, Boolean write_file) {
        //Simulating measurement done and uploaded
        //write_file is added just for consistency with iOS, here we always set it to true
        Measurement measurement = new Measurement();
        measurement.report_id = report_id;
        measurement.is_done = true;
        measurement.is_uploaded = true;
        measurement.save();
        if (write_file){
            File entryFile = Measurement.getEntryFile(c, measurement.id, measurement.test_name);
            entryFile.getParentFile().mkdirs();
            try {
                FileUtils.writeStringToFile(
                        entryFile,
                        "",
                        Charset.forName("UTF-8")
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return measurement;
    }

}
