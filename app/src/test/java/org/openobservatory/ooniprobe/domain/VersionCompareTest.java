package org.openobservatory.ooniprobe.domain;

import org.junit.Assert;
import org.junit.Test;

public class VersionCompareTest {

    @Test
    public void testSameVersion() {
        // Arrange
        VersionCompare comparator = build();

        // Act / Assert
        Assert.assertEquals(0, comparator.compare("1.0.0", "1.0.0"));
        Assert.assertEquals(0, comparator.compare("0.1.0", "0.1.0"));
        Assert.assertEquals(0, comparator.compare("0.0.1", "0.0.1"));
    }

    @Test
    public void testDifferentVersion() {
        // Arrange
        VersionCompare comparator = build();

        // Act / Assert
        Assert.assertEquals(-1, comparator.compare("1.0.0", "1.2.0"));
        Assert.assertEquals(-1, comparator.compare("0.1.0", "0.1.2"));
        Assert.assertEquals(-1, comparator.compare("0.0.1", "0.0.3"));
    }

    public VersionCompare build() {
        return new VersionCompare();
    }
}