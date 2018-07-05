package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

public class JsonResult extends AbstractJsonResult {
	@SerializedName("test_keys")
	public TestKeys test_keys;

	public class TestKeys extends AbstractTestKeys {
		@SerializedName("tampering")
		public boolean tampering;
	}
}