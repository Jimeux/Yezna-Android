package com.moobasoft.yezna.events;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class EventBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        if (bus.hasObservers())
            bus.onNext(o);
    }

    public void sendDelayed(Object event, int delay) {
        Observable.just(event)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::send);
    }

    public <E> Observable<E> listenFor(Class<E> eventClass) {
        return bus.ofType(eventClass);
    }
}