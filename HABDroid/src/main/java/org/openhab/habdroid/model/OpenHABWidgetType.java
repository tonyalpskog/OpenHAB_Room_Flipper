package org.openhab.habdroid.model;

import org.openhab.habdroid.R;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetType {
    GENERICITEM(0, "", R.layout.openhabwidgetlist_genericitem),
    ROOT(1, "root", R.layout.openhabwidgetlist_genericitem),
    FRAME(2, "Frame", R.layout.openhabwidgetlist_frameitem),
    GROUP(3, "Group", R.layout.openhabwidgetlist_groupitem),
    SWITCH(4, "Switch", R.layout.openhabwidgetlist_switchitem),
    TEXT(5, "Text", R.layout.openhabwidgetlist_textitem),
    SLIDER(6, "Slider", R.layout.openhabwidgetlist_slideritem),
    IMAGE(7, "Image", R.layout.openhabwidgetlist_imageitem),
    SELECTION(8, "Selection", R.layout.openhabwidgetlist_selectionitem),
    SECTIONSWITCH(9, "Switch", R.layout.openhabwidgetlist_sectionswitchitem),
    ROLLERSHUTTER(10, "RollershutterItem", R.layout.openhabwidgetlist_rollershutteritem),
    SETPOINT(11, "Setpoint", R.layout.openhabwidgetlist_setpointitem),
    CHART(12, "Chart", R.layout.openhabwidgetlist_chartitem),
    VIDEO(13, "Video", R.layout.openhabwidgetlist_videoitem),
    WEB(14, "Webview", R.layout.openhabwidgetlist_webitem),
    COLOR(15, "Colorpicker", R.layout.openhabwidgetlist_coloritem);

    public final String Name;
    public final int Id;
    public final int LayoutId;

    private OpenHABWidgetType(int id, String name, int layoutId) {
        Id = id;
        Name = name;
        LayoutId = layoutId;
    }
}
