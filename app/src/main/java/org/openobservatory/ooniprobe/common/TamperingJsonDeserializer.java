package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.openobservatory.ooniprobe.model.JsonResult;

import java.lang.reflect.Type;

public class TamperingJsonDeserializer implements JsonDeserializer<JsonResult.TestKeys.Tampering> {
	@Override public JsonResult.TestKeys.Tampering deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean())
			return new JsonResult.TestKeys.Tampering(json.getAsJsonPrimitive().getAsBoolean());
		else
			return new JsonResult.TestKeys.Tampering(new Gson().fromJson(json, JsonResult.TestKeys.Tampering.TamperingObj.class).isAnomaly());
	}
}
