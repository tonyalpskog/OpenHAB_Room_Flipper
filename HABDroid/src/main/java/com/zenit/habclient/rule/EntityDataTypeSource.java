package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum EntityDataTypeSource {
    UNIT(0),
    RULE(1),
    USER(2);

    public final int Value;

    private EntityDataTypeSource(int value) {
        Value = value;
    }
}
