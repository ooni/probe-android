package org.openobservatory.ooniprobe.common;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateAdapter extends TypeAdapter<Date> {
	final private SimpleDateFormat sdf;

	public DateAdapter() {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override public void write(JsonWriter out, Date value) throws IOException {
		if (value == null)
			out.nullValue();
		else
			out.value(sdf.format(value));
	}

	@Override public Date read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		} else
			try {
				return sdf.parse(in.nextString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
	}
}
