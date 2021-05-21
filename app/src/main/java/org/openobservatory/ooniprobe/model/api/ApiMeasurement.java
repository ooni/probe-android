package org.openobservatory.ooniprobe.model.api;

import java.util.List;

public class ApiMeasurement {
    public Metadata metadata;
    public List<Result> results = null;
    public Boolean found;

    public boolean hasResultWithMeasurements() {
        return results != null &&
                results.size() == 1 &&
                results.get(0).measurement_url != null;
    }

    public Result firstResult() {
        return results.get(0);
    }

    public static class Metadata {
        public Integer count;
        public Integer current_page;
        public Integer limit;
        public Object next_url;
        public Integer offset;
        public Integer pages;
        public Double query_time;
    }

    public static class Result {
        public Boolean anomaly;
        public Boolean confirmed;
        public Boolean failure;
        public String measurement_id;
        public String measurement_start_time;
        public String measurement_url;
        public String probe_asn;
        public String probe_cc;
        public String report_id;
        public String test_name;
    }

}