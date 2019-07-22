package org.openobservatory.ooniprobe.common;

import org.openobservatory.ooniprobe.model.api.ExplorerUrlResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeasurementsClient {
	@GET("api/v1/measurements")
	Call<ExplorerUrlResponse> getMeasurementUrl(@Query("report_id") String report_id, @Query("input") String input);
}
