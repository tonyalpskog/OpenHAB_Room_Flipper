package org.openhab.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */
public enum UnitType {
    Switch(0),
    Dimmer(1),
    RoomHeater(2),
    Vent(3),
    Socket(4),
    Group(5);

    public final int Value;

    private UnitType(int value) {
        Value = value;
    }
}
