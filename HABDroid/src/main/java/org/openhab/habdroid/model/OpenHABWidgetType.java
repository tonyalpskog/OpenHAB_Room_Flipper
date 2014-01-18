package org.openhab.habdroid.model;

import org.openhab.habdroid.R;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetType {
    GenericItem(0, "", R.layout.openhabwidgetlist_genericitem),
    Root(1, "root", R.layout.openhabwidgetlist_genericitem),
    Frame(2, "Frame", R.layout.openhabwidgetlist_frameitem),
    Group(3, "Group", R.layout.openhabwidgetlist_groupitem),
    Switch(4, "Switch", R.layout.openhabwidgetlist_switchitem),
    Text(5, "Text", R.layout.openhabwidgetlist_textitem),
    Slider(6, "Slider", R.layout.openhabwidgetlist_slideritem),
    Image(7, "Image", R.layout.openhabwidgetlist_imageitem),
    Selection(8, "Selection", R.layout.openhabwidgetlist_selectionitem),
    SelectionSwitch(9, "Switch", R.layout.openhabwidgetlist_sectionswitchitem),
    RollerShutter(10, "RollershutterItem", R.layout.openhabwidgetlist_rollershutteritem),
    Setpoint(11, "Setpoint", R.layout.openhabwidgetlist_setpointitem),
    Chart(12, "Chart", R.layout.openhabwidgetlist_chartitem),
    Video(13, "Video", R.layout.openhabwidgetlist_videoitem),
    Web(14, "Webview", R.layout.openhabwidgetlist_webitem),
    Color(15, "Colorpicker", R.layout.openhabwidgetlist_coloritem);

    public final String Name;
    public final int Id;
    public final int LayoutId;

    private OpenHABWidgetType(int id, String name, int layoutId) {
        Id = id;
        Name = name;
        LayoutId = layoutId;
    }
}
