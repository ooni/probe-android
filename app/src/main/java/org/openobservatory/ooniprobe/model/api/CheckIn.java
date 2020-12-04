package org.openobservatory.ooniprobe.model.api;

import org.openobservatory.ooniprobe.model.database.Url;

import java.util.List;

public class CheckIn {
    public Tests tests = null;
    public Integer v;

    public class Tests {
        public WebConnectivity web_connectivity;
    }

    public class WebConnectivity {
        public List<Url> urls;
    }
}
