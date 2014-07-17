package org.openhab.domain.model;

import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnit {
    private OpenHABWidget mOpenHABWidget;
    private UUID id;
//    private OpenHABWidgetType type;
    private float roomRelativeX = 0;
    private float roomRelativeY = 0;
    private UUID mLatestWidgetUpdateUUID;
    private boolean isSelected;

    public GraphicUnit(OpenHABWidget openHABWidget) {
        mOpenHABWidget = openHABWidget;

        isSelected = false;
        this.id = UUID.randomUUID();

        setRoomRelativeX(3);
        setRoomRelativeY(4);

        EventBus.getDefault().registerSticky(this);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public UUID getId() {
        return id;
    }

    public OpenHABWidgetType getType() {
        return getOpenHABWidget().getType();
    }

    public float getRoomRelativeX() {
        return roomRelativeX;
    }

    public void setRoomRelativeX(float roomRelativeX) {
        this.roomRelativeX = roomRelativeX;
    }

    public float getRoomRelativeY() {
        return roomRelativeY;
    }

    public void setRoomRelativeY(float roomRelativeY) {
        this.roomRelativeY = roomRelativeY;
    }

    public OpenHABWidget getOpenHABWidget() {
        return mOpenHABWidget;
    }

    public void onEvent(OpenHABWidgetEvent widgetEvent){
        if(mOpenHABWidget.getId().equalsIgnoreCase(widgetEvent.getOpenHABWidget().getId()))
            mOpenHABWidget = widgetEvent.getOpenHABWidget();
    }
}
