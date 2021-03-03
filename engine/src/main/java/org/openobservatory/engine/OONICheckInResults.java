package org.openobservatory.engine;

import java.util.ArrayList;

import oonimkall.CheckInInfo;
import oonimkall.CheckInInfoWebConnectivity;
import oonimkall.URLInfo;

public class OONICheckInResults {
    /** CheckInInfo contains the return test objects from the checkin API. */
    public OONICheckInInfoWebConnectivity webConnectivity;

    protected OONICheckInResults(CheckInInfo r) {
        webConnectivity = new OONICheckInInfoWebConnectivity(r.getWebConnectivity());
    }

    public class OONICheckInInfoWebConnectivity {
        public String reportID;
        public ArrayList<OONIURLInfo> urls;

        protected OONICheckInInfoWebConnectivity(CheckInInfoWebConnectivity r) {
            reportID = r.getReportID();
            urls = new ArrayList<>();
            for (int i = 0; i < r.size(); i++) {
                urls.add(new OONIURLInfo(r.at(i)));
            }
        }

        public class OONIURLInfo {
            public String url;
            public String category_code;
            public String country_code;

            protected OONIURLInfo(URLInfo urlInfo) {
                url = urlInfo.getURL();
                category_code = urlInfo.getCategoryCode();
                country_code = urlInfo.getCountryCode();
            }

        }

    }
}
