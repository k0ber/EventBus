package com.nik.noveo.eventbus.utils.bus;

import com.squareup.otto.Bus;

public class OttoBus {

    /**
     * In this example the ThreadEnforcer.MAIN parameter is used, in this case Otto enforces
     * that events are only send from the main thread. If you want to be able to
     * send events from any thread use the ThreadEnforcer.ANY parameter.
     */

    private static class BusHolder {
        static final Bus HOLDER_INSTANCE = new Bus(); // equals new Bus(ThreadEnforcer.MAIN)
    }

    public static Bus get() {
        return BusHolder.HOLDER_INSTANCE;
    }

}
