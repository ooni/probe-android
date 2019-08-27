package org.openobservatory.ooniprobe.client.callback;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class GetMeasurementJsonCallback implements Callback {
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
        String json;
        try {
            json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                    .toJson(new JsonParser().parse(response.body().string()));
        } catch (Exception e) {
            onError(e.getMessage());
            return;
        }
        onSuccess(json);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        onError(e.getMessage());
    }

    public abstract void onSuccess(String json);

    public abstract void onError(String msg);
}
