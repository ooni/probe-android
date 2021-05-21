package org.openobservatory.ooniprobe.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.domain.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OONIAPIClientTest extends RobolectricAbstractTest {

    private static final String EXISTING_REPORT_ID = "20190113T202156Z_AS327931_CgoC3KbgM6zKajvIIt1AxxybJ1HbjwwWJjsJnlxy9rpcGY54VH";
    private static final String NONEXISTING_REPORT_ID = "EMPTY";
    private static final String CLIENT_URL = "https://ams-pg.ooni.org";
    private static final String NON_EXISTING_MEASUREMENT_URL = CLIENT_URL + "/api/v1/measurement/nonexistent-measurement-url";

    @Test
    public void getMeasurementSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getApiClient().getMeasurement(EXISTING_REPORT_ID, null).enqueue(new GetMeasurementsCallback() {
            @Override
            public void onSuccess(ApiMeasurement.Result result) {
                Assert.assertNotNull(result);
                a.getOkHttpClient().newCall(new Request.Builder().url(result.measurement_url).build()).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Assert.assertNotNull(response.body());
                        signal.countDown();
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
        final CountDownLatch signal = new CountDownLatch(1);
        Delete.table(Measurement.class);
        addMeasurement(EXISTING_REPORT_ID, false);
        a.getApiClient().checkReportId(EXISTING_REPORT_ID).enqueue(new CheckReportIdCallback() {
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

    @Test
    public void getMeasurementJsonError() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getOkHttpClient().newCall(new Request.Builder().url(NON_EXISTING_MEASUREMENT_URL).build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                            .toJson(JsonParser.parseString(response.body().string()));
                    Assert.fail();
                } catch (Exception e) {
                    Assert.assertTrue(true);
                }
                signal.countDown();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
        if (write_file) {
            File entryFile = Measurement.getEntryFile(a, measurement.id, measurement.test_name);
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
