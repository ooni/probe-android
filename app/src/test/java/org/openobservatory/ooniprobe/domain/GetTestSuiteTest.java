package org.openobservatory.ooniprobe.domain;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.domain.models.Attribute;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;

import io.bloco.faker.Faker;

import static org.mockito.Mockito.mock;

public class GetTestSuiteTest extends RobolectricAbstractTest {

    @Test
    public void testWithAttributes() {
        // Arrange
        Faker faker = new Faker();
        GetTestSuite getSuite = build();

        PreferenceManager pm = mock(PreferenceManager.class);

        Attribute attribute = new Attribute();
        attribute.urls = new ArrayList<>();
        attribute.urls.add(faker.internet.url());
        attribute.urls.add(faker.internet.url());
        attribute.urls.add(faker.internet.url());

        // Act
        AbstractSuite suite = getSuite.get(WebConnectivity.NAME, attribute);
        AbstractTest[] tests = suite.getTestList(pm);

        // Assert
        Assert.assertEquals(WebsitesSuite.NAME, suite.getName());
        Assert.assertEquals(1, tests.length);
        Assert.assertEquals(attribute.urls.get(0), tests[0].getInputs().get(0));
        Assert.assertEquals(attribute.urls.get(1), tests[0].getInputs().get(1));
        Assert.assertEquals(attribute.urls.get(2), tests[0].getInputs().get(2));
    }

    @Test
    public void testWithoutAttributes() {
        // Arrange
        GetTestSuite getSuite = build();

        PreferenceManager pm = mock(PreferenceManager.class);

        // Act
        AbstractSuite suite = getSuite.get(WebConnectivity.NAME, null);
        AbstractTest[] tests = suite.getTestList(pm);

        // Assert
        Assert.assertEquals(WebsitesSuite.NAME, suite.getName());
        Assert.assertEquals(1, tests.length);
        Assert.assertNull(tests[0].getInputs());
    }

    public GetTestSuite build() {
        return new GetTestSuite(a);
    }

}