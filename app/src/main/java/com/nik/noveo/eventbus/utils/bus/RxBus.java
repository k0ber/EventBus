package com.nik.noveo.eventbus.utils.bus;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private final Subject<Object, Object> bus;
    private final Subject<Object, Object> stickyBus;

    private static class BusHolder {
        private static final RxBus BUS_HOLDER_INSTANCE = new RxBus();
    }

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
        stickyBus = new SerializedSubject<>(BehaviorSubject.create());
    }

    public static RxBus get() {
        return BusHolder.BUS_HOLDER_INSTANCE;
    }

    public static Observable<Object> getObservable() {
        return BusHolder.BUS_HOLDER_INSTANCE.bus;
    }

    public static Observable<Object> getStickyObservable() {
        return BusHolder.BUS_HOLDER_INSTANCE.stickyBus;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public void postSticky(Object o) {
        stickyBus.onNext(o);
    }

    public void removeStickyEvent() {
        stickyBus.onNext(null);
    }

}
