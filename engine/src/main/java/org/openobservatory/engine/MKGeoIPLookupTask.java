package org.openobservatory.engine;

final class MKGeoIPLookupTask implements OONIGeoIPLookupTask {
    private io.ooni.mk.MKGeoIPLookupTask task;

    public MKGeoIPLookupTask() {
        task = new io.ooni.mk.MKGeoIPLookupTask();
    }

    @Override
    public void setTimeout(long timeout) {
        task.setTimeout(timeout);
    }

    @Override
    public void setCABundlePath(String path) {
        task.setCABundlePath(path);
    }

    @Override
    public void setCountryDBPath(String path) {
        task.setCountryDBPath(path);
    }

    @Override
    public void setASNDBPath(String path) {
        task.setASNDBPath(path);
    }

    @Override
    public OONIGeoIPLookupResults perform() {
        return new MKGeoIPLookupResults(task.perform());
    }
}
