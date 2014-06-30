package org.openhab.habclient.command;

import android.content.Context;

import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABWidgetType;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetCommandType {
    GetStatus(0, "Get status", R.array.command_phrases_get_status, OpenHABWidgetType.GenericItem),
    SwitchOn(1, "Switch on", R.array.command_phrases_switch_on, OpenHABWidgetType.Switch),
    SwitchOff(2, "Switch off", R.array.command_phrases_switch_off, OpenHABWidgetType.Switch),
    RollerShutterDown(3, "Roll down", R.array.command_phrases_roller_down, OpenHABWidgetType.RollerShutter),
    RollerShutterUp(4, "Roll up", R.array.command_phrases_roller_up, OpenHABWidgetType.RollerShutter),
    RollerShutterStop(5, "Stop roller", R.array.command_phrases_roller_stop, OpenHABWidgetType.RollerShutter),
    SliderSetPercentage(6, "Set percentage", R.array.command_phrases_percent, OpenHABWidgetType.Slider),
    AdjustSetpoint(7, "Adjust setpoint", R.array.command_phrases_setpoint, OpenHABWidgetType.Setpoint);
//    Toggle(8, "Toggle", R.array.command_phrases_toggle, OpenHABWidgetType.GenericItem);


//    Switch(4, "Switch", R.layout.openhabwidgetlist_switchitem, R.layout.openhabwidget_control_switchitem, true),
//    ItemText(5, "Text", R.layout.openhabwidgetlist_textitem, R.layout.openhabwidget_control_textitem, false),
//    SitemapText(6, "Text", R.layout.openhabwidgetlist_textitem, R.layout.openhabwidget_control_textitem, false),
//    Slider(7, "Slider", R.layout.openhabwidgetlist_slideritem, R.layout.openhabwidget_control_slideritem, true),
//    Image(8, "Image", R.layout.openhabwidgetlist_imageitem, -1, false),
//    Selection(9, "Selection", R.layout.openhabwidgetlist_selectionitem, -1, false),
//    SelectionSwitch(10, "Switch", R.layout.openhabwidgetlist_sectionswitchitem, -1, false),
//    RollerShutter(11, "RollershutterItem", R.layout.openhabwidgetlist_rollershutteritem, -1, false),
//    Setpoint(12, "Setpoint", R.layout.openhabwidgetlist_setpointitem, -1, false),
//    Chart(13, "Chart", R.layout.openhabwidgetlist_chartitem, -1, false),
//    Video(14, "Video", R.layout.openhabwidgetlist_videoitem, -1, false),
//    Web(15, "Webview", R.layout.openhabwidgetlist_webitem, -1, false),
//    Color(16, "Colorpicker", R.layout.openhabwidgetlist_coloritem, -1, false);


    public final int Id;
    public final String Name;
    public final int ArrayNameId;
    public final OpenHABWidgetType WidgetType;
//    public final WidgetCommandType CommandType;

    private OpenHABWidgetCommandType(int id, String name, int arrayNameId, OpenHABWidgetType widgetType) {
        Id = id;
        Name = name;
        ArrayNameId = arrayNameId;
        WidgetType = widgetType;
    }

    public String[] getTextCommands(Context context) {
        return context.getResources().getStringArray(ArrayNameId);
    }
}
