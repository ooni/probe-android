package org.openobservatory.ooniprobe.domain;

import android.content.Context;

import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONIRunDescriptor;
import org.openobservatory.engine.OONIRunFetchResponse;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.List;

public class TestDescriptorManager {
    public static boolean save(TestDescriptor descriptor) {
        return descriptor.save();
    }

    public static List<TestDescriptor> getAll() {
        return SQLite.select().from(TestDescriptor.class).queryList();
    }

    public static FetchTestDescriptorResponse fetchDataFromRunId(long runId, Context context) throws Exception{
        OONISession session = EngineProvider.get().newSession(
                EngineProvider.get().getDefaultSessionConfig(
                        context,
                        BuildConfig.SOFTWARE_NAME,
                        BuildConfig.VERSION_NAME,
                        new LoggerArray(),
                        ((Application)context.getApplicationContext()).getPreferenceManager().getProxyURL()
                )
        );
        OONIContext ooniContext = session.newContextWithTimeout(300);

        OONIRunFetchResponse response = session.ooniRunFetch(ooniContext, runId);
        OONIRunDescriptor descriptor = response.descriptor;

        List<AbstractTest> tests = Lists.transform(
                descriptor.getNettests(),
                nettest -> {
                    AbstractTest test = AbstractTest.getTestByName(nettest.getName());
                    if (nettest.getName().equals(WebConnectivity.NAME)){
                        for (String url : nettest.getInputs())
                            Url.checkExistingUrl(url);
                    }
                    test.setInputs(nettest.getInputs());
                    return test;
                }
        );
        TestDescriptor testDescriptor = TestDescriptor.Builder.aTestDescriptor()
                .withRunId(runId)
                .withName(descriptor.getName())
                .withNameIntl(descriptor.getNameIntl())
                .withShortDescription(descriptor.getShortDescription())
                .withDescription(descriptor.getDescription())
                .withDescriptionIntl(descriptor.getDescriptionIntl().toString())
                .withIcon(descriptor.getIcon())
                .withArchived(descriptor.getArchived())
                .withAuthor(descriptor.getAuthor())
                .withNettests(descriptor.getNettests())
                .build();

        return new FetchTestDescriptorResponse(
                new OONIRunSuite(
                        testDescriptor,
                        tests.toArray(new AbstractTest[0])
                ),
                testDescriptor
        );

    }

    public static class FetchTestDescriptorResponse {
        public OONIRunSuite suite;
        public TestDescriptor descriptor;

        public FetchTestDescriptorResponse(OONIRunSuite suite, TestDescriptor descriptor) {
            this.suite = suite;
            this.descriptor = descriptor;
        }
    }
}
