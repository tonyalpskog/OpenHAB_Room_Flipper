package com.zenit.habclient.command;

import com.zenit.habclient.Room;
import org.openhab.habdroid.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandAnalyzerResult {
    protected Room Room;
    protected OpenHABWidget OpenHABWidget;
    protected int matchPoint;
    protected String openHABItemState;
    protected OpenHABWidgetCommandType commandType;

    public CommandAnalyzerResult(com.zenit.habclient.Room room, org.openhab.habdroid.model.OpenHABWidget openHABWidget, int matchPoint, String openHABItemState, OpenHABWidgetCommandType commandType) {
        Room = room;
        OpenHABWidget = openHABWidget;
        this.matchPoint = matchPoint;
        this.openHABItemState = openHABItemState;
        this.commandType = commandType;
    }

    public Room getRoom() {
        return Room;
    }

    public void setRoom(Room room) {
        Room = room;
    }

    public OpenHABWidget getOpenHABWidget() {
        return OpenHABWidget;
    }

    public void setOpenHABWidget(OpenHABWidget openHABWidget) {
        OpenHABWidget = openHABWidget;
    }

    public int getMatchPoint() {
        return matchPoint;
    }

    public void setMatchPoint(int matchPoint) {
        this.matchPoint = matchPoint;
    }

    public String getOpenHABItemState() {
        return openHABItemState;
    }

    public void setOpenHABItemState(String openHABItemState) { this.openHABItemState = openHABItemState; }

    public OpenHABWidgetCommandType getCommandType() { return commandType; }

    public void setCommandType(OpenHABWidgetCommandType commandType) { this.commandType = commandType; }
}
