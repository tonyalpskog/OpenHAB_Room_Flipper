package org.openhab.habdroid.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABItemType {
    GENERICITEM(0, ""),
    GROUP(1, "GroupItem"),
    ROLLERSHUTTER(2, "RollershutterItem"),
    SWITCH(3, "SwitchItem");

    public final String Name;
    public final int Id;

    private OpenHABItemType(int id, String name) {
        Id = id;
        Name = name;
    }
}
