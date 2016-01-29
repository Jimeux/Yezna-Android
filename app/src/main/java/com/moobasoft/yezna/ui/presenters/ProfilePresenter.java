package com.moobasoft.yezna.ui.presenters;

import android.text.TextUtils;

import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.io.File;

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

    public void updateProfile(String username, String email, String password, String imagePath) {

        RequestBody usernameRb = null;
        if (!TextUtils.isEmpty(username))
            usernameRb = RequestBody.create(MediaType.parse("text/plain"), username);

        RequestBody emailRb = null;
        if (!TextUtils.isEmpty(email))
            emailRb = RequestBody.create(MediaType.parse("text/plain"), email);

        RequestBody passwordRb = null;
        if (!TextUtils.isEmpty(password))
            passwordRb = RequestBody.create(MediaType.parse("text/plain"), password);

        RequestBody avatarRb = null;
        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath); //TODO: Check file
            avatarRb = RequestBody.create(MediaType.parse("image/*"), file);
        }

        Subscription createSubscription = userService
                .updateProfile(usernameRb, emailRb, passwordRb, avatarRb)
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