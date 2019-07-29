package org.openobservatory.ooniprobe.client;

import org.openobservatory.ooniprobe.model.api.UrlList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OONIOrchestraClient {
	@GET("api/v1/test-list/urls")
	Call<UrlList> getUrls(@Query("country_code") String country_code, @Query("category_codes") String category_codes);
}
