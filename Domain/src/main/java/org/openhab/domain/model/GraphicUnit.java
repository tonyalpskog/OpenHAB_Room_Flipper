package org.openhab.domain.model;

import org.openhab.domain.IEventBus;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnit {
    private OpenHABWidget mOpenHABWidget;
    private final IEventBus mEventBus;
    private UUID id;
//    private OpenHABWidgetType type;
    private float roomRelativeX = 0;
    private float roomRelativeY = 0;
    private UUID mLatestWidgetUpdateUUID;
    private boolean isSelected;

    public GraphicUnit(OpenHABWidget openHABWidget, IEventBus eventBus) {
        mOpenHABWidget = openHABWidget;
        mEventBus = eventBus;

        isSelected = false;
        this.id = UUID.randomUUID();

        setRoomRelativeX(3);
        setRoomRelativeY(4);

        //TODO: why run logic in constructor?
        mEventBus.registerSticky(this);
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
