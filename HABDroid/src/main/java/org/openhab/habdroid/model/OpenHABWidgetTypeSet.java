package org.openhab.habdroid.model;

import java.util.EnumSet;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetTypeSet {
    public static EnumSet<OpenHABWidgetType> UnitItem = EnumSet.of(OpenHABWidgetType.Color
            , OpenHABWidgetType.GenericItem, OpenHABWidgetType.Slider
            , OpenHABWidgetType.Setpoint, OpenHABWidgetType.RollerShutter
            , OpenHABWidgetType.SitemapText, OpenHABWidgetType.Switch
            , OpenHABWidgetType.ItemText, OpenHABWidgetType.SelectionSwitch
            , OpenHABWidgetType.Selection);

    public static EnumSet<OpenHABWidgetType> Navigation = EnumSet.of(OpenHABWidgetType.GenericItem, OpenHABWidgetType.Group, OpenHABWidgetType.SitemapText);

    public static EnumSet<OpenHABWidgetType> All = EnumSet.of(OpenHABWidgetType.Color
            , OpenHABWidgetType.GenericItem, OpenHABWidgetType.Slider
            , OpenHABWidgetType.Setpoint, OpenHABWidgetType.RollerShutter
            , OpenHABWidgetType.SitemapText, OpenHABWidgetType.Switch
            , OpenHABWidgetType.ItemText, OpenHABWidgetType.SelectionSwitch
            , OpenHABWidgetType.Selection, OpenHABWidgetType.Group
            , OpenHABWidgetType.Web, OpenHABWidgetType.Chart
            , OpenHABWidgetType.Frame, OpenHABWidgetType.Image
            , OpenHABWidgetType.Root, OpenHABWidgetType.Video);

    public OpenHABWidgetType[] getItemTypes(EnumSet<OpenHABWidgetType> enumSet) {
        return enumSet.toArray(new OpenHABWidgetType[0]);
    }
}
