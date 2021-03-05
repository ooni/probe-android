package org.openobservatory.engine;

import oonimkall.URLListConfig;

/** URLListConfig contains configuration for fetching the URL list. */
public final class OONIURLListConfig {

    /*
     * Categories to query for (empty means all)
     */
    String[] categories;

    /**
     * CountryCode is the optional country code
     */
    String countryCode;

    /**
     * Max number of URLs (<= 0 means no limit)
     */
    long limit;

    public OONIURLListConfig() {
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    protected URLListConfig toOonimkallURLListConfig() {
        URLListConfig c = new URLListConfig();
        c.setCountryCode(countryCode);
        c.setLimit(limit);
        for (String cat : categories)
            c.addCategory(cat);
        return c;
    }
}
