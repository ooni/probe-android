package org.openobservatory.engine;

class MKGeoIPLookupTaskAdapter implements GeoIPLookupTask {
    private io.ooni.mk.MKGeoIPLookupTask task;

    public MKGeoIPLookupTaskAdapter() {
        task = new io.ooni.mk.MKGeoIPLookupTask();
    }

    public void setTimeout(long timeout) {
        task.setTimeout(timeout);
    }

    public void setCABundlePath(String path) {
        task.setCABundlePath(path);
    }

    public void setCountryDBPath(String path) {
        task.setCountryDBPath(path);
    }

    public void setASNDBPath(String path) {
        task.setASNDBPath(path);
    }

    public GeoIPLookupResults perform() {
        return new MKGeoIPLookupResultsAdapter(task.perform());
    }
}
