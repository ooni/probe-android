package org.openobservatory.ooniprobe.domain;

import android.content.Context;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.factory.MeasurementFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasUploadabledReportsTest extends RobolectricAbstractTest {

    @Override
    public void setUp() {
        super.setUp();
        DatabaseUtils.resetDatabase();
    }

    @Test
    public void testUploadableReports() {
        // Arrange
        HasUploadabledReports uploadabledReports = build(c);
        Result testResult = ResultFactory.createAndSave(new WebsitesSuite(), 5, 0, false);
        MeasurementFactory.addEntryFiles(c, testResult.getMeasurements(), false);
        testResult.save();

        // Act
        boolean hasReports = uploadabledReports.hasUploadables();

        // Assert
        assertTrue(hasReports);
    }

    @Test
    public void testNoUploadableReports() {
        // Arrange
        HasUploadabledReports uploadabledReports = build(c);
        ResultFactory.createAndSave(new WebsitesSuite());

        // Act
        boolean hasReports = uploadabledReports.hasUploadables();

        // Assert
        assertFalse(hasReports);
    }

    private HasUploadabledReports build(Context c) {
        return new HasUploadabledReports(c);
    }

}