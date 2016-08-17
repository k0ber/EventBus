package com.nik.noveo.eventbus.utils.events;

import java.util.Random;

public class MessageEvent {
    private int number;

    public MessageEvent() {
        this.number = new Random().nextInt(1000);
    }

    public int getNumber() {
        return number;
    }
}
