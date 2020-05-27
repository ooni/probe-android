package org.openobservatory.engine;

import java.util.Vector;

class MKOrchestraTaskAdapter implements OrchestraTask {
    private io.ooni.mk.MKOrchestraTask task;

    public MKOrchestraTaskAdapter(String softwareName, String softwareVersion,
                                  Vector<String> supportedTests,
                                  String deviceToken, String secretsFile) {
        task = new io.ooni.mk.MKOrchestraTask(softwareName, softwareVersion, supportedTests,
                deviceToken, secretsFile);
    }

    public void setAvailableBandwidth(String value) {
        task.setAvailableBandwidth(value);
    }

    public void setCABundlePath(String value) {
        task.setCABundlePath(value);
    }

    public void setGeoIPCountryPath(String value) {
        task.setGeoIPCountryPath(value);
    }

    public void setGeoIPASNPath(String value) {
        task.setGeoIPASNPath(value);
    }

    public void setLanguage(String value) {
        task.setLanguage(value);
    }

    public void setNetworkType(String value) {
        task.setNetworkType(value);
    }

    public void setPlatform(String value) {
        task.setPlatform(value);
    }

    public void setProbeASN(String value) {
        task.setProbeASN(value);
    }

    public void setProbeCC(String value) {
        task.setProbeCC(value);
    }

    public void setProbeFamily(String value) {
        task.setProbeFamily(value);
    }

    public void setProbeTimezone(String value) {
        task.setProbeTimezone(value);
    }

    public void setRegistryURL(String value) {
        task.setRegistryURL(value);
    }

    public void setTimeout(long value) {
        task.setTimeout(value);
    }

    public OrchestraResults updateOrRegister() {
        return new MKOrchestraResultsAdapter(task.updateOrRegister());
    }
}
