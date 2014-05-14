package org.openhab.habdroid.model;

import java.util.EnumSet;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABItemSet {
    public static EnumSet<OpenHABItemType> UnitItem = EnumSet.of(OpenHABItemType.Color
            , OpenHABItemType.Contact, OpenHABItemType.Dimmer
            , OpenHABItemType.Number, OpenHABItemType.Rollershutter
            , OpenHABItemType.String, OpenHABItemType.Switch
            , OpenHABItemType.DateTime);

    public static EnumSet<OpenHABItemType> Navigation = EnumSet.of(OpenHABItemType.GenericItem, OpenHABItemType.Group);

    public static EnumSet<OpenHABItemType> All = EnumSet.of(OpenHABItemType.Color
            , OpenHABItemType.GenericItem, OpenHABItemType.Group
            , OpenHABItemType.Contact, OpenHABItemType.Dimmer
            , OpenHABItemType.Number, OpenHABItemType.Rollershutter
            , OpenHABItemType.String, OpenHABItemType.Switch
            , OpenHABItemType.DateTime);

    public OpenHABItemType[] getItemTypes(EnumSet<OpenHABItemType> enumSet) {
        return enumSet.toArray(new OpenHABItemType[0]);
    }
}
