package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.errors.RegistrationError;
import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.requests.RegistrationRequest;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

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

    public ConnectPresenter(RxSchedulers rxSchedulers,
                            UserService userService,
                            CredentialStore credentialStore) {
        super(rxSchedulers);
        this.userService = userService;
        this.credentialStore = credentialStore;
    }

    public void login(String username, String password) {
        Observable<Result<AccessToken>> shareable = userService
                .getAccessToken(username, password, "password")
                .compose(rxSchedulers.applySchedulers())
                .share();

        subscriptions.add(shareable
                .filter(not(isSuccess()))
                .filter(not(Result::isError))
                .subscribe(result -> view.onLoginError()));

        subscriptions.add(shareable
                .filter(not(isSuccess()))
                .subscribe(this::handleError));

        subscribeUserObservable(shareable, username);
    }

    public void register(String email, String username, String password) {
        Observable<Result<AccessToken>> shareable = userService
                .register(new RegistrationRequest(email, username, password))
                .compose(rxSchedulers.applySchedulers())
                .share();

        subscriptions.add(shareable
                .filter(hasError(UNPROCESSABLE_ENTITY))
                .map(result -> result.response().errorBody())
                .subscribe(this::handleValidationErrors));

        subscriptions.add(shareable
                .filter(not(isSuccess()))
                .filter(not(hasError(UNPROCESSABLE_ENTITY)))
                .subscribe(this::handleError));

        subscribeUserObservable(shareable, username);
    }

    private void subscribeUserObservable(Observable<Result<AccessToken>> tokenShareable, String username) {
        Observable<Result<User>> userShareable = tokenShareable
                .filter(isSuccess())
                .map(result -> result.response().body())
                .flatMap(token -> mapTokenToGetUser(username, token))
                .share();

        subscriptions.add(userShareable
                .filter(isSuccess())
                .map(result -> result.response().body())
                .subscribe(this::handleGetUser));

        subscriptions.add(userShareable
                .filter(not(isSuccess())) //TODO: Retry
                .subscribe(this::handleError));
    }

    private Observable<Result<User>> mapTokenToGetUser(String username, AccessToken token) {
        credentialStore.saveToken(token);
        return userService.getUser(username)
                .compose(rxSchedulers.applySchedulers());
    }

    private void handleGetUser(User user) {
        credentialStore.saveUser(user);
        view.onLogin();
    }

    private void handleValidationErrors(ResponseBody errorBody) {
        try {
            RegistrationError error = RegistrationError.CONVERTER.convert(errorBody);
            view.onRegistrationError(error.getUsername(),
                    error.getEmail(),
                    error.getPassword());
        } catch (IOException e) {
            view.onError(R.string.error_default);
        }
    }
}