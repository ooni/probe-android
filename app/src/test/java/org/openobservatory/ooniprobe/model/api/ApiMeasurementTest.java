package org.openobservatory.ooniprobe.model.api;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;

public class ApiMeasurementTest extends TestCase {

    public void testHasResultWithMeasurementsTest() {
        // Arrange
        ApiMeasurement apiMeasurement = new ApiMeasurement();
        ApiMeasurement.Result apiResult = new ApiMeasurement.Result();
        apiResult.measurement_url = "https://www.example.org";
        apiMeasurement.results = Collections.singletonList(apiResult);

        // Act / Assert
        assertTrue(apiMeasurement.hasResultWithMeasurements());
    }

    public void testHasNoResultWithMeasurementsTest() {
        // Arrange
        ApiMeasurement apiMeasurement = new ApiMeasurement();
        ApiMeasurement.Result apiResult = new ApiMeasurement.Result();
        apiMeasurement.results = Collections.singletonList(apiResult);

        // Act / Assert
        assertFalse(apiMeasurement.hasResultWithMeasurements());
    }

    public void testFirstResultTest() {
        // Arrange
        ApiMeasurement apiMeasurement = new ApiMeasurement();
        ApiMeasurement.Result apiResult1 = new ApiMeasurement.Result();
        apiResult1.measurement_url = "https://www.example.org";
        ApiMeasurement.Result apiResult2 = new ApiMeasurement.Result();
        apiResult2.measurement_url = "https://www.example.org";
        apiMeasurement.results = Arrays.asList(apiResult1, apiResult2);

        // Act
        ApiMeasurement.Result value = apiMeasurement.firstResult();

        // Assert
        assertEquals(apiResult1.measurement_url, value.measurement_url);
    }
}