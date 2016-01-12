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

public class ConnectPresenter extends RxPresenter<ConnectPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onLoginSuccess();
        void onRegisterSuccess(String username);
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

    public void handleOnNextRegister(Result<AccessToken> result, String username) {
        Response<AccessToken> response = result.response();

        if (result.isError()) {
            handleError(result.error());
        } else if (response.code() == CREATED) {
            credentialStore.saveToken(response.body());
            view.onRegisterSuccess(username);
            getUserAfterConnect(username);
        } else if (response.code() == UNPROCESSABLE_ENTITY) {
            getInputErrors(response.errorBody());
        } else
            defaultResponses(response.code());
    }

    public void handleOnNextLogin(Result<AccessToken> result, String username) {
        credentialStore.delete();
        Response<AccessToken> response = result.response();

        if (result.isError()) {
            handleError(result.error());
        } else if (response.isSuccess()) {
            credentialStore.saveToken(response.body());
            view.onLoginSuccess();
            getUserAfterConnect(username);
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
        subscriptions.add(
                userService.getAccessToken(username, password, "password"),
                result -> handleOnNextLogin(result, username),
                this::handleError);
    }

    public void register(String email, String username, String password) {
        subscriptions.add(
                userService.register(new RegistrationRequest(email, username, password)),
                result -> handleOnNextRegister(result, username),
                this::handleError);
    }

    private void getUserAfterConnect(String username) {
        subscriptions.add(userService.getUser(username),
                this::connectSuccess, this::handleError);
    }

    private void connectSuccess(User user) {
        credentialStore.saveUser(user);
        view.onLoginSuccess();
    }

}