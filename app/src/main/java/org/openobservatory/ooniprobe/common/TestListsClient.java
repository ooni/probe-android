package org.openobservatory.ooniprobe.common;

import org.openobservatory.ooniprobe.model.api.RetrieveUrlResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestListsClient {
	@GET("api/v1/test-list/urls")
	Call<RetrieveUrlResponse> getUrls(@Query("country_code") String country_code, @Query("category_codes") String category_codes);
}
