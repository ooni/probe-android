package org.openobservatory.ooniprobe.domain;

import android.content.Context;

import androidx.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import javax.inject.Inject;

public class MeasurementsManager {

    private final Context context;
    private final OONIAPIClient apiClient;

    @Inject
    MeasurementsManager(Context context, OONIAPIClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
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

    public void checkReportAndDeleteIt(Measurement measurement, @Nullable CheckReportIdCallback checkReportIdCallback) {
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
}
