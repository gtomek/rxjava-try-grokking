package com.r3pi.rx;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Created by tomaszgiszczak on 09/06/2016.
 * some examples of rxcode from https://speakerdeck.com/dlew/common-rxjava-mistakes
 */
public class RxPlayground {

    int myValue = 11;

    Observable<Integer> getMyValueObservableFromCallble() {
        return Observable.fromCallable(() -> {
            return myValue;
        });
    }

    ConnectableObservable<Long> getConnectableObservable() {
        return Observable.fromCallable(() -> System.nanoTime()).publish();
    }

    Observable<?> getEmptyObservable() {
        return Observable.empty();
    }

    Observable<?> getBrokenMulticast() {
        return Observable.just("Event")
                .publish()
                .autoConnect(2)
                .map(s -> {
                    System.out.println("Expensive operation for " + s);
                    return s;
                });
    }


    Observable<?> getFixedMulticast() {
        return Observable.just("Event")
                .map(s -> {
                    System.out.println("Expensive operation for " + s);
                    return s;
                })
                .publish()
                .autoConnect(2);
    }

    Observable<String> getRetryTestGood() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .map(input -> {
                    double random = Math.random();
                    if (random < .5) {
                        throw new RuntimeException();
                    }
                    return "Success " + input + " random " + random;
                })
                .retry()
                .onErrorReturn(error -> "Uh oh");
    }


    Observable<String> getRetryTestBad() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .map(input -> {
                    double random = Math.random();
                    if (random < .5) {
                        throw new RuntimeException("Wrong value :" + random);
                    }
                    return "Success " + input + " random " + random;
                })
                .onErrorReturn(error -> "Uh oh" + error)
                .retry();
    }

    Observable<String> getErrorPropagatedObservable() {
        return Observable.just("Hello!")
                .map(input -> {
                    try {
                        return transform(input);
                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }
                });
    }

    Observable<String> getErrorPropagatedFlatMapObservable() {
        return Observable.just("Hello!")
                .flatMap(input -> {
                    try {
                        return Observable.just(transform(input));
                    } catch (Throwable t) {
                        return Observable.error(t);
                    }
                });
    }

    private String transform(String input) {
        return input + Integer.valueOf(input);
    }

}
