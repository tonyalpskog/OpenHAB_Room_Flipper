package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum AccessModifier {
    Private(0),
    ReadOnly(1),
    Writable(2);

    public final int Value;

    private AccessModifier(int value) {
        Value = value;
    }
}
