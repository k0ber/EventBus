package com.nik.noveo.eventbus.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nik.noveo.eventbus.R;
import com.nik.noveo.eventbus.fragments.BusFragment;
import com.nik.noveo.eventbus.fragments.EventBusFragment;
import com.nik.noveo.eventbus.fragments.OttoBusFragment;
import com.nik.noveo.eventbus.fragments.RxBusFragment;
import com.nik.noveo.eventbus.utils.bus.OttoBus;
import com.nik.noveo.eventbus.utils.bus.RxBus;
import com.nik.noveo.eventbus.utils.events.MessageEvent;
import com.nik.noveo.eventbus.utils.events.TimeEvent;
import com.squareup.otto.Produce;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private MessageEvent stickyEventForOtto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ButterKnife.bind(this);

        OttoBus.get().register(this); // needs for sticky events only

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_bus_fragment, EventBusFragment.newInstance())
                    .add(R.id.otto_fragment, OttoBusFragment.newInstance())
                    .add(R.id.rx_bus_fragment, RxBusFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        OttoBus.get().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.btn_send_event)
    public void onSendEventClicked() {
        EventBus.getDefault().post(new TimeEvent(System.nanoTime()));
        OttoBus.get().post(new TimeEvent(System.nanoTime()));
        RxBus.get().post(new TimeEvent(System.nanoTime()));
    }

    @OnClick(R.id.btn_send_sticky_event)
    public void onSendStickyEventClicked() {
        MessageEvent event = new MessageEvent();
        EventBus.getDefault().postSticky(event);

        stickyEventForOtto = event;
        OttoBus.get().post(event);

        RxBus.get().postSticky(event);
    }

    @Produce
    public MessageEvent onMessageAvailable() {
        return stickyEventForOtto;
    }

    @OnClick(R.id.btn_cancel_sticky_event)
    public void cancelStickyEvents() {
        // green robot event bus
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }

        stickyEventForOtto = null;

        RxBus.get().removeStickyEvent();
    }

    @OnClick(R.id.btn_subscribe)
    public void onSubscribeClicked() {
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.event_bus_fragment)).subscribeToBus();
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.otto_fragment)).subscribeToBus();
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.rx_bus_fragment)).subscribeToBus();
    }

    @OnClick(R.id.btn_unsubscribe)
    public void onUnsubscribeClicked() {
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.event_bus_fragment)).unsubscribeFromBus();
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.otto_fragment)).unsubscribeFromBus();
        ((BusFragment) getSupportFragmentManager().findFragmentById(R.id.rx_bus_fragment)).unsubscribeFromBus();
    }

}
