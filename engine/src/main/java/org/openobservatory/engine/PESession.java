package org.openobservatory.engine;

import oonimkall.Oonimkall;
import oonimkall.Session;

final class PESession implements OONISession {
    private Session session;

    public PESession(OONISessionConfig config) throws OONIException {
        try {
            session = Oonimkall.newSession(config.toOonimkallSessionConfig());
        } catch (Exception exc) {
            throw new OONIException("Oonimkall.newSession failed", exc);
        }
    }

    @Override
    public OONIGeolocateTask newGeolocateTask(long timeout) throws OONIException {
        try {
            return new PEGeolocateTask(session.newGeolocateTask(timeout));
        } catch (Exception exc) {
            throw new OONIException("session.newGeolocateTask failed", exc);
        }
    }

    @Override
    public OONIProbeServicesClient newProbeServicesClient(long timeout) throws OONIException {
        try {
            return new PEProbeServicesClient(session.newMakeSubmitterTask(timeout));
        } catch (Exception exc) {
            throw new OONIException("session.newMakeSubmitterTask failed", exc);
        }
    }

    @Override
    public void close() throws Exception {
        session.close();
    }
}
