package org.openobservatory.engine;

import java.util.ArrayList;

import oonimkall.URLListResult;

/** URLListResult contains the URLs returned from the FetchURL API. */
public class OONIURLListResult {
    public ArrayList<OONIURLInfo> urls;

    protected OONIURLListResult(URLListResult r) {
        urls = new ArrayList<>();
        for (int i = 0; i < r.size(); i++) {
            urls.add(new OONIURLInfo(r.at(i)));
        }
    }
}
