package org.openobservatory.ooniprobe.domain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONISubmitResults;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.JsonPrinter;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.domain.callback.DomainCallback;
import org.openobservatory.ooniprobe.domain.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeasurementsManager {

    private final Context context;
    private final JsonPrinter jsonPrinter;
    private final OONIAPIClient apiClient;
    private final OkHttpClient httpClient;

    @Inject
    MeasurementsManager(Context context, JsonPrinter jsonPrinter, OONIAPIClient apiClient, OkHttpClient httpClient) {
        this.context = context;
        this.apiClient = apiClient;
        this.httpClient = httpClient;
        this.jsonPrinter = jsonPrinter;
    }

    public Measurement get(int measurementId) {
        return SQLite.select().from(Measurement.class)
                .where(Measurement_Table.id.eq(measurementId)).querySingle();
    }

    public boolean hasUploadables() {
        return Measurement.hasReport(context, Measurement.selectUploadable());
    }

    public boolean canUpload(Measurement measurement) {
        return (!measurement.is_failed
                && (!measurement.is_uploaded || measurement.report_id == null)
                && measurement.hasReportFile(context));
    }

    public String getExplorerUrl(Measurement measurement) {
        String url = "https://explorer.ooni.io/measurement/" + measurement.report_id;
        if (measurement.test_name.equals("web_connectivity"))
            url = url + "?input=" + measurement.url.url;
        return url;
    }

    public void checkReportAndDeleteIt(Measurement measurement, @Nullable DomainCallback<Boolean> checkReportIdCallback) {
        if (!measurement.hasReportFile(context)) {
            return;
        }

        apiClient.checkReportId(measurement.report_id).enqueue(new CheckReportIdCallback() {
            @Override
            public void onSuccess(Boolean found) {
                if (found) {
                    Measurement.deleteMeasurementWithReportId(context, measurement.report_id);
                }
                if (checkReportIdCallback != null) {
                    checkReportIdCallback.onSuccess(found);
                }
            }

            @Override
            public void onError(String msg) {
                if (checkReportIdCallback != null) {
                    checkReportIdCallback.onError(msg);
                }
            }
        });
    }

    public boolean hasReportId(Measurement measurement) {
        return measurement.report_id != null && !measurement.report_id.isEmpty();
    }

    public String getReadableLog(Measurement measurement) throws IOException {
        File logFile = Measurement.getLogFile(context, measurement.result.id, measurement.test_name);
        return FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
    }

    public String getReadableEntry(Measurement measurement) throws IOException {
        File entryFile = Measurement.getEntryFile(context, measurement.id, measurement.test_name);
        return jsonPrinter.prettyText(FileUtils.readFileToString(entryFile, StandardCharsets.UTF_8));
    }

    public void syncMeasurements(Activity activity,
                                 OnStartResubmission onStartResubmission,
                                 OnCancelResubmission onCancelResubmission,
                                 Integer... params) {

        if (determineIfMeasurementsWereTakenOverVPN(params)) {
            new AlertDialog.Builder(activity, R.style.MaterialAlertDialogCustom)
                    .setTitle(activity.getString(R.string.Modal_UploadVPNResults_Title))
                    .setMessage(activity.getString(R.string.Modal_UploadVPNResults_Message))
                    .setNegativeButton(R.string.Modal_Cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        onCancelResubmission.onCancel();
                    })
                    .setPositiveButton(R.string.Modal_OK, (dialogInterface, i) -> {
                        onStartResubmission.onResubmit();
                    })
                    .show();
        } else {
            onStartResubmission.onResubmit();
        }
    }

    public boolean determineIfMeasurementsWereTakenOverVPN(Integer... params) {
        if (params.length != 2)
            throw new IllegalArgumentException("MKCollectorResubmitTask requires 2 nullable params: result_id, measurement_id");
        Where<Measurement> msmQuery = Measurement.selectUploadable();
        if (params[0] != null) {
            msmQuery.and(Measurement_Table.result_id.eq(params[0]));
        }
        if (params[1] != null) {
            msmQuery.and(Measurement_Table.id.eq(params[1]));
        }
        //Get a list of measurements with report file
        List<Measurement> measurements = Measurement.withReport(context, msmQuery);

        return Lists.transform(measurements, measurement -> {
            try {
                return ((Application) context.getApplicationContext()).getGson().fromJson(
                        FileUtils.readFileToString(
                                Measurement.getEntryFile(context, measurement.id, measurement.test_name),
                                StandardCharsets.UTF_8
                        ),
                        Measurement.DataRoot.class
                ).annotations.network_type;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).contains(ReachabilityManager.VPN);
    }

    public void downloadReport(Measurement measurement, DomainCallback<String> callback) {
        //measurement.getUrlString will return null when the measurement is not a web_connectivity
        apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()).enqueue(new GetMeasurementsCallback() {
            @Override
            public void onSuccess(ApiMeasurement.Result result) {
                downloadMeasurement(result, callback);
            }

            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }
        });
    }

    private void downloadMeasurement(ApiMeasurement.Result result, DomainCallback<String> callback) {
        httpClient.newCall(new Request.Builder().url(result.measurement_url).build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    callback.onSuccess(jsonPrinter.prettyText(response.body().string()));
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public boolean reSubmit(Measurement m, OONISession session) {
        File file = Measurement.getEntryFile(context, m.id, m.test_name);
        String input;
        long uploadTimeout = getTimeout(file.length());
        OONIContext ooniContext = session.newContextWithTimeout(uploadTimeout);
        try {
            input = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            OONISubmitResults results = session.submit(ooniContext, input);
            FileUtils.writeStringToFile(file, results.getUpdatedMeasurement(), StandardCharsets.UTF_8);
            m.report_id = results.getUpdatedReportID();
            m.is_uploaded = true;
            m.is_upload_failed = false;
            m.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
            return false;
        }
    }

    public long getTimeout(long length) {
        return length / 2000 + 10;
    }

    public interface OnStartResubmission {
        void onResubmit();
    }
    public interface OnCancelResubmission {
        void onCancel();
    }
}
