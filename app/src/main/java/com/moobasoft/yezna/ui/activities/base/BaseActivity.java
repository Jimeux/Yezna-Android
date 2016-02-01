package com.moobasoft.yezna.ui.activities.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.moobasoft.yezna.App;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.di.components.ActivityComponent;
import com.moobasoft.yezna.di.components.DaggerActivityComponent;
import com.moobasoft.yezna.di.modules.ActivityModule;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.events.EventBus;
import com.moobasoft.yezna.events.auth.LoginPromptEvent;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.ui.activities.ConnectActivity;

import javax.inject.Inject;

import icepick.Icepick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject protected EventBus eventBus;
    @Inject protected CredentialStore credentialStore;

    protected CompositeSubscription eventSubscriptions;

    @Override protected void onStart() {
        super.onStart();
        subscribeToEvents();
    }

    @Override protected void onStop() {
        super.onStop();
        eventSubscriptions.clear();
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected void subscribeToEvents() {
        Subscription loginPromptEvent = eventBus
                .listenFor(LoginPromptEvent.class)
                .subscribe(this::promptForLogin);
        eventSubscriptions = new CompositeSubscription(loginPromptEvent);
    }

    protected void promptForLogin(LoginPromptEvent event) {
        String message = (event != null && event.getMessage() != null) ?
                event.getMessage() : getString(R.string.error_unauthorized);

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == Snackbar.Callback.DISMISS_EVENT_ACTION)
                            startConnectActivity(false);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.green400))
                .setAction(getString(R.string.login), v -> {
                })
                .show();
    }

    public void promptForLogin() {
        promptForLogin(null);
    }

    public void startConnectActivity(boolean register) {
        Intent intent = new Intent(this, ConnectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ConnectActivity.REGISTER_MODE_KEY, register);
        startActivity(intent);
    }
}