package org.openobservatory.ooniprobe.domain.callback;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class GetMeasurementsCallback implements Callback<ApiMeasurement> {
    @Override
    public void onResponse(Call<ApiMeasurement> call, Response<ApiMeasurement> response) {
        if (response.isSuccessful() &&
                response.body() != null &&
                response.body().hasResultWithMeasurements()) {
            onSuccess(response.body().firstResult());
        } else {
            onError(Integer.toString(R.string.Modal_Error_JsonEmpty));
        }
    }

    @Override
    public void onFailure(Call<ApiMeasurement> call, Throwable t) {
        onError(t.getMessage());
    }

    public abstract void onSuccess(ApiMeasurement.Result result);

    public abstract void onError(String msg);
}
