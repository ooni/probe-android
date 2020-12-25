package org.openobservatory.ooniprobe.client.callback;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class GetMeasurementJsonCallback implements Callback {
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        String json;
        try {
            json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                    .toJson(JsonParser.parseString(response.body().string()));
        } catch (Exception e) {
            onError(e.getMessage());
            return;
        }
        onSuccess(json);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        onError(e.getMessage());
    }

    public abstract void onSuccess(String json);

    public abstract void onError(String msg);
}
