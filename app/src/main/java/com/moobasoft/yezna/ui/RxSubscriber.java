package com.moobasoft.yezna.ui;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RxSubscriber {

    private final CompositeSubscription subscriptions;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;

    public RxSubscriber(Scheduler subscribeOnScheduler,
                        Scheduler observeOnScheduler) {
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler   = observeOnScheduler;
        this.subscriptions        = new CompositeSubscription();
    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public <T> void add(Observable<T> observable,
                        Action1<T> onNext,
                        Action1<Throwable> onError) {
        subscriptions.add(observable
                .compose(applySchedulers())
                .subscribe(onNext, onError));
    }

    public void clear() {
        subscriptions.clear();
        //subscriptions.unsubscribe();
    }

}