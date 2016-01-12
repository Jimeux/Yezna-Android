package com.moobasoft.yezna.di.components;

import android.content.Context;

import com.moobasoft.yezna.EventBus;
import com.moobasoft.yezna.di.modules.AppModule;
import com.moobasoft.yezna.di.modules.EndpointModule;
import com.moobasoft.yezna.di.modules.RestModule;
import com.moobasoft.yezna.di.scopes.Endpoint;
import com.moobasoft.yezna.rest.auth.CredentialStore;

import javax.inject.Singleton;

import dagger.Component;
import retrofit.Retrofit;

@Singleton
@Component(
        modules = {
                AppModule.class,
                RestModule.class,
                EndpointModule.class
        }
)
public interface AppComponent {

    EventBus eventBus();

    Context applicationContext();

    Retrofit retrofit();

    CredentialStore credentialStore();

    @Endpoint
    String endpoint();

}