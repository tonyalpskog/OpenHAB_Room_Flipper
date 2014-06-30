package org.openhab.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;

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

    private String mGroupWidgetId;
//    private String mSitemapId;
    private HashMap<UUID, GraphicUnit> unitHash = null;
    private static String TAG = "Room";
    private UUID mLatestWidgetUpdateUUID;
    private OpenHABWidget mLocalWidget;
    private Context mContext;
    private Integer mBackgroundImageResourceId;
    private final IOpenHABWidgetProvider mWidgetProvider;
    private final ILogger mLogger;
    private final IColorParser mColorParser;

    public Room(String groupItemName, String name, int backgroundImageResourceId, Context context,
                ILogger logger, IColorParser colorParser, IOpenHABWidgetProvider widgetProvider) {
        this(groupItemName, name, logger, colorParser, widgetProvider);

        mBackgroundImageResourceId = backgroundImageResourceId;
        mContext = context;
    }

    public Room(String groupItemId, String name, ILogger logger, IColorParser colorParser,
                IOpenHABWidgetProvider widgetProvider) {
        this(logger, colorParser, widgetProvider);
        mGroupWidgetId = groupItemId;
        if(groupItemId == null || groupItemId.isEmpty())
            mLocalWidget = new OpenHABWidget(logger, colorParser);
        mName = name;
        id = UUID.randomUUID();
        roomAlignment = new HashMap<Direction, Room>(6);
        unitHash = new HashMap<UUID, GraphicUnit>();
    }

    private Room(ILogger logger, IColorParser colorParser, IOpenHABWidgetProvider widgetProvider) {
        mLogger = logger;
        mColorParser = colorParser;
        mWidgetProvider = widgetProvider;
    }

    public Room shallowClone() {
        Room copy = new Room(mLogger, mColorParser, mWidgetProvider);
        copy.roomAlignment = (HashMap<Direction, Room>) this.roomAlignment.clone();
        copy.mName = this.mName;
        copy.id = this.id;
        copy.mGroupWidgetId = this.mGroupWidgetId;
        copy.unitHash = (HashMap<UUID, GraphicUnit>) this.unitHash.clone();
        copy.mLatestWidgetUpdateUUID = this.mLatestWidgetUpdateUUID;
        copy.mLocalWidget = this.mLocalWidget;
        copy.mContext = this.mContext;
        copy.mBackgroundImageResourceId = this.mBackgroundImageResourceId;

        return copy;
    }

    private Bitmap getBitmap(int bitmapResourceId) {
        return BitmapFactory.decodeResource(mContext.getResources(), bitmapResourceId);
    }

    public void setAlignment(Room room, Direction alignment) {
        roomAlignment.put(alignment, room);
    }

    public Room getRoomByAlignment(Direction direction) {
        return roomAlignment.get(direction);
    }

    //Check if this room has alignments to input room
    public boolean contains(Room room) {
        for (Room alignmentRoom : roomAlignment.values()) {
            if (room.id == alignmentRoom.getId())
                return true;
        }
        return false;
    }

    //Remove all alignments to input room
    public void removeAlignment(Room room) {
        Iterator iterator = roomAlignment.values().iterator();
        while(iterator.hasNext()) {
            Room alignmentRoom = (Room) iterator.next();
            if(room.id == alignmentRoom.getId())
                roomAlignment.remove(alignmentRoom);
        }
    }

    public String getGroupWidgetId() {
        return mGroupWidgetId;
    }

    public void setGroupWidgetId(String groupWidgetId) {
        mGroupWidgetId = groupWidgetId;
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
        //Bitmap bitmap= Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888);
//        Bitmap mutableBitmap = mBackgroundImageId.copy(Bitmap.Config.ARGB_8888, true);
//       mutableBitmap.eraseColor(Color.WHITE);
//        return invertBitmap(mutableBitmap);

//        mBackgroundImageId = invertBitmap(/*adjustOpacity(*/setColorAsAlfa(Color.BLACK, mBackgroundImageId)/*, 0*/);//);
        return getBitmap(mBackgroundImageResourceId);
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
//        if(HABApplication.getOpenHABWidgetProvider2().hasWidgetID(mGroupWidgetId)) {
//            OpenHABWidget widget = HABApplication.getOpenHABWidgetProvider2().getWidgetByID(mGroupWidgetId);
//            if(widget.getLabel() == null) {
//                Log.w(HABApplication.getLogTag(), String.format("\n%s\nNo label found for Room widget ID '%s'", HABApplication.getLogTag(1), mGroupWidgetId));
//                return "<No label>";
//            }
//            Log.d(HABApplication.getLogTag(), String.format("Group ID '%s' got label: '%s'", mGroupWidgetId, widget.getLabel()));
//            return widget.getLabel();
//        }
//
//        Log.w(HABApplication.getLogTag(), String.format("\n%s\nNo Room widget found with ID '%s'", HABApplication.getLogTag(1), mGroupWidgetId));
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

        return mWidgetProvider.getWidgetByID(mGroupWidgetId);
    }

    public Bitmap setPointAsAlfa(int x, int y, Bitmap source) {
        int pixelColor = source.getPixel(x, y);
        return setColorAsAlfa(pixelColor, source);
    }

    public Bitmap setColorAsAlfa(int color, Bitmap source) {
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        int height = source.getHeight();
        int width = source.getWidth();

        for (int yPos = 0; yPos < height; yPos++) {
            for (int xPos = 0; xPos < width; xPos++) {
                if(target.getPixel(xPos, yPos) == color)
                    target.setPixel(xPos, yPos, Color.alpha(color));
            }
        }

        return target;
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

    public void dispose() {

    }

//    private Bitmap invertBitmap2(Bitmap source) {
//        float invert[] =
//                {
//                        -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
//                        0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
//                        0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
//                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
//                };
//        ColorMatrix cm = new ColorMatrix(invert);
//        invertPaint.setColorFilter(new ColorMatrixColorFilter(cm));
//        c.drawBitmap(source, null, Screen, invertPaint);
//    }


//    private static final int RGB_MASK = 0x00FFFFFF;
//
//    public Bitmap invertBitmap3(Bitmap inversion) {
//        // Create mutable Bitmap to invert, argument true makes it mutable
//        //Bitmap inversion = original.copy(Bitmap.Config.ARGB_8888, true);
//
//        // Get info about Bitmap
//        int width = inversion.getWidth();
//        int height = inversion.getHeight();
//        int pixels = width * height;
//
//        // Get original pixels
//        int[] pixel = new int[pixels];
//        inversion.getPixels(pixel, 0, width, 0, 0, width, height);
//
//        // Modify pixels
//        for (int i = 0; i < pixels; i++)
//            pixel[i] ^= RGB_MASK;
//        inversion.setPixels(pixel, 0, width, 0, 0, width, height);
//
//        // Return inverted Bitmap
//        return inversion;
//    }
//
//    private Bitmap invertBitmap4(Bitmap myBitmap) {
//        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
//
//        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
//
//        for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++){
//
//            if( allpixels[i] == Color.BLACK)
//                allpixels[i] = Color.RED;
//        }
//
//        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
//        return myBitmap;
//    }
}
