package com.r3pi.rx;


import rx.Observable;
import rx.Observer;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.Locale;

public class Main {

    private static final int STACK_LEVEL = 4;

    public static void main(String[] args) {
        RxPlayground rxPlayground = new RxPlayground();

        rxPlayground.getMyValueObservableFromCallble().subscribe(integer -> {
            System.out.println(integer);
        }, Throwable::printStackTrace);

        // Connectable
        ConnectableObservable<Long> autoConnectObservable = rxPlayground.getConnectableObservable();
//        Observable<Long> autoConnectObservable = rxPlayground.getConnectableObservable().refCount();

        autoConnectObservable.subscribe(aLong -> {
            System.out.printf("1: %s\n", aLong);
        }, getDefaultErrorHandling());
        autoConnectObservable.subscribe(aLong -> {
            System.out.printf("2: %s\n", aLong);
        }, getDefaultErrorHandling());

        autoConnectObservable.connect();

        // multicasting
        // http://blog.danlew.net/2016/06/13/multicasting-in-rxjava/
        System.out.println("Multicast wrong way");

        Observable<?> brokenMulticastObservable = rxPlayground.getBrokenMulticast();
        brokenMulticastObservable.subscribe(s -> System.out.println("Sub1 got: " + s));
        brokenMulticastObservable.subscribe(s -> System.out.println("Sub2 got: " + s));

        System.out.println("\nMulticast Good way");

        Observable<?> fixedMulticastObservable = rxPlayground.getFixedMulticast();
        fixedMulticastObservable.subscribe(s -> System.out.println("Sub1 got: " + s));
        fixedMulticastObservable.subscribe(s -> System.out.println("Sub2 got: " + s));

        //Empty crash log
        rxPlayground.getEmptyObservable().first()
//                .subscribeOn(Schedulers.io())
                .subscribe(o -> System.out.println(o),
                        getDefaultErrorHandling());
    }

    private static Action1<Throwable> getDefaultErrorHandling() {
        return throwable -> {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final StackTraceElement stackTraceElement = stackTrace[STACK_LEVEL];
            final String identifyingString =
                    String.format(Locale.getDefault(), "%s : %s (%d)", stackTraceElement.getClassName(),
                            stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            throw new OnErrorNotImplementedException(String.format("Crash: %s", identifyingString),
                    throwable);
        };
    }




}
