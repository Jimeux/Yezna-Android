package com.moobasoft.yezna;

import org.junit.Test;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        Scheduler threadOne = Schedulers.newThread();
        Scheduler threadTwo = Schedulers.newThread();

        Observable<Integer> one = Observable.just(1);
        Observable<String> two = Observable.just("A");

        one
                .flatMap(integer -> {
                    System.out.println(Thread.currentThread().getName());
                    return two;
                })
                .subscribeOn(threadOne)
                .observeOn(threadTwo)
                .subscribe(s -> {
                    System.out.println("End: " + Thread.currentThread());
                });


        Thread.sleep(1000);
    }
}