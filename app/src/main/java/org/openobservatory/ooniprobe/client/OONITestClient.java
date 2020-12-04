package org.openobservatory.ooniprobe.client;

import org.openobservatory.ooniprobe.model.api.CheckIn;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OONITestClient {
	@POST("api/v1/check-in")
	Call<CheckIn> checkIn(@Query("charging") Boolean charging,
						  @Query("on_wifi") Boolean on_wifi,
						  @Query("platform") String platform,
						  @Query("probe_asn") String probe_asn,
						  @Query("probe_cc") String probe_cc,
						  @Query("run_type") String run_type,
						  @Query("software_version") String software_version,
						  @Query("web_connectivity") String web_connectivity);
}

/*
{
  "charging": true,
  "on_wifi": true,
  "platform": "string",
  "probe_asn": "string",
  "probe_cc": "string",
  "run_type": "string",
  "software_version": "string",
  "web_connectivity": {
    "category_codes:": "string"
  }
}
 */