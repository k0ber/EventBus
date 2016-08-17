package com.nik.noveo.eventbus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nik.noveo.eventbus.R;
import com.nik.noveo.eventbus.utils.bus.OttoBus;
import com.nik.noveo.eventbus.utils.events.MessageEvent;
import com.nik.noveo.eventbus.utils.events.TimeEvent;
import com.squareup.otto.Subscribe;

import butterknife.BindView;

public class OttoBusFragment extends BusFragment {

    @BindView(R.id.tv1)
    TextView title;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;

    private boolean subscribed;

    public static OttoBusFragment newInstance() {
        return new OttoBusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(getString(R.string.otto_bus_fragment_name));
    }

    @Override
    public void subscribeToBus() {
        if (!subscribed) {
            OttoBus.get().register(this);
            subscribed = true;
        }
    }

    @Override
    public void unsubscribeFromBus() {
        if (subscribed) {
            OttoBus.get().unregister(this);
            subscribed = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscribed) {
            OttoBus.get().unregister(this);
        }
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        tv2.setText(getString(R.string.normal_event_msg, System.nanoTime() - event.getTime()));
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        tv3.setText(getString(R.string.sticky_event_msg, event.getNumber()));
    }

}
