package com.moobasoft.yezna.ui;

import rx.Observable;
import rx.Scheduler;

public class RxSchedulers {

    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;

    public RxSchedulers(Scheduler subscribeOnScheduler,
                        Scheduler observeOnScheduler) {
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler   = observeOnScheduler;
    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}