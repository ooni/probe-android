package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.utils.models.TestSuiteMeasurements;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteUtils {

    /**
     * Saves a result in the DB and returns it with the given number of measurements, and
     * all related model objects in the DB.
     *
     * @param measurementsPool       pool with types os measurements
     * @param accessibleMeasurements number of successful measurements
     * @param blockedMeasurements    number of failed measurements
     *
     * @return TestSuiteMeasurements successful and failed based of the initial pool
     *
     * @throws IllegalArgumentException for excess number of measurements
     */
    public static TestSuiteMeasurements populateMeasurements(
            List<AbstractTest> measurementsPool,
            int accessibleMeasurements,
            int blockedMeasurements
    ) {
        if (measurementsPool == null) {
            throw new IllegalArgumentException("No measurement list was given to pick from.");
        }

        if (accessibleMeasurements + blockedMeasurements > measurementsPool.size()) {
            throw new IllegalArgumentException("Test suite only has "
                    + measurementsPool.size()
                    + " possibilities of measurement, can't run "
                    + (accessibleMeasurements + blockedMeasurements)
            );
        }

        List<AbstractTest> tempAccessible = new ArrayList<>();
        List<AbstractTest> tempBlocked = new ArrayList<>();

        int typeCurrentIndex = 0;
        int maxMeasurements = Math.max(accessibleMeasurements, blockedMeasurements);

        if (maxMeasurements == accessibleMeasurements) {
            for (int i = 0; i < accessibleMeasurements; i++) {
                tempAccessible.add(measurementsPool.get(typeCurrentIndex++));
            }

            for (int i = 0; i < blockedMeasurements; i++) {
                tempBlocked.add(measurementsPool.get(typeCurrentIndex++));
            }
        } else {
            for (int i = 0; i < blockedMeasurements; i++) {
                tempBlocked.add(measurementsPool.get(typeCurrentIndex++));
            }

            for (int i = 0; i < accessibleMeasurements; i++) {
                tempAccessible.add(measurementsPool.get(typeCurrentIndex++));
            }
        }

        return new TestSuiteMeasurements(tempAccessible, tempBlocked);
    }

}
