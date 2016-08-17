package com.nik.noveo.eventbus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nik.noveo.eventbus.R;
import com.nik.noveo.eventbus.utils.events.MessageEvent;
import com.nik.noveo.eventbus.utils.events.TimeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class EventBusFragment extends BusFragment {

    @BindView(R.id.tv1) TextView title;
    @BindView(R.id.tv2) TextView tv2;
    @BindView(R.id.tv3) TextView tv3;

    public static EventBusFragment newInstance() {
        return new EventBusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(getString(R.string.event_bus_fragment_name));
    }

    @Override
    public void subscribeToBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void unsubscribeFromBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseEvent(TimeEvent event) {
        tv2.setText(getString(R.string.normal_event_msg, System.nanoTime() - event.getTime()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        tv3.setText(getString(R.string.sticky_event_msg, event.getNumber()));
    }

    /**
     * THREADING *
     * @Subscribe // if we not specify thread, method will be called from thread where event was send
     * it is the same as @Subscribe(threadMode = ThreadMode.POSTING)
     * @Subscribe(threadMode = ThreadMode.MAIN) - android main thread for ui stuff
     * @Subscribe(threadMode = ThreadMode.BACKGROUND) - background thread for database works for example
     * event bus from green robot have one background thread, so avoid to blocking it for a long time
     * @Subscribe(threadMode = ThreadMode.ASYNC) -  async for api calls for example,
     * async mean your method will be executed asynchronously, in some new thread,
     * but avoid launch large number of methods with this thread mode.

     * PRIORITY *
     * @Subscribe(priority = 1);
     * priority may be useful when an event trigger some UI logic if the app is in the foreground,
     * but react differently if the app is currently not visible to the user.
     * The default priority is 0

     * CANCELLING *
     * @Subscribe public void onSomeEvent(SomeEvent ev) {
     * ...
     * EventBus.getDefault().cancelEventDelivery(event);
     * }
     * after cancel event any further event delivery will be cancelled: subsequent subscribers won’t receive the event.
     * you can cancel event in high priority subscriber and subscriber with lower priority will not receive it.
     */
}
