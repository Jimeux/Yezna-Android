package com.moobasoft.yezna.ui.presenters;

import android.text.TextUtils;

import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;

public class ProfilePresenter extends RxPresenter<ProfilePresenter.View> {

    public interface View extends RxPresenter.RxView  {
        void onProfileUpdated(User user);
        void onProfileError(String error);
    }

    private final UserService userService;

    public ProfilePresenter(RxSchedulers schedulers,
                            UserService userService) {
        super(schedulers);
        this.userService = userService;
    }

    public void updateProfile(String email, String password, RequestBody avatarRb) {

        RequestBody emailRb = null;
        if (!TextUtils.isEmpty(email)) //TODO: Validation
            emailRb = RequestBody.create(MediaType.parse("text/plain"), email);

        RequestBody passwordRb = null;
        if (!TextUtils.isEmpty(password)) //TODO: Validation
            passwordRb = RequestBody.create(MediaType.parse("text/plain"), password);

        Subscription createSubscription = userService
                .updateProfile(emailRb, passwordRb, avatarRb)
                .compose(rxSchedulers.applySchedulers())
                .subscribe(view::onProfileUpdated, this::handleError);

        subscriptions.add(createSubscription);
    }

    private void handleError(Throwable throwable) {
        if (hasErrorCode(throwable, UNPROCESSABLE_ENTITY))
            view.onProfileError("Process profile errors!");
        else
            handleThrowable(throwable);
    }
}