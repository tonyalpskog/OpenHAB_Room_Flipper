package org.openhab.domain.command;

import org.openhab.domain.model.OpenHABWidgetType;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum OpenHABWidgetCommandType {
    GetStatus(0, "Get status", OpenHABWidgetType.GenericItem),
    SwitchOn(1, "Switch on", OpenHABWidgetType.Switch),
    SwitchOff(2, "Switch off", OpenHABWidgetType.Switch),
    RollerShutterDown(3, "Roll down", OpenHABWidgetType.RollerShutter),
    RollerShutterUp(4, "Roll up", OpenHABWidgetType.RollerShutter),
    RollerShutterStop(5, "Stop roller", OpenHABWidgetType.RollerShutter),
    SliderSetPercentage(6, "Set percentage", OpenHABWidgetType.Slider),
    AdjustSetpoint(7, "Adjust setpoint", OpenHABWidgetType.Setpoint);
//    GotoGroup(8, "Goto group", OpenHABWidgetType.Group);

    public final int Id;
    public final String Name;
    public final OpenHABWidgetType WidgetType;

    private OpenHABWidgetCommandType(int id, String name, OpenHABWidgetType widgetType) {
        Id = id;
        Name = name;
        WidgetType = widgetType;
    }
}
