package org.openobservatory.ooniprobe.model.database;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;

import static org.junit.Assert.assertEquals;

@SmallTest
public class NetworkTest extends RobolectricAbstractTest {
    private static final String BLANK = "";
    private static final String ASN = "asn";
    private static final String NETWORK_NAME = "network_name";
    private static final String COUNTRY_CODE = "country_code";

    private String unknown;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        unknown = c.getString(R.string.TestResults_UnknownASN);
    }

    @Test
    public void asn() {
        assertEquals(Network.getAsn(c, null), unknown);
        assertEquals(Network.getAsn(c, new Network()), unknown);
        Network n = new Network();
        n.asn = "";
        assertEquals(Network.getAsn(c, n), unknown);
        n.asn = ASN;
        assertEquals(Network.getAsn(c, n), ASN);
    }

    @Test
    public void name() {
        assertEquals(Network.getName(c, null), unknown);
        assertEquals(Network.getName(c, new Network()), unknown);
        Network n = new Network();
        n.network_name = BLANK;
        assertEquals(Network.getName(c, n), unknown);
        n.network_name = NETWORK_NAME;
        assertEquals(Network.getName(c, n), NETWORK_NAME);
    }

    @Test
    public void country() {
        assertEquals(Network.getCountry(c, null), unknown);
        assertEquals(Network.getCountry(c, new Network()), unknown);
        Network n = new Network();
        n.country_code = BLANK;
        assertEquals(Network.getCountry(c, n), unknown);
        n.country_code = COUNTRY_CODE;
        assertEquals(Network.getCountry(c, n), COUNTRY_CODE);
    }
}
