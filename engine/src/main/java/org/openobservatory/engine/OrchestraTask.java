package org.openobservatory.engine;

/** OrchestraTask is a task for interacting with the OONI orchestra */
@Deprecated
public interface OrchestraTask {
    /** setAvailableBandwidth sets the bandwidth that a probe is
     * available to commit to running measurements. */
    public void setAvailableBandwidth(String value);

    /** setCABundlePath sets the path of the CA bundle to use. */
    public void setCABundlePath(String value);

    /** setGeoIPCountryPath sets the path of the MaxMind country database. */
    public void setGeoIPCountryPath(String value);

    /** setGeoIPASNPath sets the path of the MaxMind ASN database. */
    public void setGeoIPASNPath(String value);

    /** setLanguage sets the device's language. */
    public void setLanguage(String value);

    /** setNetworkType sets the current network type. */
    public void setNetworkType(String value);

    /** setPlatform sets the device's platform */
    public void setPlatform(String value);

    /** setProbeASN sets the ASN in which we are. */
    public void setProbeASN(String value);

    /** setProbeCC sets the country code in which we are. */
    public void setProbeCC(String value);

    /** setProbeFamily sets an identifier for a group of probes. */
    public void setProbeFamily(String value);

    /** setProbeTimezone sets the timezone of the probe. */
    public void setProbeTimezone(String value);

    /** setRegistryURL sets the base URL to contact the registry. */
    public void setRegistryURL(String value);

    /** setTimeout sets the number of seconds after which
     * outstanding requests are aborted. */
    public void setTimeout(long value);

    /** updateOrRegister will either update the status of the probe with
     * the registry, or register the probe with the registry, depending
     * on whether we did already register or not. */
    public OrchestraResults updateOrRegister();
}
