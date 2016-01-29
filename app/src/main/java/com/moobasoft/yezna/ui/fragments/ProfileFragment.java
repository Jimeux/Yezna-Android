package com.moobasoft.yezna.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.ProfilePresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ProfileFragment extends RxFragment implements ProfilePresenter.View {

    @Inject ProfilePresenter presenter;

    public ProfileFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);
        getComponent().inject(this);
        presenter.bindView(this);
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override protected void subscribeToEvents() {
        Subscription logOutEvent =
                eventBus.listenFor(LogOutEvent.class)
                        .subscribe(event -> {
                            activateEmptyView(getString(R.string.unauthorized_my_questions));
                            //TODO: Redirect
                        });
        eventSubscriptions = new CompositeSubscription(logOutEvent);
    }

    @Override public void onError(int messageId) {
        super.onError(messageId);
    }

    @Override public void onRefresh() {
    }

    @Override public void onProfileUpdated(User user) {
        credentialStore.saveUser(user);
        eventBus.send(new LoginEvent());
    }

    @Override public void onProfileError(String error) {

    }

    @OnClick(R.id.update_profile_btn)
    public void clickUpdateBtn() {
        presenter.updateProfile("jimzie", null, null, null);
    }
}