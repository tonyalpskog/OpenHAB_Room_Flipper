package org.openhab.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum EntityDataTypeSource {
    UNIT(0),
    OPERATION(1),
    STATIC(2);

    public final int Value;

    private EntityDataTypeSource(int value) {
        Value = value;
    }
}
