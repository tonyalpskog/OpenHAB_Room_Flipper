package org.openhab.habclient;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetType;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnit {
    private UUID id;
//    private OpenHABWidgetType type;
    private float roomRelativeX = 0;
    private float roomRelativeY = 0;
    UnitContainerView mRoomView;
    private final IOpenHABWidgetProvider mWidgetProvider;
    private String mWidgetId;
    private UUID mLatestWidgetUpdateUUID;
    private boolean isSelected;

    public GraphicUnit(String widgetId, UnitContainerView roomView,
                       IOpenHABWidgetProvider widgetProvider) {
        mWidgetId = widgetId;
        mLatestWidgetUpdateUUID = widgetProvider.getUpdateUUID();
        mRoomView = roomView;
        mWidgetProvider = widgetProvider;

        isSelected = false;
        this.id = UUID.randomUUID();

        setRoomRelativeX(3);
        setRoomRelativeY(4);
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
        return mWidgetProvider.getWidgetByID(mWidgetId);
    }

    public void setOpenHABWidget(String itemName) {
        mWidgetId = itemName;
    }

    public UnitContainerView getUnitContainerView() {
        return mRoomView;
    }
}
