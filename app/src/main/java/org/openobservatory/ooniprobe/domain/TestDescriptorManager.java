package org.openobservatory.ooniprobe.domain;

import android.content.res.Resources;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;

import java.util.ArrayList;
import java.util.List;

public class TestDescriptorManager {

    public static void populateDescriptorData(DatabaseWrapper database) {

        try {
            database.beginTransaction();
            List<TestDescriptor> testDescriptors = defaultDescriptors();

            for (int i = 0; i < testDescriptors.size(); i++) {
                TestDescriptor descriptor = testDescriptors.get(i);
                descriptor.save(database);
            }

            database.setTransactionSuccessful();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    public static List<TestDescriptor> defaultDescriptors() {
        Resources res = FlowManager.getContext().getResources();

        List<TestDescriptor> testDescriptors = new ArrayList<>();
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName(res.getString(R.string.Test_Websites_Fullname))
                .withShortDescription(res.getString(R.string.Dashboard_Websites_Card_Description))
                .withDescription(res.getString(R.string.Dashboard_Websites_Overview_Paragraph))
                .withIcon("test_websites")
                .withDataUsage(res.getString(R.string.websites_datausage))
                .withAuthor("OONI")
                .build());
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName(res.getString(R.string.Test_InstantMessaging_Fullname))
                .withShortDescription(res.getString(R.string.Dashboard_InstantMessaging_Card_Description))
                .withDescription(res.getString(R.string.Dashboard_InstantMessaging_Overview_Paragraph))
                .withIcon("test_instant_messaging")
                .withDataUsage(res.getString(R.string.small_datausage))
                .withAuthor("OONI")
                .build());
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName(res.getString(R.string.Test_Circumvention_Fullname))
                .withShortDescription(res.getString(R.string.Dashboard_Circumvention_Card_Description))
                .withDescription(res.getString(R.string.Dashboard_Circumvention_Overview_Paragraph))
                .withDataUsage(res.getString(R.string.small_datausage))
                .withIcon("test_circumvention")
                .withAuthor("OONI")
                .build());
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName(res.getString(R.string.Test_Performance_Fullname))
                .withShortDescription(res.getString(R.string.Dashboard_Performance_Card_Description))
                .withDescription(res.getString(R.string.Dashboard_Performance_Overview_Paragraph_Updated))
                .withDataUsage(res.getString(R.string.performance_datausage))
                .withIcon("test_performance")
                .withAuthor("OONI")
                .build());
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName(res.getString(R.string.Test_Experimental_Fullname))
                .withShortDescription(res.getString(R.string.Dashboard_Experimental_Card_Description))
                .withDescription(res.getString(R.string.Dashboard_Experimental_Overview_Paragraph))
                .withDataUsage(res.getString(R.string.TestResults_NotAvailable))
                .withIcon("test_experimental")
                .withAuthor("OONI")
                .build());
        testDescriptors.add(TestDescriptor.Builder.aTestDescriptor()
                .withName("OONI Run Link Descriptor")
                .withShortDescription("Censorship measurement for some event in country x")
                .withDescription(res.getString(R.string.Dashboard_Experimental_Overview_Paragraph))
                .withDataUsage(res.getString(R.string.TestResults_NotAvailable))
                .withIcon("settings_icon")
                .withAuthor("Censorship coordinator")
                .build());

        return testDescriptors;
    }
}
