package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

public class JsonResultB extends AbstractJsonResult {
	@SerializedName("test_keys")
	public TestKeysB test_keys;

	public class TestKeysB extends AbstractTestKeys {
		@SerializedName("tampering")
		public boolean tampering;
	}
}