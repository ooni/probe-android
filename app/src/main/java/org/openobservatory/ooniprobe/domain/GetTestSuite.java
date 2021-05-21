package org.openobservatory.ooniprobe.domain;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;

import javax.inject.Inject;

public class GetTestSuite {

    private final Application application;

    @Inject
    GetTestSuite(Application application) {
        this.application = application;
    }

    public AbstractSuite get(String testName, @Nullable Attribute attribute) {
        return AbstractSuite.getSuite(application, testName,
                attribute == null ? null : attribute.urls,
                "ooni-run");
    }

    public AbstractSuite getFrom(Result result) {
        AbstractSuite testSuite = result.getTestSuite();
        WebConnectivity test = new WebConnectivity();
        ArrayList<String> urls = new ArrayList<>();
        for (Measurement m : result.getMeasurements()){
            urls.add(Url.checkExistingUrl(m.url.url, m.url.category_code, m.url.country_code).url);
        }
        test.setInputs(urls);
        testSuite.setTestList(test);

        return testSuite;
    }
}
