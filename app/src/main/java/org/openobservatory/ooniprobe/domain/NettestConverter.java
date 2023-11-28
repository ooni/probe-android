package org.openobservatory.ooniprobe.domain;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.openobservatory.engine.OONIRunNettest;

import java.util.Arrays;
import java.util.List;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class NettestConverter extends TypeConverter<String, List> {
    @Override
    public String getDBValue(List model) {
        return new Gson().toJson(model);
    }

    @Override
    public List getModelValue(String data) {
        return Arrays.asList(new Gson().fromJson(data, OONIRunNettest[].class));
    }
}
