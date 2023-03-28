package org.openobservatory.ooniprobe.model.jsonresult;

import java.io.Serializable;

public class EventResult implements Serializable {
	public String key;
	public Value value;

	public static class Value implements Serializable {
		public double key;
		public String log_level;
		public String message;
		public double percentage;
		public String json_str;
		public int idx;
		public String report_id;
		public String probe_ip;
		public String probe_asn;
		public String probe_cc;
		public String probe_network_name;
		public double downloaded_kb;
		public double uploaded_kb;
		public String input;
		public String failure;
		public String orig_key;
	}
}
