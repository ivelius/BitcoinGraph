package com.example.yanbraslavski.bitcoingraph.rx.eventbus;

import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Reactive eventbus implementation
 */
public class RxBus {

    //TODO : replace with dagger
//    public static RxBus instance = new RxBus();

    private final Subject<IRxEvent, IRxEvent> _bus = new SerializedSubject<>(PublishSubject.create());

    public void send(IRxEvent o) {
        _bus.onNext(o);
    }

    public Observable<IRxEvent> toObserverable() {
        return _bus;
    }

    public <T extends IRxEvent> Subscription subscribeOnMainThread(final Class<T> type, final Action1<? super T> action) {
        return toObserverable()
                .compose(RxUtils.createBackgroundTransformer())
                .ofType(type).subscribe(action);
    }

}