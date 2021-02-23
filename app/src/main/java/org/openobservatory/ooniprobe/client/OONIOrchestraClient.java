package org.openobservatory.ooniprobe.client;

import org.openobservatory.ooniprobe.model.api.UrlList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * @deprecated
 * superseed by https://github.com/ooni/probe-cli/pull/221
 */
@Deprecated
public interface OONIOrchestraClient {
	@GET("api/v1/test-list/urls")
	Call<UrlList> getUrls(@Query("country_code") String country_code, @Query("category_codes") String category_codes);
}
