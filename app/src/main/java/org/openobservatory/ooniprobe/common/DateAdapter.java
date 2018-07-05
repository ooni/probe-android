package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Date;

public class DateAdapter implements JsonDeserializer<Date> {
	@Override public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return new Gson().fromJson(new JsonPrimitive(json.getAsString().replace(' ','T') + "Z"), typeOfT);
	}
}
