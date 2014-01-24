package com.zenit.habclient;

import android.content.Context;
import android.widget.ImageView;

import org.openhab.habdroid.model.OpenHABItemSet;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnit {

    private UUID id;
//    private OpenHABWidgetType type;
    private float roomRelativeX = 0;
    private float roomRelativeY = 0;
    private GraphicUnitWidget view;

    private String mWidgetId;
    private UUID mLatestWidgetUpdateUUID;
    private boolean isSelected;

    public GraphicUnit(UnitType type) {
        this(3, 4);
        //this.type = type;
    }

    public GraphicUnit(String widgetId) {
        this(3, 4);
        mWidgetId = widgetId;
        mLatestWidgetUpdateUUID = HABApplication.getOpenHABWidgetProvider().getUpdateUUID();
    }
    public GraphicUnit(int roomRelativeX, int roomRelativeY) {
        this.view = null;
        isSelected = false;
        this.roomRelativeX = roomRelativeX;
        this.roomRelativeX = roomRelativeX;
        this.id = UUID.randomUUID();
    }

    public ImageView getView(Context context) {
        if(view == null) {
            view = new GraphicUnitWidget(context, this);

//            view.setTop(relativeTop);
//            view.setLeft(relativeLeft);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(relativeLeft, relativeTop, 0, 0);
//            view.setLayoutParams(params);
            view.setMinimumWidth(64);
            view.setMinimumHeight(64);
            view.setTag(id);
            view.setSelected(isSelected);
        }
        return view;
    }

    public void resetView() {
        view = null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if(view != null) {
            view.setSelected(isSelected);
            view.drawSelection(isSelected);
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

    public void setOpenHABWidget(String widgetID) {
        mWidgetId = widgetID;
    }

}
