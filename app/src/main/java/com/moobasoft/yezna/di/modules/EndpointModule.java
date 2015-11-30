package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.Endpoint;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EndpointModule {

    //private static final String PRODUCTION_API_URL = "http://210.140.83.8/";
    private static final String PRODUCTION_API_URL = "http://192.168.11.5:3000/";

    @Endpoint
    @Provides
    @Singleton
    String provideEndpoint() { return PRODUCTION_API_URL; }

}