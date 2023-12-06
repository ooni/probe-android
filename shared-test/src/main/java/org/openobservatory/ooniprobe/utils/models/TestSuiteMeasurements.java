package org.openobservatory.ooniprobe.utils.models;

import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.List;

public class TestSuiteMeasurements {
    List<AbstractTest> accessibleTestTypes;
    List<AbstractTest> blockedTestTypes;

    public TestSuiteMeasurements(List<AbstractTest> accessibleTestTypes, List<AbstractTest> blockedTestTypes) {
        this.accessibleTestTypes = accessibleTestTypes;
        this.blockedTestTypes = blockedTestTypes;
    }

    public List<AbstractTest> getAccessibleTestTypes() {
        return accessibleTestTypes;
    }

    public List<AbstractTest> getBlockedTestTypes() {
        return blockedTestTypes;
    }
}
