package org.openobservatory.ooniprobe.domain;

import android.content.Context;

import org.openobservatory.ooniprobe.model.database.Measurement;

import javax.inject.Inject;

public class HasUploadabledReports {

    private final Context context;

    @Inject HasUploadabledReports(Context context) {
        this.context = context;
    }

    public boolean hasUploadables() {
        return Measurement.hasReport(context, Measurement.selectUploadable());
    }
}
