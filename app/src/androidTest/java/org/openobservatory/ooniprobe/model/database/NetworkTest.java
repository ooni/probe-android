package org.openobservatory.ooniprobe.model.database;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;

import androidx.test.filters.SmallTest;

@SmallTest public class NetworkTest extends AbstractTest {
	@Test public void asn() {
		Assert.assertEquals(Network.getAsn(c, null), c.getString(R.string.TestResults_UnknownASN));
		Assert.assertEquals(Network.getAsn(c, new Network()), c.getString(R.string.TestResults_UnknownASN));
		Network nAsn = new Network();
		nAsn.asn = "";
		Assert.assertEquals(Network.getAsn(c, nAsn), c.getString(R.string.TestResults_UnknownASN));
		nAsn.asn = "ASN";
		Assert.assertEquals(Network.getAsn(c, nAsn), "ASN");
	}
}
