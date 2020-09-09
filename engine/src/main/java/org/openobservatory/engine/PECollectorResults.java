package org.openobservatory.engine;

import java.io.PrintWriter;
import java.io.StringWriter;

import oonimkall.SubmitResults;

final class PECollectorResults implements OONICollectorResults {
    private SubmitResults results;
    private Exception error;

    public PECollectorResults(SubmitResults results) {
        this.results = results;
    }

    public PECollectorResults(Exception error) {
        this.error = error;
    }

    @Override
    public boolean isGood() {
        return error == null;
    }

    @Override
    public String getReason() {
        if (error != null) {
            return error.toString();
        }
        return "";
    }

    @Override
    public String getLogs() {
        if (error != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            return pw.toString();
        }
        return "";
    }

    @Override
    public String getUpdatedSerializedMeasurement() {
        if (error != null) {
            return "";
        }
        return results.getUpdatedMeasurement();
    }

    @Override
    public String getUpdatedReportID() {
        if (error != null) {
            return "";
        }
        return results.getUpdatedReportID();
    }
}
