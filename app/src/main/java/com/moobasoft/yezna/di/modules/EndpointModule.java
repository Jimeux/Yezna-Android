package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.Endpoint;
import com.moobasoft.yezna.rest.Rest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EndpointModule {

    @Endpoint
    @Provides
    @Singleton
    public String provideEndpoint() { return Rest.PRODUCTION_API_URL; }

}