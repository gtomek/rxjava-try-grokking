package com.r3pi.rx;

import rx.Observable;
import rx.observables.ConnectableObservable;

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
}
