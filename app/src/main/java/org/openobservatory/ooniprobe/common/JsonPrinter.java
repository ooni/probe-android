package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import javax.inject.Inject;

public class JsonPrinter {

    private final Gson gson;

    @Inject
    JsonPrinter(GsonBuilder builder) {
        this.gson = builder.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public String prettyText(String json) {
        return gson.toJson(JsonParser.parseString(json));
    }
}
