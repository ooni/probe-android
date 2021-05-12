package org.openobservatory.ooniprobe.common;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

public class DateAdapter extends TypeAdapter<Date> {
	@Override public void write(JsonWriter out, Date value) throws IOException {
		if (value == null) {
			out.nullValue();
		} else {
			String formatted = ISO8601Utils.format(value);
			out.value(formatted.replace('T', ' ').substring(0, formatted.length() - 1));
		}
	}

	@Override public Date read(JsonReader in) throws IOException {
		Date out;
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			out = null;
		} else {
			try {
				out = ISO8601Utils.parse(in.nextString().replace(' ', 'T') + "Z", new ParsePosition(0));
			} catch (ParseException e) {
				throw new IOException(e);
			}
		}
		return out;
	}
}