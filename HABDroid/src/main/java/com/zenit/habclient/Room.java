package com.zenit.habclient;

import android.graphics.Bitmap;
import android.util.Log;

import org.openhab.habdroid.model.OpenHABWidget;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class Room {
    private HashMap<Direction, Room> roomAlignment;

    private String mName;
    private UUID id;

    private String mHABGroupId;
    private String mSitemapId;
    private HashMap<UUID, GraphicUnit> unitHash = null;
    private Bitmap mBackgroundImage = null;//TA: TODO - Add a resource member as an alternative to a Bitmap. Bitmap may still be used as external image input and later on be replaced by a path or URL to an image.
    private static String TAG = "Room";
    private UUID mLatestWidgetUpdateUUID;

    //TA: TODO - Add a second constructor that replaces the Bitmap with an integer (resource ID) in order to save some memory.
    public Room(String sitemapId, Bitmap roomImage) {
        mSitemapId = sitemapId;
        mBackgroundImage = roomImage;
        id = UUID.randomUUID();
        roomAlignment = new HashMap<Direction, Room>(6);
        unitHash = new HashMap<UUID, GraphicUnit>();
    }

    public void setAlignment(Room room, Direction alignment) {
        roomAlignment.put(alignment, room);
    }

    public Room getRoomByAlignment(Direction direction) {
        return roomAlignment.get(direction);
    }

    public String getHABGroupId() {
        return mHABGroupId;
    }

    public void setHABGroupId(String HABGroupId) {
        mHABGroupId = HABGroupId;
    }

    public String getSitemapId() {
        return mSitemapId;
    }

    public void setSitemapId(String sitemapId) {
        mSitemapId = sitemapId;
    }

    public UUID isUpdated() {
        return mLatestWidgetUpdateUUID;
    }

    public UUID getId() {
        return id;
    }

    //TA: TODO - Modify this method to also be able to create a bitmap from a resource ID.
    public Bitmap getRoomImage() {
        return mBackgroundImage;
    }

    //TA: TODO - Override this method and replace the Bitmap with an integer (resource ID) in order to save some memory.
    public void setRoomImage(Bitmap bitmap) {
        mBackgroundImage = bitmap;
    }

    public void addUnit(GraphicUnit gUnit) {
        unitHash.put(gUnit.getId(), gUnit);
    }

    public void removeUnit(GraphicUnit gUnit) {
        unitHash.remove(gUnit.getId());
        //TA: TODO - Add widget removal here too
    }

    public Iterator<GraphicUnit> getUnitIterator() {
        return unitHash.values().iterator();
    }

    public boolean contains(OpenHABWidget widget) {
        boolean isContained = false;
        String strLogAll = "full Room widget list: ";
        Iterator<GraphicUnit> iterator = unitHash.values().iterator();//TA: TODO - What if a widget is removed from server? Must add a watch dog for that.
        while(iterator.hasNext()) {
            GraphicUnit gu = iterator.next();
            strLogAll += gu.getOpenHABWidget().getId() + ", ";
            if(gu.getOpenHABWidget().getId().equals(widget.getId()))
                isContained = true;
        }
        Log.d(TAG, "contains() -> " + widget.getId() + " is " + (isContained? "": "NOT") + " contained in " + strLogAll);
        return isContained;
    }

    public GraphicUnit getUnit(UUID unitId) {
        return unitHash.get(unitId);
    }

    public String getName() {
        if(HABApplication.getOpenHABWidgetProvider().hasWidget(mHABGroupId))
            return HABApplication.getOpenHABWidgetProvider().getWidget(mHABGroupId).getLabel();

        return "<No name>";
    }

//    public void setName(String name) {
//        this.mName = name;
//    }

    public String toString() {
        return mName;
    }
}
