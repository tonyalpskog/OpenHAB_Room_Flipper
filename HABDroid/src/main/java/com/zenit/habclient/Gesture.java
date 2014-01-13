package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */
public enum Gesture {
    SWIPE_UP(0),
    SWIPE_DOWN(1),
    SWIPE_LEFT(2),
    SWIPE_RIGHT(3),
    PINCH_IN(4),
    PINCH_OUT(5);

    public final int Value;

    private Gesture(int value) {
        Value = value;
    }
}
