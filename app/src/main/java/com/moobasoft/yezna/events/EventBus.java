package com.moobasoft.yezna.events;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class EventBus {

    private final Subject<Event, Event> bus = new SerializedSubject<>(PublishSubject.create());

    public <E extends Event> void send(E event) {
        if (bus.hasObservers())
            bus.onNext(event);
    }

    public <E extends Event> void sendDelayed(E event, int delay) {
        Observable.just(event)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::send);
    }

    public <E extends Event> Observable<E> listenFor(Class<E> eventClass) {
        return bus.ofType(eventClass);
    }
}