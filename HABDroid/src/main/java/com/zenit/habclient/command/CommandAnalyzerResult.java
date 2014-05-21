package com.zenit.habclient.command;

import com.zenit.habclient.Room;
import org.openhab.habdroid.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandAnalyzerResult {
    protected Room mRoom;
    protected OpenHABWidget mOpenHABWidget;
    protected int mMatchPoint;
    protected String mOpenHABItemState;
    protected OpenHABWidgetCommandType mCommandType;

    public CommandAnalyzerResult(com.zenit.habclient.Room room, org.openhab.habdroid.model.OpenHABWidget openHABWidget, int matchPoint, String openHABItemState, OpenHABWidgetCommandType commandType) {
        mRoom = room;
        mOpenHABWidget = openHABWidget;
        this.mMatchPoint = matchPoint;
        this.mOpenHABItemState = openHABItemState;
        this.mCommandType = commandType;
    }

    public Room getRoom() {
        return mRoom;
    }

    public void setRoom(Room room) {
        mRoom = room;
    }

    public OpenHABWidget getOpenHABWidget() {
        return mOpenHABWidget;
    }

    public void setOpenHABWidget(OpenHABWidget openHABWidget) {
        mOpenHABWidget = openHABWidget;
    }

    public int getMatchPoint() {
        return mMatchPoint;
    }

    public void setMatchPoint(int matchPoint) {
        this.mMatchPoint = matchPoint;
    }

    public String getOpenHABItemState() {
        return mOpenHABItemState;
    }

    public void setOpenHABItemState(String openHABItemState) { this.mOpenHABItemState = openHABItemState; }

    public OpenHABWidgetCommandType getCommandType() { return mCommandType; }

    public void setCommandType(OpenHABWidgetCommandType commandType) { this.mCommandType = commandType; }
}
