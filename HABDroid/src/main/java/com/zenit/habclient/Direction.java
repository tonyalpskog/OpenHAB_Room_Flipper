package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */
public enum Direction {
    UP(0),
    DOWN(1),
    RIGHT(2),
    LEFT(3),
    BELOW(4),
    ABOVE(5),
    UP_LEFT(6),
    UP_RIGHT(7),
    DOWN_LEFT(8),
    DOWN_RIGHT(9);

    public final int Value;

    private Direction(int value) {
        Value = value;
    }
}
