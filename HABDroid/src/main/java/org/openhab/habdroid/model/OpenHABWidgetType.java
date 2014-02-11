package org.openhab.habdroid.model;

import org.openhab.habdroid.R;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetType {
    GenericItem(0, "", R.layout.openhabwidgetlist_genericitem, -1, false),
    Root(1, "root", R.layout.openhabwidgetlist_genericitem, -1, false),
    Frame(2, "Frame", R.layout.openhabwidgetlist_frameitem, -1, false),
    Group(3, "Group", R.layout.openhabwidgetlist_groupitem, -1, false),
    Switch(4, "Switch", R.layout.openhabwidgetlist_switchitem, R.layout.openhabwidget_control_switchitem, true),
    Text(5, "Text", R.layout.openhabwidgetlist_textitem, R.layout.openhabwidget_control_textitem, false),
    Slider(6, "Slider", R.layout.openhabwidgetlist_slideritem, R.layout.openhabwidget_control_slideritem, true),
    Image(7, "Image", R.layout.openhabwidgetlist_imageitem, -1, false),
    Selection(8, "Selection", R.layout.openhabwidgetlist_selectionitem, -1, false),
    SelectionSwitch(9, "Switch", R.layout.openhabwidgetlist_sectionswitchitem, -1, false),
    RollerShutter(10, "RollershutterItem", R.layout.openhabwidgetlist_rollershutteritem, -1, false),
    Setpoint(11, "Setpoint", R.layout.openhabwidgetlist_setpointitem, -1, false),
    Chart(12, "Chart", R.layout.openhabwidgetlist_chartitem, -1, false),
    Video(13, "Video", R.layout.openhabwidgetlist_videoitem, -1, false),
    Web(14, "Webview", R.layout.openhabwidgetlist_webitem, -1, false),
    Color(15, "Colorpicker", R.layout.openhabwidgetlist_coloritem, -1, false);

    public final String Name;
    public final int Id;
    public final int WidgetLayoutId;
    public final int ControlLayoutId;
    public final boolean HasDynamicControl;

    private OpenHABWidgetType(int id, String name, int widgetLayoutId, int controlLayoutId, boolean hasDynamicControl) {
        Id = id;
        Name = name;
        WidgetLayoutId = widgetLayoutId;
        ControlLayoutId = controlLayoutId;
        HasDynamicControl = hasDynamicControl;
    }
}
