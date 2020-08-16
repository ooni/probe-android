package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;

import androidx.test.filters.SmallTest;

@SmallTest public class NetworkTest extends AbstractTest {
	private static final String BLANK = "";
	private static final String ASN = "asn";
	private static final String NETWORK_NAME = "network_name";
	private static final String COUNTRY_CODE = "country_code";

	@Test public void asn() {
		Assert.assertEquals(Network.getAsn(c, null), c.getString(R.string.TestResults_UnknownASN));
		Assert.assertEquals(Network.getAsn(c, new Network()), c.getString(R.string.TestResults_UnknownASN));
		Network n = new Network();
		n.asn = "";
		Assert.assertEquals(Network.getAsn(c, n), c.getString(R.string.TestResults_UnknownASN));
		n.asn = ASN;
		Assert.assertEquals(Network.getAsn(c, n), ASN);
	}

	@Test public void name() {
		Assert.assertEquals(Network.getName(c, null), c.getString(R.string.TestResults_UnknownASN));
		Assert.assertEquals(Network.getName(c, new Network()), c.getString(R.string.TestResults_UnknownASN));
		Network n = new Network();
		n.network_name = BLANK;
		Assert.assertEquals(Network.getName(c, n), c.getString(R.string.TestResults_UnknownASN));
		n.network_name = NETWORK_NAME;
		Assert.assertEquals(Network.getName(c, n), NETWORK_NAME);
	}

	@Test public void country() {
		Assert.assertEquals(Network.getCountry(c, null), c.getString(R.string.TestResults_UnknownASN));
		Assert.assertEquals(Network.getCountry(c, new Network()), c.getString(R.string.TestResults_UnknownASN));
		Network n = new Network();
		n.country_code = BLANK;
		Assert.assertEquals(Network.getCountry(c, n), c.getString(R.string.TestResults_UnknownASN));
		n.country_code = COUNTRY_CODE;
		Assert.assertEquals(Network.getCountry(c, n), COUNTRY_CODE);
	}

	//This test doesn't work anymore with the new preloaded db
	 public void getNetwork() {
		Delete.table(Network.class);
		Assert.assertEquals(SQLite.selectCountOf().from(Network.class).longValue(), 0);
		Network n = Network.getNetwork(BLANK, BLANK, BLANK, BLANK, BLANK);
		n.save();
		Assert.assertEquals(SQLite.selectCountOf().from(Network.class).longValue(), 1);
		n = Network.getNetwork(BLANK, BLANK, BLANK, BLANK, BLANK);
		n.save();
		Assert.assertEquals(SQLite.selectCountOf().from(Network.class).longValue(), 1);
		n = Network.getNetwork("networkName", "ip", "asn", "countryCode", "networkType");
		n.save();
		Assert.assertEquals(SQLite.selectCountOf().from(Network.class).longValue(), 2);
		Delete.table(Network.class);
		Assert.assertEquals(SQLite.selectCountOf().from(Network.class).longValue(), 0);
	}
}
