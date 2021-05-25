package org.openobservatory.ooniprobe.domain;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.domain.models.DatedResults;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
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
        Result result = ResultFactory.createAndSave(new WebsitesSuite());
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
        List<Result> web = getResults.getOrderedByTime(WebsitesSuite.NAME);
        List<Result> messaging = getResults.getOrderedByTime(InstantMessagingSuite.NAME);
        List<Result> circumvention = getResults.getOrderedByTime(CircumventionSuite.NAME);
        List<Result> performance = getResults.getOrderedByTime(PerformanceSuite.NAME);

        // Assert
        assertEquals(all.size(), 4);
        assertEquals(web.size(), 1);
        assertEquals(messaging.size(), 1);
        assertEquals(circumvention.size(), 1);
        assertEquals(performance.size(), 1);
        assertEquals(all.get(0).test_group_name, PerformanceSuite.NAME);
        assertEquals(all.get(1).test_group_name, CircumventionSuite.NAME);
        assertEquals(all.get(2).test_group_name, InstantMessagingSuite.NAME);
        assertEquals(all.get(3).test_group_name, WebsitesSuite.NAME);
    }

    @Test public void groupedByMonth() {
        // Arrange
        createDatedResults();
        GetResults getResults = build();

        // Act
        List<DatedResults> all = getResults.getGroupedByMonth(null);
        List<DatedResults> web = getResults.getGroupedByMonth(WebsitesSuite.NAME);
        List<DatedResults> messaging = getResults.getGroupedByMonth(InstantMessagingSuite.NAME);
        List<DatedResults> circumvention = getResults.getGroupedByMonth(CircumventionSuite.NAME);
        List<DatedResults> performance = getResults.getGroupedByMonth(PerformanceSuite.NAME);

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

        assertEquals(all.get(0).getResultsList().get(0).test_group_name, PerformanceSuite.NAME);
        assertEquals(all.get(1).getResultsList().get(0).test_group_name, CircumventionSuite.NAME);
        assertEquals(all.get(2).getResultsList().get(0).test_group_name, InstantMessagingSuite.NAME);
        assertEquals(all.get(3).getResultsList().get(0).test_group_name, WebsitesSuite.NAME);
    }


    private void createDatedResults() {
        Result websites = ResultFactory.createAndSave(new WebsitesSuite());
        websites.start_time = getDateFrom(1, Calendar.JANUARY, 2020);
        websites.save();

        Result messaging = ResultFactory.createAndSave(new InstantMessagingSuite());
        messaging.start_time = getDateFrom(1, Calendar.FEBRUARY, 2020);
        messaging.save();

        Result circumvention = ResultFactory.createAndSave(new CircumventionSuite(), 1, 2);
        circumvention.start_time = getDateFrom(1, Calendar.MARCH, 2020);
        circumvention.save();

        Result performance = ResultFactory.createAndSave(new PerformanceSuite());
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