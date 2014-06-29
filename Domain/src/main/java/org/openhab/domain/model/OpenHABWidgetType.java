package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetType {
    GenericItem(0, "", false),
    Root(1, "root", false),
    Frame(2, "Frame", false),
    Group(3, "Group", false),
    Switch(4, "Switch", true),
    ItemText(5, "Text", false),
    SitemapText(6, "Text", false),
    Slider(7, "Slider", true),
    Image(8, "Image", false),
    Selection(9, "Selection", false),
    SelectionSwitch(10, "Switch", false),
    RollerShutter(11, "RollershutterItem", false),
    Setpoint(12, "Setpoint", false),
    Chart(13, "Chart", false),
    Video(14, "Video", false),
    Web(15, "Webview", false),
    Color(16, "Colorpicker", false);

    public final int Id;
    public final String Name;
    public final boolean HasDynamicControl;

    private OpenHABWidgetType(int id, String name, boolean hasDynamicControl) {
        Id = id;
        Name = name;
        HasDynamicControl = hasDynamicControl;
    }
}
