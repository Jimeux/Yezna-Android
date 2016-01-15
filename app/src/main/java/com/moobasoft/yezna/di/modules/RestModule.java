package com.moobasoft.yezna.di.modules;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moobasoft.yezna.di.scopes.Endpoint;
import com.moobasoft.yezna.rest.auth.ApiAuthenticator;
import com.moobasoft.yezna.rest.auth.ApiHeaders;
import com.moobasoft.yezna.rest.auth.CredentialStore;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
@Module
public class RestModule {

    private static final int DISK_CACHE_SIZE = 8 * 1024 * 1024; //8MB

    @Provides @Singleton
    Retrofit provideRetrofit(@Endpoint String baseUrl, OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton
    Gson provideGson() {
        final GsonBuilder gson = new GsonBuilder();
        gson.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gson.create();
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Context context, CredentialStore store) {
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .authenticator(new ApiAuthenticator(store))
                .addInterceptor(new ApiHeaders(context, store))
                .connectTimeout(10, SECONDS)
                .readTimeout(10, SECONDS)
                .writeTimeout(10, SECONDS)
                //.cache(cache)
                .build();

        //if (BuildConfig.DEBUG)
        //  client.networkInterceptors().add(new StethoInterceptor());
    }

}