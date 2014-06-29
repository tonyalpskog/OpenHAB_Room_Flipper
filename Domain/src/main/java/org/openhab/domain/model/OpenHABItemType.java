package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABItemType {
    GenericItem(0, ""),
    Group(1, "GroupItem"),
    Rollershutter(2, "RollershutterItem"),
    Switch(3, "SwitchItem"),
    Dimmer(4, "DimmerItem"),
    Number(5, "NumberItem"),
    Contact(6, "ContactItem"),
    Color(7, "ColorItem"),
    String(8,"StringItem"),
    DateTime(9, "DateTimeItem");

    public final String Name;
    public final int Id;

    private OpenHABItemType(int id, String name) {
        Id = id;
        Name = name;
    }
}
