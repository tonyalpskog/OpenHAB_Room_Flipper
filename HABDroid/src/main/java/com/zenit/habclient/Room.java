package com.zenit.habclient;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.openhab.habdroid.model.OpenHABWidget;

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

    private String mGroupItemName;
//    private String mSitemapId;
    private HashMap<UUID, GraphicUnit> unitHash = null;
    private Bitmap mBackgroundImage = null;//TA: TODO - Add a resource member as an alternative to a Bitmap. Bitmap may still be used as external image input and later on be replaced by a path or URL to an image.
    private static String TAG = "Room";
    private UUID mLatestWidgetUpdateUUID;
    private OpenHABWidget mLocalWidget;

    //TA: TODO - Add a second constructor that replaces the Bitmap with an integer (resource ID) in order to save some memory.
    public Room(String groupItemName, String name, Bitmap roomImage) {
        mGroupItemName = groupItemName;
        if(groupItemName == null || groupItemName.isEmpty())
            mLocalWidget = new OpenHABWidget();
        mName = name;
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

    public String getGroupItemName() {
        return mGroupItemName;
    }

    public void setGroupItemName(String groupItemName) {
        mGroupItemName = groupItemName;
    }

//    public String getSitemapId() {
//        return mSitemapId;
//    }
//
//    public void setSitemapId(String sitemapId) {
//        mSitemapId = sitemapId;
//    }

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
        return mName;
//        if(HABApplication.getOpenHABWidgetProvider().hasWidget(mGroupItemName)) {
//            OpenHABWidget widget = HABApplication.getOpenHABWidgetProvider().getWidget(mGroupItemName);
//            if(widget.getLabel() == null) {
//                Log.w(HABApplication.getLogTag(), String.format("\n%s\nNo label found for Room widget ID '%s'", HABApplication.getLogTag(1), mGroupItemName));
//                return "<No label>";
//            }
//            Log.d(HABApplication.getLogTag(), String.format("Group ID '%s' got label: '%s'", mGroupItemName, widget.getLabel()));
//            return widget.getLabel();
//        }
//
//        Log.w(HABApplication.getLogTag(), String.format("\n%s\nNo Room widget found with ID '%s'", HABApplication.getLogTag(1), mGroupItemName));
//        return "<No widget>";
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String toString() {
        return mName;
    }

    public OpenHABWidget getRoomWidget() {
        if(mLocalWidget != null)
            return mLocalWidget;

        return HABApplication.getOpenHABWidgetProvider().getWidget(mGroupItemName);
    }

    public void setPointAsAlfa(int x, int y) {
        mBackgroundImage = setPointAsAlfa(x, y, mBackgroundImage);
    }

    public Bitmap setPointAsAlfa(int x, int y, Bitmap source) {
        int pixelColor = source.getPixel(x, y);

        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        int height = source.getHeight();
        int width = source.getWidth();

        for (int yPos = 0; yPos < height; yPos++) {
            for (int xPos = 0; xPos < width; xPos++) {
                target.setPixel(xPos, yPos, Color.alpha(pixelColor));
            }
        }

        return target;
    }

    public void invertRoomImage() {
        mBackgroundImage = invertBitmap(mBackgroundImage);
    }

    private Bitmap invertBitmap(Bitmap source) {
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = source.getHeight();
        int width = source.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelColor = source.getPixel(x, y);
                A = Color.alpha(pixelColor);

                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);

                target.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return target;
    }
}
