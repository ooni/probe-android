package org.openobservatory.ooniprobe.client;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.model.api.UrlList;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrchestrateClientTest extends AbstractTest {
    @Test
    public void getUrlsSuccess() {
        final CountDownLatch signal = new CountDownLatch(1);
        a.getOrchestrateClient().getUrls("XX", null).enqueue(new Callback<UrlList>() {
            @Override
            public void onResponse(Call<UrlList> call, Response<UrlList> response) {
                if (!response.isSuccessful())
                    Assert.fail();
                signal.countDown();
            }

            @Override
            public void onFailure(Call<UrlList> call, Throwable t) {
                Assert.fail();
                signal.countDown();
            }
        });
        try {
            signal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
