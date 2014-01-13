package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum ValueTypeEnum {
    Time(0),
    Date(1),
    Numeric(2),
    Boolean(3),
    Percentage(4),
    DateAndTime(5);

    public final int Value;

    private ValueTypeEnum(int id) {
        Value = id;
    }
}
