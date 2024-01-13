package org.openobservatory.ooniprobe.domain;

import static org.openobservatory.ooniprobe.test.suite.AbstractSuiteExtensionsKt.getSuite;

import androidx.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class GetTestSuite {

    private final Application application;

    @Inject
    GetTestSuite(Application application) {
        this.application = application;
    }

    public AbstractSuite get(String testName, @Nullable List<String> urls) {
        return getSuite(application, testName, urls, "ooni-run");
    }

    public AbstractSuite getFrom(Result result) {
        Optional<AbstractSuite> optionalSuite = result.getTestSuite(application);
        if (optionalSuite.isPresent()) {
            AbstractSuite testSuite = optionalSuite.get();
            WebConnectivity test = new WebConnectivity();

            // possible NPE from measurements whose url's are null.
            List<Url> urls = Lists.transform(
                    Lists.newArrayList(
                            Iterables.filter(result.getMeasurements(), input -> input.url != null)
                    ),
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
        } else {
            return null;
        }
    }

    public AbstractSuite getForWebConnectivityReRunFrom(Result result, List<String> inputs) {
        Optional<AbstractSuite> optionalSuite = result.getTestSuite(application);
        if (optionalSuite.isPresent() && Objects.equals(optionalSuite.get().getName(), OONITests.WEBSITES.name())) {
            AbstractSuite testSuite = optionalSuite.get();
            WebConnectivity test = new WebConnectivity();

            // possible NPE from measurements whose url's are null.
            List<Url> urls = Lists.transform(
                    Lists.newArrayList(
                            Iterables.filter(result.getMeasurements(), input -> input.url != null)
                    ),
                    measurement -> new Url(
                            measurement.url.url,
                            measurement.url.category_code,
                            measurement.url.country_code
                    )
            );

            Url.saveOrUpdate(urls);

            test.setInputs(inputs);
            testSuite.setTestList(test);

            return testSuite;
        } else {
            return null;
        }
    }
}
