package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum DataSourceType {
    UNIT(0),
    RULE(1),
    USER(2);

    public final int Value;

    private DataSourceType(int value) {
        Value = value;
    }
}
