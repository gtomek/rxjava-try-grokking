package com.r3pi.rx;


import com.sun.tools.javac.util.Pair;
import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int STACK_LEVEL = 4;

    public static void main(String[] args) {
        RxPlayground rxPlayground = new RxPlayground();

        rxPlayground.getMyValueObservableFromCallble().subscribe(integer -> {
            System.out.println(integer);
        }, Throwable::printStackTrace);

        // Connectable, common RxJava mistakes
        // https://speakerdeck.com/dlew/common-rxjava-mistakes?slide=70
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
//        rxPlayground.getEmptyObservable().first()
//                .subscribe(o -> System.out.println(o),
//                        getDefaultErrorHandling());

        // retry errors handling blog.danlew.net/2015/12/08/error-handling-in-rxjava/

        //rxPlayground.getErrorPropagatedObservable().subscribe(System.out::println, getDefaultErrorHandling());

        rxPlayground.getRetryTestGood().subscribe(s -> System.out.println("Retry returned: " + s),
                getDefaultErrorHandling());

        Observable.combineLatest(rxPlayground.getIntervalStrings(200),
                rxPlayground.getIntervalStrings(800), (s, s2) -> Pair.of(s, s2))
                .throttleFirst(401, TimeUnit.MILLISECONDS)
                .subscribe(pair -> System.out.println("Interval returned :" + pair.fst + ":" + pair.snd +
                " :" +  TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS)));

        // sleep just to see results of intervals in previous examples
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
