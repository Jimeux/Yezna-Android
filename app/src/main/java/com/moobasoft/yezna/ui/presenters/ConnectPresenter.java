package com.moobasoft.yezna.ui.presenters;

import android.util.Log;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.errors.RegistrationError;
import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.requests.RegistrationRequest;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Observable;

public class ConnectPresenter extends RxPresenter<ConnectPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onLogin();

        //void onRegister(String username);
        void onLoginError();

        void onRegistrationError(String username, String email, String password);
    }

    private Observable<AccessToken> connectObservable;

    private final UserService userService;
    private final CredentialStore credentialStore;

    public ConnectPresenter(RxSchedulers rxSchedulers,
                            UserService userService,
                            CredentialStore credentialStore) {
        super(rxSchedulers);
        this.userService = userService;
        this.credentialStore = credentialStore;
    }

    @Override
    public void bindView(View view) {
        super.bindView(view);
        if (connectObservable != null)
            subscribeToConnectObservable();
    }

    public void login(String username, String password) {
        createConnectObservable(userService.getAccessToken(
                username, password, "password"));
    }

    public void register(String email, String username, String password) {
        createConnectObservable(userService.register(
                new RegistrationRequest(email, username, password)));
    }

    private void createConnectObservable(Observable<AccessToken> observable) {
        connectObservable = observable
                .compose(rxSchedulers.applySchedulers())
                .cache();
        subscribeToConnectObservable();
    }

    private void subscribeToConnectObservable() {
        subscriptions.add(connectObservable.subscribe(
                this::handleConnectOnNext,
                this::handleConnectOnError,
                this::clearConnectObservable));
    }

    private void handleConnectOnNext(AccessToken accessToken) {
        credentialStore.saveToken(accessToken);
        view.onLogin();
    }

    private void handleConnectOnError(Throwable throwable) {
        if (hasErrorCode(throwable, UNPROCESSABLE_ENTITY))
            handleValidationErrors(throwable);
        else if (hasErrorCode(throwable, UNAUTHORIZED))
            view.onLoginError();
        else
            handleThrowable(throwable);
        clearConnectObservable();
    }

    private void clearConnectObservable() {
        connectObservable = null;
    }

    private void handleValidationErrors(Throwable throwable) {
        HttpException httpException = (HttpException) throwable;
        ResponseBody errorBody = httpException.response().errorBody();

        try {
            RegistrationError error = RegistrationError.CONVERTER.convert(errorBody);
            view.onRegistrationError(
                    error.getUsername(),
                    error.getEmail(),
                    error.getPassword());
        } catch (IOException e) {
            view.onError(R.string.error_default);
            Log.e("TAGGART", "Error in handleValidationErrors: ", e);
        }
    }
}