package org.openobservatory.ooniprobe.client.callback;

import org.openobservatory.ooniprobe.model.api.ApiMeasurement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class GetMeasurementsCallback implements Callback<ApiMeasurement> {
    @Override
    public void onResponse(Call<ApiMeasurement> call, Response<ApiMeasurement> response) {
        if (response.isSuccessful() &&
                response.body() != null &&
                response.body().results != null &&
                response.body().results.size() == 1 &&
                response.body().results.get(0).measurement_url != null) {
            onSuccess(response.body().results.get(0));
        } else {
            onError(Integer.toString(response.code()));
        }
    }

    @Override
    public void onFailure(Call<ApiMeasurement> call, Throwable t) {
        onError(t.getMessage());
    }

    public abstract void onSuccess(ApiMeasurement.Result result);

    public abstract void onError(String msg);
}
