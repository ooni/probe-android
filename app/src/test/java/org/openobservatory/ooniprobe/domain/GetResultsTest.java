package org.openobservatory.ooniprobe.domain;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.domain.models.DatedResults;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetResultsTest extends RobolectricAbstractTest {

    @Override
    public void setUp() {
        super.setUp();
        DatabaseUtils.resetDatabase();
    }

    @Test
    public void getterTest() {
        // Arrange
        Result result = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c).getTest(c));
        GetResults getResults = build();

        // Act
        Result value = getResults.get(result.id);

        // Assert
        assertNotNull(value);
        assertEquals(result.id, value.id);
        assertEquals(result.test_group_name, value.test_group_name);
        assertEquals(result.getMeasurements().size(), value.getMeasurements().size());
    }

    @Test public void orderedByTimeTest() {
        // Arrange
        createDatedResults();
        GetResults getResults = build();

        // Act
        List<Result> all = getResults.getOrderedByTime(null);
        List<Result> web = getResults.getOrderedByTime(OONITests.WEBSITES.getLabel());
        List<Result> messaging = getResults.getOrderedByTime(OONITests.INSTANT_MESSAGING.getLabel());
        List<Result> circumvention = getResults.getOrderedByTime(OONITests.CIRCUMVENTION.getLabel());
        List<Result> performance = getResults.getOrderedByTime(OONITests.PERFORMANCE.getLabel());

        // Assert
        assertEquals(all.size(), 4);
        assertEquals(web.size(), 1);
        assertEquals(messaging.size(), 1);
        assertEquals(circumvention.size(), 1);
        assertEquals(performance.size(), 1);
        assertEquals(all.get(0).test_group_name, OONITests.PERFORMANCE.getLabel());
        assertEquals(all.get(1).test_group_name, OONITests.CIRCUMVENTION.getLabel());
        assertEquals(all.get(2).test_group_name, OONITests.INSTANT_MESSAGING.getLabel());
        assertEquals(all.get(3).test_group_name, OONITests.INSTANT_MESSAGING.getLabel());
    }

    @Test public void groupedByMonth() {
        // Arrange
        createDatedResults();
        GetResults getResults = build();

        // Act
        List<DatedResults> all = getResults.getGroupedByMonth(null);
        List<DatedResults> web = getResults.getGroupedByMonth(OONITests.WEBSITES.getLabel());
        List<DatedResults> messaging = getResults.getGroupedByMonth(OONITests.INSTANT_MESSAGING.getLabel());
        List<DatedResults> circumvention = getResults.getGroupedByMonth(OONITests.CIRCUMVENTION.getLabel());
        List<DatedResults> performance = getResults.getGroupedByMonth(OONITests.PERFORMANCE.getLabel());

        // Assert
        assertEquals(all.size(), 4);
        assertEquals(web.size(), 1);
        assertEquals(messaging.size(), 1);
        assertEquals(circumvention.size(), 1);
        assertEquals(performance.size(), 1);

        assertEquals(all.get(0).getGroupedDate(), getDateFrom(1, Calendar.APRIL, 2020) );
        assertEquals(all.get(1).getGroupedDate(), getDateFrom(1, Calendar.MARCH, 2020) );
        assertEquals(all.get(2).getGroupedDate(), getDateFrom(1, Calendar.FEBRUARY, 2020) );
        assertEquals(all.get(3).getGroupedDate(), getDateFrom(1, Calendar.JANUARY, 2020) );

        assertEquals(all.get(0).getResultsList().size(), 1);
        assertEquals(all.get(1).getResultsList().size(), 1);
        assertEquals(all.get(2).getResultsList().size(), 1);
        assertEquals(all.get(3).getResultsList().size(), 1);

        assertEquals(all.get(0).getResultsList().get(0).test_group_name, OONITests.PERFORMANCE.getLabel());
        assertEquals(all.get(1).getResultsList().get(0).test_group_name, OONITests.CIRCUMVENTION.getLabel());
        assertEquals(all.get(2).getResultsList().get(0).test_group_name, OONITests.INSTANT_MESSAGING.getLabel());
        assertEquals(all.get(3).getResultsList().get(0).test_group_name, OONITests.WEBSITES.getLabel());
    }


    private void createDatedResults() {
        Result websites = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c).getTest(c));
        websites.start_time = getDateFrom(1, Calendar.JANUARY, 2020);
        websites.save();

        Result messaging = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c).getTest(c));
        messaging.start_time = getDateFrom(1, Calendar.FEBRUARY, 2020);
        messaging.save();

        Result circumvention = ResultFactory.createAndSave(OONITests.CIRCUMVENTION.toOONIDescriptor(c).getTest(c), 1, 2);
        circumvention.start_time = getDateFrom(1, Calendar.MARCH, 2020);
        circumvention.save();

        Result performance = ResultFactory.createAndSave(OONITests.PERFORMANCE.toOONIDescriptor(c).getTest(c));
        performance.start_time = getDateFrom(1, Calendar.APRIL, 2020);
        performance.save();
    }

    public GetResults build() {
        return new GetResults();
    }

    public Date getDateFrom(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTime();
    }
}