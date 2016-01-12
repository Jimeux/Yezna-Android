package com.moobasoft.yezna;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class EventBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        if (bus.hasObservers())
            bus.onNext(o);
    }

    public Observable<Object> listen() {
        return bus;
    }
}