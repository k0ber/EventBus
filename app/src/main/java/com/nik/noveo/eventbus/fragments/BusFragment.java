package com.nik.noveo.eventbus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BusFragment extends Fragment {

    private Unbinder unbinder;

    /**
     * for most cases it's enough to subscribe to bus in onStart()
     * and unsubscribe in onStop()
     */

    public abstract void subscribeToBus();

    public abstract void unsubscribeFromBus();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onStop() {
        super.onStop();
        unsubscribeFromBus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
