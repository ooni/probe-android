package org.openobservatory.ooniprobe.domain;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.List;

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

        List<Url> urls = Lists.transform(
                result.getMeasurements(),
                measurement -> new Url(
                        measurement.url.url,
                        measurement.url.category_code,
                        measurement.url.country_code
                )
        );

        List<String> inputs = Url.saveOrUpdate(urls);

        test.setInputs(inputs);
        testSuite.setTestList(test);

        return testSuite;
    }
}
