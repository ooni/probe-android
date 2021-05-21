package org.openobservatory.ooniprobe.domain;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

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


}
