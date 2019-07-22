package org.openobservatory.ooniprobe.common;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;

public class APITest extends AbstractTest {
    private static final String EXISTING_REPORT_ID = "20190113T202156Z_AS327931_CgoC3KbgM6zKajvIIt1AxxybJ1HbjwwWJjsJnlxy9rpcGY54VH";
    private static final String EXISTING_REPORT_ID_2 = "20190702T000027Z_AS5413_6FT78sjp5qnESDVWlFlm6bfxxwOEqR08ySAwigTF6C8PFCbMsM";
    private static final String NONEXISTING_REPORT_ID = "EMPTY";
    private static final String NON_PARSABLE_URL = "https://\t";
    private static final String JSON_URL = "https://api.ooni.io/api/v1/measurement/temp-id-263478291";

    @Test public void testExisting() {
    }
    @Test public void testNonExisting() {
    }
    @Test public void testDeleteJsons() {
    }
}
