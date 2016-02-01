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

    public void updateProfile(String email, String password,
                              String imagePath, String imageUrl, RequestBody imageRb) {

        RequestBody emailRb = null;
        if (!TextUtils.isEmpty(email))
            emailRb = RequestBody.create(MediaType.parse("text/plain"), email);

        RequestBody passwordRb = null;
        if (!TextUtils.isEmpty(password))
            passwordRb = RequestBody.create(MediaType.parse("text/plain"), password);

        RequestBody avatarFileRb = null;
        if (imageRb != null)
            avatarFileRb = imageRb;
        else if (!TextUtils.isEmpty(imagePath))
            avatarFileRb = RequestBody.create(MediaType.parse("image/*"), new File(imagePath));

        RequestBody avatarUrlRb = null;
        if (!TextUtils.isEmpty(imageUrl))
            avatarUrlRb = RequestBody.create(MediaType.parse("text/plain"), imageUrl);

        Subscription createSubscription = userService
                .updateProfile(emailRb, passwordRb, avatarUrlRb, avatarFileRb)
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