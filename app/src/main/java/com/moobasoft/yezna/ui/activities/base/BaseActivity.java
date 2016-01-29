package com.moobasoft.yezna.ui.activities.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.moobasoft.yezna.App;
import com.moobasoft.yezna.di.components.ActivityComponent;
import com.moobasoft.yezna.di.components.DaggerActivityComponent;
import com.moobasoft.yezna.di.modules.ActivityModule;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.events.EventBus;
import com.moobasoft.yezna.rest.auth.CredentialStore;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Inject protected EventBus eventBus;
    @Inject protected CredentialStore credentialStore;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected ActivityComponent getComponent() {
        return DaggerActivityComponent.builder()
                .mainModule(new MainModule())
                .activityModule(new ActivityModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}