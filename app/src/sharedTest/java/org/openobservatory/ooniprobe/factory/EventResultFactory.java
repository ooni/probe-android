package org.openobservatory.ooniprobe.factory;

import org.openobservatory.ooniprobe.model.jsonresult.EventResult;

public class EventResultFactory {

    public static String ERROR_THROW_KEY = "THROW ERROR";

    public static EventResult build(String key) {
        EventResult.Value value = new EventResult.Value();
        return build(key, value);
    }

    public static EventResult build(String key, EventResult.Value value) {
        EventResult event = new EventResult();
        event.key = key;
        event.value = value;
        return event;
    }

    public static EventResult buildStarted() {
        return build("status.started");
    }

    public static EventResult buildCreateReport(String id) {
        EventResult.Value value = new EventResult.Value();
        value.report_id = id;
        return build("status.report_create", value);
    }

    public static EventResult buildMeasurementStart(int measurementId, String url) {
        EventResult.Value value = new EventResult.Value();
        value.idx = measurementId;
        value.input = url;

        return build("status.measurement_start", value);
    }

    public static EventResult buildMeasurementEntry(int measurementId, String jsonResult) {
        EventResult.Value value = new EventResult.Value();
        value.idx = measurementId;
        value.json_str = jsonResult;

        return build("measurement", value);
    }

    public static EventResult buildMeasurementUpload(int measurementId, boolean isFailure) {
        EventResult.Value value = new EventResult.Value();
        value.idx = measurementId;
        value.failure = isFailure ? "failed" : null;

        return build("status.measurement_submission", value);
    }

    public static EventResult buildMeasurementDone(int measurementId) {
        EventResult.Value value = new EventResult.Value();
        value.idx = measurementId;

        return build("status.measurement_done", value);
    }

    public static EventResult buildIpLookup(String networkName, String ip, String asn, String countryCode) {
        EventResult.Value value = new EventResult.Value();
        value.probe_network_name =networkName;
        value.probe_ip = ip;
        value.probe_asn = asn;
        value.probe_cc = countryCode;

        return build("status.geoip_lookup", value);
    }

    public static EventResult buildDataUsage(double download, double upload) {
        EventResult.Value value = new EventResult.Value();
        value.downloaded_kb = download;
        value.uploaded_kb = upload;

        return build("status.end", value);
    }

    public static EventResult buildLog(String message) {
        EventResult.Value value = new EventResult.Value();
        value.message = message;

        return build("log", value);
    }
    public static EventResult buildProgress(Double percentage, String message) {
        EventResult.Value value = new EventResult.Value();
        value.percentage = percentage;
        value.message = message;

        return build("status.progress", value);
    }

    public static EventResult resolverFailure(String message) {
        EventResult.Value value = new EventResult.Value();
        value.failure = message;

        return build("failure.resolver_lookup", value);
    }

    public static EventResult buildEnded() {
        return build("task_terminated");
    }

}
