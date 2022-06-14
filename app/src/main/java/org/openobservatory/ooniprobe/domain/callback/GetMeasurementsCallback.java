package org.openobservatory.ooniprobe.domain.callback;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class GetMeasurementsCallback implements Callback<ResponseBody> {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful() &&
                response.body() != null &&
                response.isSuccessful()) {
            try {
                onSuccess(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onError(Integer.toString(R.string.Modal_Error_JsonEmpty));
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        onError(t.getMessage());
    }

    public abstract void onSuccess(String result);

    public abstract void onError(String msg);
}
