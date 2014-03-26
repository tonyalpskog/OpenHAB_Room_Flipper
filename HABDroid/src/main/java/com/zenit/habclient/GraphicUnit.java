package com.zenit.habclient;

import android.content.Context;
import android.widget.ImageView;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnit {

    public static int UNIT_SIZE = 64;

    private UUID id;
//    private OpenHABWidgetType type;
    private float roomRelativeX = 0;
    private float roomRelativeY = 0;
    private GraphicUnitWidget mView;
    UnitContainerView mRoomView;
    private String mWidgetId;
    private UUID mLatestWidgetUpdateUUID;
    private boolean isSelected;

    public GraphicUnit(String widgetId, UnitContainerView roomView) {
        mWidgetId = widgetId;
        mLatestWidgetUpdateUUID = HABApplication.getOpenHABWidgetProvider().getUpdateUUID();
        mRoomView = roomView;

        mView = null;
        isSelected = false;
        this.id = UUID.randomUUID();

        setRoomRelativeX(3);
        setRoomRelativeY(4);
    }

    public GraphicUnitWidget getGraphicUnitWidget() {
        return mView;
    }

    public ImageView getView(Context context) {
        if(mView == null) {
            mView = new GraphicUnitWidget(context, this);

//            mView.setTop(relativeTop);
//            mView.setLeft(relativeLeft);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(relativeLeft, relativeTop, 0, 0);
//            mView.setLayoutParams(params);
            mView.setMinimumWidth(UNIT_SIZE);
            mView.setMinimumHeight(UNIT_SIZE);
            mView.setTag(id);
            mView.setSelected(isSelected);
        }
        return mView;
    }

    public void resetView() {
        mView = null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if(mView != null) {
            mView.setSelected(isSelected);
            mView.drawSelection(isSelected);
        }
    }

    public UUID getId() {
        return id;
    }

    public OpenHABWidgetType getType() {
        return getOpenHABWidget().getType();
    }

//    public void setType(UnitType type) {
//        this.type = type;
//    }

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
        return HABApplication.getOpenHABWidgetProvider().getWidget(mWidgetId);
    }

    public void setOpenHABWidget(String itemName) {
        mWidgetId = itemName;
    }

    public UnitContainerView getUnitContainerView() {
        return mRoomView;
    }

    public void setUnitContainerView(UnitContainerView roomView) {
        this.mRoomView = roomView;
    }
}
