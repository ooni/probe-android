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
			public boolean header_field_name;
			@SerializedName("header_field_number")
			public boolean header_field_number;
			@SerializedName("header_field_value")
			public boolean header_field_value;
			@SerializedName("header_name_capitalization")
			public boolean header_name_capitalization;
			@SerializedName("request_line_capitalization")
			public boolean request_line_capitalization;
			@SerializedName("total")
			public boolean total;

			public boolean isAnomaly() {
				return header_field_name || header_field_number || header_field_value || header_name_capitalization || request_line_capitalization || total;
			}
		}
	}
}