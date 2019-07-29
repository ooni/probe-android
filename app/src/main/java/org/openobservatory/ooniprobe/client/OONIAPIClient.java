package org.openobservatory.ooniprobe.client;

import org.openobservatory.ooniprobe.model.api.ApiMeasurement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OONIAPIClient {
	@GET("api/v1/measurements")
	Call<ApiMeasurement> getMeasurement(@Query("report_id") String report_id, @Query("input") String input);
}
