package org.openobservatory.ooniprobe.di;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.DateAdapter;
import org.openobservatory.ooniprobe.common.TamperingJsonDeserializer;
import org.openobservatory.ooniprobe.di.annotations.ApiUrl;
import org.openobservatory.ooniprobe.di.annotations.HeaderInterceptor;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideAppContext() {
        return application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    GsonBuilder provideGsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateAdapter())
                .registerTypeAdapter(TestKeys.Tampering.class, new TamperingJsonDeserializer());
    }

    @Provides
    @Singleton
    Gson provideGson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor();
    }

    @Provides
    @Singleton
    @HeaderInterceptor
    Interceptor provideHeaderInterceptor() {
        return chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("User-Agent", "ooniprobe-android/" + BuildConfig.VERSION_NAME)
                    .build();
            return chain.proceed(request);
        };
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging, @HeaderInterceptor Interceptor headerInterceptor) {
        logging.level(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false) // TODO(https://github.com/ooni/probe/issues/1923)
                .addInterceptor(logging)
                .addInterceptor(headerInterceptor)
                .build();
    }

    protected String getApiUrl() {
        return BuildConfig.OONI_API_BASE_URL;
    }

    @Provides
    @ApiUrl
    protected String provideApiUrl() {
        return getApiUrl();
    }

    @Provides
    @Singleton
    protected OONIAPIClient provideApiClient(OkHttpClient client, @ApiUrl String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(OONIAPIClient.class);
    }

}