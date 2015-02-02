package org.openhab.domain.model;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class Room {
    private static final String TAG = "Room";

    private HashMap<Direction, Room> roomAlignment;
    private String mName;

    private UUID id;
    private String mGroupWidgetId;
    //    private String mSitemapId;
    private HashMap<UUID, GraphicUnit> unitHash = null;
    private UUID mLatestWidgetUpdateUUID;
    private OpenHABWidget mLocalWidget;
    private int mBackgroundImageResourceId;
    private final IOpenHABWidgetProvider mWidgetProvider;
    private final ILogger mLogger;
    private final IColorParser mColorParser;
    private String mBackgroundImageFilePath;

    public Room(String groupItemName, String name, int backgroundImageResourceId,
                ILogger logger, IColorParser colorParser, IOpenHABWidgetProvider widgetProvider) {
        this(groupItemName, name, logger, colorParser, widgetProvider);

        mBackgroundImageResourceId = backgroundImageResourceId;
    }

    public Room(String groupItemId, String name, ILogger logger, IColorParser colorParser,
                IOpenHABWidgetProvider widgetProvider) {
        this(logger, colorParser, widgetProvider);
        mGroupWidgetId = groupItemId;
        if(groupItemId == null || groupItemId.isEmpty())
            mLocalWidget = new OpenHABWidget(logger);
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
        copy.mBackgroundImageResourceId = this.mBackgroundImageResourceId;
        copy.mBackgroundImageFilePath = this.mBackgroundImageFilePath;
        
        return copy;
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
        for (Room alignmentRoom : roomAlignment.values()) {
            if (room.id == alignmentRoom.getId())
                roomAlignment.remove(alignmentRoom);
        }
    }

    public String getGroupWidgetId() {
        return mGroupWidgetId;
    }

    public void setGroupWidgetId(String groupWidgetId) {
        mGroupWidgetId = groupWidgetId;
    }

    public UUID isUpdated() {
        return mLatestWidgetUpdateUUID;
    }

    public UUID getId() {
        return id;
    }

    public void addUnit(GraphicUnit gUnit) {
        unitHash.put(gUnit.getId(), gUnit);
    }

    public void removeUnit(GraphicUnit gUnit) {
        unitHash.remove(gUnit.getId());
        //TA: TODO - Add widget removal here too
    }

    public Collection<GraphicUnit> getUnits() {
        return unitHash.values();
    }

    public boolean contains(OpenHABWidget widget) {
        boolean isContained = false;
        String strLogAll = "full Room widget list: ";
        for (GraphicUnit gu : unitHash.values()) {
            strLogAll += gu.getOpenHABWidget().getId() + ", ";
            if (gu.getOpenHABWidget().getId().equals(widget.getId()))
                isContained = true;
        }
        mLogger.d(TAG, "contains() -> " + widget.getId() + " is " + (isContained? "": "NOT") + " contained in " + strLogAll);
        return isContained;
    }

    public GraphicUnit getUnit(UUID unitId) {
        return unitHash.get(unitId);
    }

    public String getName() {
        return mName;
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

    public int getBackgroundImageResourceId() {
        return mBackgroundImageResourceId;
    }
    
    public String getBackgroundImageFilePath() { return mBackgroundImageFilePath; }
    public void setBackgroundImageFilePath(String backgroundImageFilePath) {
        mBackgroundImageFilePath = backgroundImageFilePath;
    }
}
