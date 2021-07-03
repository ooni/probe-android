package org.openobservatory.ooniprobe.model.database;

import androidx.test.filters.SmallTest;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Before;
import org.junit.Test;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.factory.NetworkFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void toStringTest() {
        // Arrange
        Network network = new Network();
        network.asn = "asn";
        network.network_name = "network";

        // Act
        String value = Network.toString(c, network);

        // Assert
        assertEquals("asn - network", value);
    }

    @Test
    public void deleteTest() {
        // Arrange
        Network noResultNetwork = NetworkFactory.build();
        noResultNetwork.save();
        Network resultNetwork = ResultFactory.createAndSave(new WebsitesSuite()).network;

        // Act
        boolean noResultDelete = noResultNetwork.delete();
        boolean resultDelete = resultNetwork.delete();

        Network noResultDatabase = SQLite.select().from(Network.class).where(Network_Table.ip.eq(noResultNetwork.ip)).querySingle();
        Network resultDatabase = SQLite.select().from(Network.class).where(Network_Table.ip.eq(resultNetwork.ip)).querySingle();

        // Assert
        assertTrue(noResultDelete);
        assertFalse(resultDelete);
        assertNull(noResultDatabase);
        assertNotNull(resultDatabase);
    }

    @Test
    public void getLocalizedTypeTest() {
        // Arrange
        Network mobileNetwork = new Network();
        mobileNetwork.network_type = ReachabilityManager.MOBILE;

        Network wifiNetwork = new Network();
        wifiNetwork.network_type = ReachabilityManager.WIFI;

        Network noNetwork = new Network();
        noNetwork.network_type = ReachabilityManager.NO_INTERNET;

        Network unknownNetwork = new Network();
        unknownNetwork.network_type = "loremIpsum";

        // Act / Assert
        assertEquals(unknown, Network.getLocalizedNetworkType(c, null));
        assertEquals(c.getString(R.string.TestResults_Summary_Hero_Mobile), Network.getLocalizedNetworkType(c, mobileNetwork));
        assertEquals(c.getString(R.string.TestResults_Summary_Hero_WiFi), Network.getLocalizedNetworkType(c, wifiNetwork));
        assertEquals(c.getString(R.string.TestResults_Summary_Hero_NoInternet), Network.getLocalizedNetworkType(c, noNetwork));
        assertEquals(unknown, Network.getLocalizedNetworkType(c, unknownNetwork));
    }
}
