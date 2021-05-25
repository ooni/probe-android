package org.openobservatory.engine;

import oonimkall.URLInfo;

public class OONIURLInfo {
    private final String url;
    private final String category_code;
    private final String country_code;

    protected OONIURLInfo(URLInfo urlInfo) {
        url = urlInfo.getURL();
        category_code = urlInfo.getCategoryCode();
        country_code = urlInfo.getCountryCode();
    }

    public String getUrl() {
        return url;
    }

    public String getCategoryCode() {
        return category_code;
    }

    public String getCountryCode() {
        return country_code;
    }

}