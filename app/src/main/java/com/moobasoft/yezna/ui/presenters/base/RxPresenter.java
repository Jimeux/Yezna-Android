package com.moobasoft.yezna.ui.presenters.base;

import android.util.Log;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.Rest;
import com.moobasoft.yezna.ui.RxSubscriber;

import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class RxPresenter<V extends RxPresenter.RxView> extends Presenter<V> {

    public interface RxView {
        void promptForLogin();
        void onError(int messageId);
    }

    protected final RxSubscriber subscriptions;

    public RxPresenter(RxSubscriber subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        subscriptions.clear();
    }

    protected String getCacheHeader(boolean forceRefresh) {
        return (forceRefresh) ? Rest.CACHE_NO_CACHE : Rest.CACHE_DEFAULT;
    }

    public static final int SUCCESS              = 200;
    public static final int CREATED              = 201;
    public static final int UNAUTHORIZED         = 401;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int GATEWAY_TIMEOUT      = 504;

    public void defaultResponses(int responseCode) {
        switch (responseCode) {
            case UNAUTHORIZED:
                view.promptForLogin();
                break;
            case GATEWAY_TIMEOUT:
                view.onError(R.string.error_offline);
                break;
            default:
                view.onError(R.string.error_default);
        }
    }

    public static final String OFFLINE_CODE     = "ENETUNREACH";
    public static final String SERVER_DOWN_CODE = "ECONNREFUSED";

    public void handleError(Throwable throwable) {
        String message = throwable.getMessage();

        // 401 is giving java.io.EOFException on API 10

        if (throwable instanceof SocketTimeoutException)
            view.onError(R.string.error_timeout);
        else if (message != null && message.contains(OFFLINE_CODE))
            view.onError(R.string.error_offline);
        else if (message != null && message.contains(SERVER_DOWN_CODE) ||
                throwable instanceof SocketException)
            view.onError(R.string.error_server);
        else {
            Log.e("Taggart", throwable.getMessage(), throwable);
            view.onError(R.string.error_default);
        }
    }

}