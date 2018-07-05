package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

public class JsonResultHttpHeader extends AbstractJsonResult {
	@SerializedName("test_keys")
	public TestKeys test_keys;

	public class TestKeys extends AbstractTestKeys {
		@SerializedName("tampering")
		public Tampering tampering;

		public class Tampering {
			@SerializedName("header_field_name")
			public String header_field_name;
			@SerializedName("header_field_number")
			public String header_field_number;
			@SerializedName("header_field_value")
			public String header_field_value;
			@SerializedName("header_name_capitalization")
			public String header_name_capitalization;
			@SerializedName("request_line_capitalization")
			public String request_line_capitalization;
			@SerializedName("total")
			public String total;
		}
	}
}