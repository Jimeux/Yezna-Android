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

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Observable;
import rx.Subscription;

public class ConnectPresenter extends RxPresenter<ConnectPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onLogin();
        //void onRegister(String username);
        void onLoginError();
        void onRegistrationError(String username, String email, String password);
    }

    private Observable<User> connectObservable;

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
        Observable<AccessToken> loginObservable = userService
                .getAccessToken(username, password, "password");
        createConnectObservable(loginObservable, username);
    }

    public void register(String email, String username, String password) {
        Observable<AccessToken> registerObservable = userService
                .register(new RegistrationRequest(email, username, password));
        createConnectObservable(registerObservable, username);
    }

    private void createConnectObservable(Observable<AccessToken> observable, String username) {
        connectObservable = observable
                .flatMap(token -> mapTokenToGetUser(username, token))
                .compose(rxSchedulers.applySchedulers())
                .cache();
        subscribeToConnectObservable();
    }

    private void subscribeToConnectObservable() {
        Subscription loginSubscription = connectObservable.subscribe(
                this::handleConnectOnNext,
                this::handleConnectOnError,
                this::handleConnectOnComplete);
        subscriptions.add(loginSubscription);
    }

    private Observable<User> mapTokenToGetUser(String username, AccessToken token) {
        credentialStore.saveToken(token);
        return userService.getUser(username)
                .compose(rxSchedulers.applySchedulers());
    }

    private void handleConnectOnNext(User user) {
        credentialStore.saveUser(user);
        view.onLogin();
    }

    private void handleConnectOnError(Throwable throwable) {
        if (hasErrorCode(throwable, UNPROCESSABLE_ENTITY))
            handleValidationErrors(throwable);
        else if (hasErrorCode(throwable, UNAUTHORIZED))
            view.onLoginError();
        else
            handleThrowable(throwable);
    }

    private void handleConnectOnComplete() {
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
        }
    }
}