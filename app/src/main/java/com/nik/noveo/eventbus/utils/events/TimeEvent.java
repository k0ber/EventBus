package com.nik.noveo.eventbus.utils.events;

public class TimeEvent {
    private final long time;

    public TimeEvent(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

}
