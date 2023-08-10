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
import org.openobservatory.ooniprobe.model.database.TestDescriptor_Table;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;

import java.util.List;

public class TestDescriptorManager {
    public static boolean save(TestDescriptor descriptor) {
        return descriptor.save();
    }

    public static List<TestDescriptor> getAll() {
        return SQLite.select().from(TestDescriptor.class).queryList();
    }

    public static TestDescriptor fetchDescriptorFromRunId(long runId, Context context) throws Exception{
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

        return TestDescriptor.Builder.aTestDescriptor()
                .withRunId(runId)
                .withName(descriptor.getName())
                .withNameIntl(descriptor.getNameIntl())
                .withShortDescription(descriptor.getShortDescription())
                .withShortDescriptionIntl(descriptor.getShortDescriptionIntl())
                .withDescription(descriptor.getDescription())
                .withDescriptionIntl(descriptor.getDescriptionIntl())
                .withIcon(descriptor.getIcon())
                .withArchived(response.archived)
                .withAuthor(descriptor.getAuthor())
                .withVersion(response.version)
                .withNettests(descriptor.getNettests())
                .build();
    }

    public static FetchTestDescriptorResponse fetchDataFromRunId(long runId, Context context) throws Exception{
        TestDescriptor testDescriptor = fetchDescriptorFromRunId(runId,context);

        return new FetchTestDescriptorResponse(
                testDescriptor.getTestSuite(context),
                testDescriptor
        );

    }

    public static List<OONIRunSuite> descriptorsWithAutoRunEnabled(Context context){
        List<TestDescriptor> descriptors = TestDescriptor.selectAllAvailable()
                .and(TestDescriptor_Table.auto_run.eq(true))
                .queryList();
        return Lists.transform(descriptors, input -> input.getTestSuite(context));
    }

    public static List<TestDescriptor> descriptorsWithAutoUpdateEnabled(){
        return TestDescriptor.selectAllAvailable()
                .and(TestDescriptor_Table.auto_update.eq(true))
                .queryList();
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
