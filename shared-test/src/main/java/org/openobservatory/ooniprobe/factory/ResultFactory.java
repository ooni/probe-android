package org.openobservatory.ooniprobe.factory;

import android.content.Context;

import org.openobservatory.ooniprobe.common.OONIDescriptor;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Tor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;
import org.openobservatory.ooniprobe.utils.models.TestSuiteMeasurements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.bloco.faker.Faker;

import static org.openobservatory.ooniprobe.utils.TestSuiteUtils.populateMeasurements;

public class ResultFactory {

    private static final Faker faker = new Faker();

    private static final int DEFAULT_SUCCESS_MEASUREMENTS = 4;
    private static final int DEFAULT_FAILED_MEASUREMENTS = 0;

    public static Result build(OONIDescriptor descriptor) {
        return build(descriptor, true, true);
    }

    public static Result build(OONIDescriptor descriptor, boolean wasViewed, boolean startNetworkData) {
        Result temp = new Result();
        temp.id = faker.number.positive();
        temp.test_group_name = descriptor.getName();
        temp.data_usage_down = startNetworkData ? faker.number.positive() : 0;
        temp.data_usage_up = startNetworkData ? faker.number.positive() : 0;
        temp.start_time = faker.date.forward();
        temp.is_done = true;
        temp.is_viewed = wasViewed;

        temp.failure_msg = null;
        return temp;
    }

    /**
     * Saves a result in the DB and returns it with the 4 measurements, and all related model
     * objects in the DB.
     *
     * @param suite type of result (ex: Websites, Instant Messaging, Circumvention, Performance)
     * @return result with
     */
    public static Result createAndSave(OONIDescriptor suite) {
        return createAndSave(suite, DEFAULT_SUCCESS_MEASUREMENTS, DEFAULT_FAILED_MEASUREMENTS, true);
    }

    /**
     * Saves a result in the DB and returns it with the given number of measurements, and
     * all related model objects in the DB.
     *
     * @param descriptor                  type of result (ex: Websites, Instant Messaging, Circumvention, Performance)
     * @param accessibleMeasurements number of accessible measurements
     * @param blockedMeasurements    number of blocked measurements
     * @return result with
     * @throws IllegalArgumentException for excess number of measurements
     */
    public static Result createAndSave(
            OONIDescriptor descriptor,
            int accessibleMeasurements,
            int blockedMeasurements
    ) throws IllegalArgumentException {
        return createAndSave(descriptor, accessibleMeasurements, blockedMeasurements, true);
    }


    /**
     * Saves a result in the DB and returns it with the given number of measurements, and
     * all related model objects in the DB.
     *
     * @param descriptor                  type of result (ex: Websites, Instant Messaging, Circumvention, Performance)
     * @param accessibleMeasurements number of accessible measurements
     * @param blockedMeasurements    number of blocked measurements
     * @param measurementsUploaded   if the measurements are uploaded
     * @return result with
     * @throws IllegalArgumentException for excess number of measurements
     */
    public static Result createAndSave(
            OONIDescriptor descriptor,
            int accessibleMeasurements,
            int blockedMeasurements,
            boolean measurementsUploaded
    ) throws IllegalArgumentException {

        List<AbstractTest> measurementTestTypes = new ArrayList<>();

        if (descriptor.getName().equals(OONITests.INSTANT_MESSAGING.getLabel())) {
            measurementTestTypes = Arrays.asList(
                    new FacebookMessenger(),
                    new Telegram(),
                    new Whatsapp(),
                    new Signal()
            );
        }

        if (descriptor.getName().equals(OONITests.CIRCUMVENTION.getLabel())) {
            measurementTestTypes = Arrays.asList(
                    new Psiphon(),
                    new Tor(),
                    new RiseupVPN()
            );
        }

        if (descriptor.getName().equals(OONITests.WEBSITES.getLabel())) {
            for (int i = 0; i < accessibleMeasurements + blockedMeasurements; i++) {
                measurementTestTypes.add(new WebConnectivity());
            }
        }

        if (descriptor.getName().equals(OONITests.PERFORMANCE.getLabel())) {
            measurementTestTypes = Arrays.asList(
                    new Ndt(),
                    new Dash(),
                    new HttpInvalidRequestLine(),
                    new HttpHeaderFieldManipulation()
            );
        }

        TestSuiteMeasurements measurements = populateMeasurements(
                measurementTestTypes,
                accessibleMeasurements,
                blockedMeasurements
        );

        return createAndSave(
                descriptor,
                measurements.getAccessibleTestTypes(),
                measurements.getBlockedTestTypes(),
                measurementsUploaded
        );
    }

    private static Result createAndSave(
            OONIDescriptor descriptor,
            List<AbstractTest> successTestTypes,
            List<AbstractTest> failedTestTypes,
            boolean resultsUploaded
    ) {
        Result tempResult = ResultFactory.build(descriptor);

        Network tempNetwork = NetworkFactory.createAndSave(tempResult);
        tempResult.network = tempNetwork;
        tempNetwork.save();

        successTestTypes.forEach(type -> MeasurementFactory.build(
                type,
                tempResult,
                UrlFactory.createAndSave(),
                false,
                resultsUploaded
        ).save());

        failedTestTypes.forEach(type -> MeasurementFactory.build(
                type,
                tempResult,
                UrlFactory.createAndSave(),
                true,
                resultsUploaded
        ).save());

        tempResult.getMeasurements();
        tempResult.save();

        return tempResult;
    }

    /**
     * Saves a result in the DB and returns it with the given number of measurements, and
     * all related model objects in the DB and files in storage.
     *
     * @param context                to save the entry files
     * @param descriptor                  type of result (ex: Websites, Instant Messaging, Circumvention, Performance)
     * @param accessibleMeasurements number of accessible measurements
     * @param blockedMeasurements    number of blocked measurements
     * @param measurementsUploaded   if the measurements are uploaded
     * @return result with
     * @throws IllegalArgumentException for excess number of measurements
     */
    public static Result createAndSaveWithEntryFiles(
            Context context,
            OONIDescriptor descriptor,
            int accessibleMeasurements,
            int blockedMeasurements,
            boolean measurementsUploaded
    ) throws IllegalArgumentException {
        Result temp = createAndSave(descriptor, accessibleMeasurements, blockedMeasurements, measurementsUploaded);
        MeasurementFactory.addEntryFiles(context, temp.getMeasurements(), false);
        temp.save();

        return temp;
    }
}
