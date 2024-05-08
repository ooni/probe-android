package org.openobservatory.ooniprobe.ui.resultdetails;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.flaky.AllowFlaky;
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.utils.FormattingUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.openobservatory.ooniprobe.ui.utils.RecyclerViewMatcher.withRecyclerView;
import static org.openobservatory.ooniprobe.ui.utils.ViewMatchers.waitId;
import static org.openobservatory.ooniprobe.ui.utils.ViewMatchers.waitPartialText;

@RunWith(AndroidJUnit4.class)
public class TestResultsMainScreenTest extends MeasurementAbstractTest {

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

    @Test
    @AllowFlaky(attempts = 3)
    public void testHeaderData() {
        // Arrange
        Result websites = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));
        Result messaging = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));
        Result circumvention = ResultFactory.createAndSave(OONITests.CIRCUMVENTION.toOONIDescriptor(c), 1, 2);
        Result performance = ResultFactory.createAndSave(OONITests.PERFORMANCE.toOONIDescriptor(c));

        long totalDownload = websites.data_usage_down +
                messaging.data_usage_down +
                circumvention.data_usage_down +
                performance.data_usage_down;

        long totalUpload = websites.data_usage_up +
                messaging.data_usage_up +
                circumvention.data_usage_up +
                performance.data_usage_up;

        // Act
        launchResults();

        // Assert
        onView(withId(R.id.tests)).check(matches(withText("4")));
        onView(withId(R.id.networks)).check(matches(withText("4")));
        onView(withText(Result.readableFileSize(totalDownload))).check(matches(isDisplayed()));
        onView(withText(Result.readableFileSize(totalUpload))).check(matches(isDisplayed()));
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void testListOfResults() {
        // Arrange
        Result websites = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));
        websites.start_time = getDateFrom(1, Calendar.JANUARY, 2020);
        websites.save();

        Result messaging = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));
        messaging.start_time = getDateFrom(1, Calendar.FEBRUARY, 2020);
        messaging.save();

        Result circumvention = ResultFactory.createAndSave(OONITests.CIRCUMVENTION.toOONIDescriptor(c), 1, 2);
        circumvention.start_time = getDateFrom(1, Calendar.MARCH, 2020);
        circumvention.save();

        Result performance = ResultFactory.createAndSave(OONITests.PERFORMANCE.toOONIDescriptor(c));
        performance.start_time = getDateFrom(1, Calendar.APRIL, 2020);
        performance.save();

        TestKeys performanceTestKeys = performance.getMeasurement(Ndt.NAME).getTestKeys();
        String download = FormattingUtils.getDownload(performanceTestKeys.protocol, performanceTestKeys.summary, performanceTestKeys.simple);
        String upload = FormattingUtils.getUpload(performanceTestKeys.protocol, performanceTestKeys.summary, performanceTestKeys.simple);
        String quality = getResourceString(performance.getMeasurement(Dash.NAME).getTestKeys().getVideoQuality(false));

        // Act
        launchResults();

        // Assert
            // Performance
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.textView))
                .check(matches(withText("APRIL 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.download))
                .check(matches(withText(containsString(download))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.upload))
                .check(matches(withText(containsString(upload))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.quality))
                .check(matches(withText(quality)));

            // Circumvention
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(2));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(2, R.id.textView))
                .check(matches(withText("MARCH 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(3));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(3, R.id.failedMeasurements))
                .check(matches(withText(containsString("2"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(3, R.id.okMeasurements))
                .check(matches(withText(containsString("1"))));

            // Instant Messaging
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(4));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(4, R.id.textView))
                .check(matches(withText("FEBRUARY 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(5));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(5, R.id.failedMeasurements))
                .check(matches(withText(containsString("0"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(5, R.id.okMeasurements))
                .check(matches(withText(containsString("4"))));

        // Websites
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(6));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(6, R.id.textView))
                .check(matches(withText("JANUARY 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(7));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(7, R.id.failedMeasurements))
                .check(matches(withText(containsString("0"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(7, R.id.testedMeasurements))
                .check(matches(withText(containsString("4"))));

    }

    @Test
    @AllowFlaky(attempts = 3)
    public void deleteResultsTest() {
        // Arrange
        ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));
        ResultFactory.createAndSave(OONITests.PERFORMANCE.toOONIDescriptor(c));

        // Act
        launchResults();
        onView(withId(R.id.delete)).perform(click());
        onView(isRoot()).perform(waitPartialText("delete", TimeUnit.SECONDS.toMillis(3)));
        onView(withText(getResourceString(R.string.Modal_Delete))).perform(click());

        // Assert
        onView(isRoot()).perform(waitId(R.id.tests, TimeUnit.SECONDS.toMillis(3)));
        onView(withId(R.id.tests)).check(matches(withText("4")));
        onView(withId(R.id.networks)).check(matches(withText("1")));
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void filterTest() {
        // Arrange
        Result websites = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));
        websites.start_time = getDateFrom(1, Calendar.JANUARY, 2020);
        websites.save();

        Result messaging = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));
        messaging.start_time = getDateFrom(1, Calendar.FEBRUARY, 2020);
        messaging.save();

        Result circumvention = ResultFactory.createAndSave(OONITests.CIRCUMVENTION.toOONIDescriptor(c), 1, 2);
        circumvention.start_time = getDateFrom(1, Calendar.MARCH, 2020);
        circumvention.save();

        Result performance = ResultFactory.createAndSave(OONITests.PERFORMANCE.toOONIDescriptor(c));
        performance.start_time = getDateFrom(1, Calendar.APRIL, 2020);
        performance.save();


        TestKeys performanceTestKeys = performance.getMeasurement(Ndt.NAME).getTestKeys();
        String download = FormattingUtils.getDownload(performanceTestKeys.protocol, performanceTestKeys.summary, performanceTestKeys.simple);
        String upload = FormattingUtils.getUpload(performanceTestKeys.protocol, performanceTestKeys.summary, performanceTestKeys.simple);
        String quality = getResourceString(performance.getMeasurement(Dash.NAME).getTestKeys().getVideoQuality(false));

        // Act
        launchResults();

        // Assert
            // Performance
        onView(withId(R.id.filterTests)).perform(click());
        onData(anything()).atPosition(4).perform(click());

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.textView))
                .check(matches(withText("APRIL 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.download))
                .check(matches(withText(containsString(download))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.upload))
                .check(matches(withText(containsString(upload))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.quality))
                .check(matches(withText(quality)));

        // Circumvention
        onView(withId(R.id.filterTests)).perform(click());
        onData(anything()).atPosition(3).perform(click());
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.textView))
                .check(matches(withText("MARCH 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.failedMeasurements))
                .check(matches(withText(containsString("2"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.okMeasurements))
                .check(matches(withText(containsString("1"))));

        // Instant Messaging
        onView(withId(R.id.filterTests)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.textView))
                .check(matches(withText("FEBRUARY 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.failedMeasurements))
                .check(matches(withText(containsString("0"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.okMeasurements))
                .check(matches(withText(containsString("4"))));

        // Websites
        onView(withId(R.id.filterTests)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.textView))
                .check(matches(withText("JANUARY 2020")));

        onView(withId(R.id.recycler))
                .perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.failedMeasurements))
                .check(matches(withText(containsString("0"))));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(1, R.id.testedMeasurements))
                .check(matches(withText(containsString("4"))));

    }

    private Date getDateFrom(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTime();
    }

}
