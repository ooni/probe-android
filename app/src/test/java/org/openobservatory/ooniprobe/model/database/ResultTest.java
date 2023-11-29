package org.openobservatory.ooniprobe.model.database;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ResultTest extends RobolectricAbstractTest {

    @Test
    public void constructor() {
        Result result = new Result("circumvention");

        assertEquals("circumvention", result.test_group_name);
        assertTrue(System.currentTimeMillis() - result.start_time.getTime() < 100);
    }

    @Test
    public void readableFileSize() {
        assertEquals("0", Result.readableFileSize(0));
        assertEquals("1 kB", Result.readableFileSize(1));
        assertEquals("1 MB", Result.readableFileSize(1024));
        assertEquals("1.1 MB", Result.readableFileSize(1024 + 102));
    }

    @Test
    public void getLastResult() {
        Result first = new Result("");
        first.save();
        Result second = new Result("");
        second.save();

        Result lastResult = Result.getLastResult();

        assertEquals(second.id, lastResult.id);
    }

    @Test
    public void getLastResult_withName() {
        Result first = new Result("group1");
        Result second = new Result("group2");
        first.save();
        second.save();

        Result lastResult = Result.getLastResult("group1");

        assertEquals(first.id, lastResult.id);
    }

    @Test
    public void deleteAll() {
        Result result = new Result();
        result.save();
        Measurement measurement = new Measurement(result, "");
        measurement.save();

        assertTrue(result.exists());
        assertTrue(measurement.exists());

        Result.deleteAll(c);

        assertFalse(result.exists());
        assertFalse(measurement.exists());
    }

    @Test
    public void getMeasurements() {
        Result result = new Result();
        result.save();
        Measurement measurementDone = new Measurement(result, "");
        measurementDone.is_done = true;
        measurementDone.save();
        Measurement measurementNotDone = new Measurement(result, "");
        measurementNotDone.is_done = false;
        measurementNotDone.save();

        List<Measurement> measurements = result.getMeasurements();

        assertEquals(1, measurements.size());
        assertEquals(measurementDone.id, measurements.get(0).id);
    }

    @Test
    public void getMeasurementsSorted_websites() {
        Result result = new Result(OONITests.WEBSITES.getLabel());
        result.save();
        Measurement measurementAnomaly = new Measurement(result, "a");
        measurementAnomaly.is_done = true;
        measurementAnomaly.is_anomaly = true;
        measurementAnomaly.save();
        Measurement measurementOk = new Measurement(result, "c");
        measurementOk.is_done = true;
        measurementOk.save();

        List<Measurement> measurements = result.getMeasurementsSorted();

        assertEquals(2, measurements.size());
        assertEquals(measurementAnomaly.id, measurements.get(0).id);
        assertEquals(measurementOk.id, measurements.get(1).id);
    }

    @Test
    public void getMeasurementsSorted_instantMessaging() {
        Result result = new Result(OONITests.INSTANT_MESSAGING.getLabel());
        result.save();
        Measurement signal = new Measurement(result, Signal.NAME);
        signal.is_done = true;
        signal.save();
        Measurement whatsapp = new Measurement(result, Whatsapp.NAME);
        whatsapp.is_done = true;
        whatsapp.save();

        List<Measurement> measurements = result.getMeasurementsSorted();

        assertEquals(2, measurements.size());
        assertEquals(Whatsapp.NAME, measurements.get(0).test_name);
        assertEquals(Signal.NAME, measurements.get(1).test_name);
    }

    @Test
    public void getMeasurementsSorted_performance() {
        Result result = new Result(OONITests.PERFORMANCE.getLabel());
        result.save();
        Measurement httpHeader = new Measurement(result, HttpHeaderFieldManipulation.NAME);
        httpHeader.is_done = true;
        httpHeader.save();
        Measurement ndt = new Measurement(result, Ndt.NAME);
        ndt.is_done = true;
        ndt.save();

        List<Measurement> measurements = result.getMeasurementsSorted();

        assertEquals(2, measurements.size());
        assertEquals(Ndt.NAME, measurements.get(0).test_name);
        assertEquals(HttpHeaderFieldManipulation.NAME, measurements.get(1).test_name);
    }

    @Test
    public void getMeasurement() {
        Result result = new Result();
        result.save();
        Measurement measurement = new Measurement(result, "test");
        measurement.save();

        assertEquals(measurement.id, result.getMeasurement(measurement.test_name).id);
    }

    @Test
    public void countMeasurements() {
        Result result = new Result();
        result.save();
        Measurement done = new Measurement(result, "");
        done.is_done = true;
        done.save();
        Measurement doneAnomaly = new Measurement(result, "");
        doneAnomaly.is_done = true;
        doneAnomaly.is_anomaly = true;
        doneAnomaly.save();
        Measurement notDone = new Measurement(result, "");
        notDone.is_done = false;
        notDone.save();

        assertEquals(2, result.countTotalMeasurements());
        assertEquals(2, result.countCompletedMeasurements());
        assertEquals(1, result.countOkMeasurements());
        assertEquals(1, result.countAnomalousMeasurements());
    }

    @Test
    public void getRuntime() {
        Result result = new Result("");
        result.save();
        Measurement first = new Measurement(result, "first");
        first.start_time = new Date(0);
        first.save();
        Measurement last = new Measurement(result, "second");
        last.start_time = new Date(1000);
        last.runtime = 3; // seconds
        last.save();

        assertEquals(4, result.getRuntime(), 0.1);
    }

    @Test
    public void getFormattedDataUsageUp() {
        Result result = new Result();
        result.data_usage_up = 1;
        assertEquals("1 kB", result.getFormattedDataUsageUp());
    }

    @Test
    public void getFormattedDataUsageDown() {
        Result result = new Result();
        result.data_usage_down = 1;
        assertEquals("1 kB", result.getFormattedDataUsageDown());
    }

    @Test
    public void getTestSuite() {
        assertEquals(OONITests.WEBSITES.getLabel(), new Result(OONITests.WEBSITES.getLabel()).getTestSuite(c).getName());
        assertEquals(OONITests.INSTANT_MESSAGING.getLabel(), new Result(OONITests.INSTANT_MESSAGING.getLabel()).getTestSuite(c).getName());
        assertEquals(OONITests.PERFORMANCE.getLabel(), new Result(OONITests.PERFORMANCE.getLabel()).getTestSuite(c).getName());
        assertEquals(OONITests.CIRCUMVENTION.getLabel(), new Result(OONITests.CIRCUMVENTION.getLabel()).getTestSuite(c).getName());
        assertEquals(OONITests.EXPERIMENTAL.getLabel(), new Result(OONITests.EXPERIMENTAL.getLabel()).getTestSuite(c).getName());
        assertEquals(new Result("invalid").getTestSuite(c).getName(),"invalid");
    }

    @Test
    public void delete() {
        Network network = new Network();
        network.save();
        Result result = new Result();
        result.network = network;
        result.save();
        Measurement measurement = new Measurement(result, "");
        measurement.save();

        result.delete(c);

        assertFalse(network.exists());
        assertFalse(result.exists());
        assertFalse(measurement.exists());
    }
}
