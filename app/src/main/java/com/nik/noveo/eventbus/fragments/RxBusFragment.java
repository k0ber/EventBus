package com.nik.noveo.eventbus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nik.noveo.eventbus.R;
import com.nik.noveo.eventbus.utils.bus.RxBus;
import com.nik.noveo.eventbus.utils.events.MessageEvent;
import com.nik.noveo.eventbus.utils.events.TimeEvent;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class RxBusFragment extends BusFragment {

    @BindView(R.id.tv1)
    TextView title;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;

    private CompositeSubscription subscriptions;

    public static RxBusFragment newInstance() {
        return new RxBusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(getString(R.string.rx_bus_fragment_name));
    }

    @Override
    public void subscribeToBus() {
        unsubscribeFromBus();
        subscriptions = new CompositeSubscription();
        subscriptions.addAll(RxBus.getObservable().subscribe((this::onEvent)),
                RxBus.getStickyObservable().subscribe((this::onEvent)));
    }

    private void onEvent(Object event) {
        if (event instanceof TimeEvent) {
            onTimeEvent(((TimeEvent) event));
        } else if (event instanceof MessageEvent) {
            onMessageEvent((MessageEvent) event);
        }
    }

    public void onTimeEvent(TimeEvent event) {
        tv2.setText(getString(R.string.normal_event_msg, System.nanoTime() - event.getTime()));
    }

    public void onMessageEvent(MessageEvent event) {
        tv3.setText(getString(R.string.sticky_event_msg, event.getNumber()));
    }

    @Override
    public void unsubscribeFromBus() {
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }
}
