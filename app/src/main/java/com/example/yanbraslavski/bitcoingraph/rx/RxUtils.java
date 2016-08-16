package com.example.yanbraslavski.bitcoingraph.rx;

import rx.Observable;


/**
 * Utility class to provide common RxJava operations
 */
public class RxUtils {

    /**
     * Creates an observable that will do the computation on back thread only once for each emmited
     * item and will notify multiple subscribers.
     */
    public static <T> Observable<T> createSharedBackgroundObservable(Observable.OnSubscribe<T> onSubscribe) {
        return RxUtils.createBackgroundObservable(onSubscribe).share();
    }

    /**
     * Creates an observable that will do the computation on back thread and publish to main thread
     */
    public static <T> Observable<T> createBackgroundObservable(Observable.OnSubscribe<T> onSubscribe) {
        return Observable.create(onSubscribe).compose(RxUtils.createBackgroundTransformer());
    }

    /**
     * Creates a transformer that will schedule the work for background thread and will make it
     * observable
     * on Main thread
     */
    public static <T> IoToMainTransformer<T> createBackgroundTransformer() {
        return new IoToMainTransformer<>();
    }
}
