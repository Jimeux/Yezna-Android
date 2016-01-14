package com.moobasoft.yezna.ui.presenters.base;

import android.util.Log;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.Rest;
import com.moobasoft.yezna.ui.RxSchedulers;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit.Result;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public abstract class RxPresenter<V extends RxPresenter.RxView> extends Presenter<V> {

    public interface RxView {
        void promptForLogin();
        void onError(int messageId);
    }

    public static final int UNAUTHORIZED = 401;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int GATEWAY_TIMEOUT = 504;

    public static final String OFFLINE_CODE = "ENETUNREACH";
    public static final String SERVER_DOWN_CODE = "ECONNREFUSED";

    protected CompositeSubscription subscriptions = new CompositeSubscription();
    protected RxSchedulers rxSchedulers;

    public RxPresenter(RxSchedulers rxSchedulers) {
        this.rxSchedulers = rxSchedulers;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        subscriptions.clear();
    }

    protected String getCacheHeader(boolean forceRefresh) {
        return (forceRefresh) ? Rest.CACHE_NO_CACHE : Rest.CACHE_DEFAULT;
    }

    public void defaultResponses(Result<?> result) {
        final int code = result.response().code();

        switch (code) {
            case UNAUTHORIZED:
                view.promptForLogin();
                break;
            case GATEWAY_TIMEOUT:
                view.onError(R.string.error_offline);
                break;
            default:
                Log.e("Taggart", "Error in defaultResponses: code " + code);
                view.onError(R.string.error_default);
        }
    }

    public void handleThrowable(Throwable throwable) {
        String message = throwable.getMessage();

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

    public void handleError(Result<?> result) {
        if (result.isError())
            handleThrowable(result.error());
        else
            defaultResponses(result);
    }

    protected static Func1<Result<?>, Boolean> hasError(final int errorCode) {
        return result -> !result.isError() && result.response().code() == errorCode;
    }

    protected static Func1<Result<?>, Boolean> isSuccess() {
        return result -> !result.isError() && result.response().isSuccess();
    }

    public static <T> Func1<T, Boolean> not(final Func1<T, Boolean> func) {
        return value -> !func.call(value);
    }

}