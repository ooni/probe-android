package org.openobservatory.engine;

import java.util.ArrayList;

import oonimkall.CheckInInfo;
import oonimkall.CheckInInfoWebConnectivity;

public class OONICheckInResults {
    /** CheckInInfo contains the return test objects from the checkin API. */
    private final OONICheckInInfoWebConnectivity webConnectivity;

    protected OONICheckInResults(CheckInInfo r) {
        webConnectivity = new OONICheckInInfoWebConnectivity(r.getWebConnectivity());
    }

    public OONICheckInInfoWebConnectivity getWebConnectivity() {
        return webConnectivity;
    }

    public class OONICheckInInfoWebConnectivity {
        private final String reportID;
        private final ArrayList<OONIURLInfo> urls;

        protected OONICheckInInfoWebConnectivity(CheckInInfoWebConnectivity r) {
            reportID = r.getReportID();
            urls = new ArrayList<>();
            for (int i = 0; i < r.size(); i++) {
                urls.add(new OONIURLInfo(r.at(i)));
            }
        }

        public String getReportID() {
            return reportID;
        }

        public ArrayList<OONIURLInfo> getUrls() {
            return urls;
        }
    }
}
