package org.openobservatory.engine;

import oonimkall.URLInfo;

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