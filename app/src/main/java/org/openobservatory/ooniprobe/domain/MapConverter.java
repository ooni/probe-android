package org.openobservatory.ooniprobe.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.lang.reflect.Type;
import java.util.HashMap;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MapConverter extends TypeConverter<String, HashMap> {
    @Override
    public String getDBValue(HashMap model) {
        return new Gson().toJson(model);
    }

    @Override
    public HashMap getModelValue(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> map = gson.fromJson(json, type);
        return map;
    }
}
