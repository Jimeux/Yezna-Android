package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.errors.RegistrationError;
import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.requests.RegistrationRequest;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSubscriber;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Response;
import retrofit.Result;
import rx.Observable;

public class ConnectPresenter extends RxPresenter<ConnectPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onLogin();
        void onRegister(String username);
        void onLoginError();
        void onRegistrationError(String username, String email, String password);
    }

    private final UserService userService;
    private final CredentialStore credentialStore;

    public ConnectPresenter(RxSubscriber subscriptions,
                            UserService userService,
                            CredentialStore credentialStore) {
        super(subscriptions);
        this.userService = userService;
        this.credentialStore = credentialStore;
    }

    public void handleOnNextRegister(Result<AccessToken> result) {
        Response<AccessToken> response = result.response();

        if (result.isError()) {
            handleError(result.error());
        } else if (response.code() == CREATED) {
            credentialStore.saveToken(response.body());
        } else if (response.code() == UNPROCESSABLE_ENTITY) {
            getInputErrors(response.errorBody());
        } else
            defaultResponses(response.code());
    }

    public void handleOnNextLogin(Result<AccessToken> result) {
        credentialStore.delete();
        Response<AccessToken> response = result.response();

        if (result.isError()) {
            handleError(result.error());
        } else if (response.isSuccess()) {
            credentialStore.saveToken(response.body());
        } else if (response.code() == UNAUTHORIZED) {
            view.onLoginError();
        } else
            defaultResponses(response.code());
    }

    private void getInputErrors(ResponseBody errorBody) {
        try {
            RegistrationError error = RegistrationError.CONVERTER.convert(errorBody);
            view.onRegistrationError(error.getUsername(),
                    error.getEmail(),
                    error.getPassword());
        } catch (IOException e) {
            view.onError(R.string.error_default);
        }
    }

    public void login(String username, String password) {
        Observable<Result<AccessToken>> observable =
                userService.getAccessToken(username, password, "password")
                .doOnNext(this::handleOnNextLogin);
        getUserAfterConnect(true, username, observable);
    }

    public void register(String email, String username, String password) {
        Observable<Result<AccessToken>> observable =
                userService.register(new RegistrationRequest(email, username, password))
                        .doOnNext(this::handleOnNextRegister);
        getUserAfterConnect(true, username, observable);
    }

    private void getUserAfterConnect(boolean isLogin, String username,
                                     Observable<Result<AccessToken>> observable) {
        Observable<User> userObservable = observable.flatMap(accessTokenResult -> {
            return userService.getUser(username); //TODO: Retries
        });

        subscriptions.add(userObservable,
                user -> handleUserRetrieved(user, isLogin), this::handleError);
    }

    private void handleUserRetrieved(User user, boolean isLogin) {
        credentialStore.saveUser(user);
        if (isLogin)
            view.onLogin();
        else
            view.onRegister(user.getUsername());
    }
}