package com.moobasoft.yezna.ui.fragments.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.yezna.App;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.di.components.DaggerFragmentComponent;
import com.moobasoft.yezna.di.components.FragmentComponent;
import com.moobasoft.yezna.di.modules.FragmentModule;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.events.EventBus;
import com.moobasoft.yezna.events.auth.LoginPromptEvent;
import com.moobasoft.yezna.rest.auth.CredentialStore;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.Icepick;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public abstract class RxFragment extends Fragment {

    @Inject protected EventBus eventBus;
    @Inject protected CredentialStore credentialStore;

    protected CompositeSubscription eventSubscriptions;

    @Bind(R.id.content)      protected ViewGroup contentView;
    @Bind(R.id.loading_view) protected ViewGroup loadingView;
    @Bind(R.id.empty_view)   protected ViewGroup emptyView;
    @Bind(R.id.error_view)   protected ViewGroup errorView;
    @Bind(R.id.error_msg)    protected TextView errorMessage;
    @Bind(R.id.empty_msg)    protected TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    protected List<ViewGroup> stateViews;

    protected abstract void subscribeToEvents();

    @Override public void onStart() {
        super.onStart();
        eventSubscriptions = new CompositeSubscription();
        subscribeToEvents();
    }

    @Override public void onStop() {
        super.onStop();
        if (eventSubscriptions != null)
            eventSubscriptions.clear();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public void onError(int messageId) {
        String message = getString(messageId);
        if (message == null)
            message = getString(R.string.error_default);
        activateErrorView(message);
    }

    public void promptForLogin() {
        eventBus.send(new LoginPromptEvent());
    }

    public void promptForLogin(String message) {
        eventBus.send(new LoginPromptEvent(message));
    }

    protected FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .mainModule(new MainModule())
                .fragmentModule(new FragmentModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build();
    }

    protected void activateView(View view) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(View.GONE);

        if (view == null)
            activateErrorView(getString(R.string.error_default));
        else
            view.setVisibility(VISIBLE);
    }

    protected void activateEmptyView(String message) {
        activateView(emptyView);
        emptyMessage.setText(message);
    }

    protected void activateErrorView(String message) {
        if (loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            errorMessage.setText(message);
            activateView(errorView);
        } else
           Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    protected void activateContentView() {
        activateView(contentView);
    }
    
    protected void activateLoadingView() {
        activateView(loadingView);
    }
    
    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() {
        onRefresh();
    }

}