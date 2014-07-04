package org.openhab.habclient;

import org.openhab.domain.IRoomProvider;
import org.openhab.domain.model.Room;
import org.openhab.domain.util.ILogger;

import java.util.UUID;

import javax.inject.Inject;

public class RoomDataContainer implements IRoomDataContainer {
    private static final String TAG = "RoomDataContainer";
    private final ILogger mLogger;
    private final IRoomProvider mRoomProvider;
    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;

    @Inject
    public RoomDataContainer(ILogger logger,
                             IRoomProvider roomProvider) {
        mLogger = logger;
        mRoomProvider = roomProvider;
    }

    @Override
    public Room getConfigRoom() {
        if(currentConfigRoom == null)
            return null;

        return getRoom(currentConfigRoom);
//        Log.d(getLogTag(), String.format("Getting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupWidgetId(), room.getId()));
    }

    @Override
    public void setConfigRoom(Room room) {
        if(room != null) {
            mLogger.d(TAG, String.format("Setting config room: name = '%s'  widget UUID = '%s' room UUID = '%s'", room.getName(), room.getRoomWidget() != null ? room.getRoomWidget().getId() : "<Widget is NULL>", room.getId()));
            currentConfigRoom = room.getId();
        } else {
            mLogger.d(TAG, "Setting config room: New Room");
            currentConfigRoom = null;
        }
    }

    @Override
    public Room getFlipperRoom() {
        Room room = getRoom(currentFlipperRoom);
        currentFlipperRoom = room.getId();
        return room;
    }

    @Override
    public void setFlipperRoom(Room room) {
        currentFlipperRoom = room.getId();
    }

    private Room getRoom(UUID roomId) {
        if(roomId == null)
            return mRoomProvider.getInitialRoom();
        else
            return mRoomProvider.get(roomId);
    }

}
