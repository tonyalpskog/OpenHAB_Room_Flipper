package com.zenit.habclient;

import android.app.Application;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {
    private HashMap<UUID, HashMap<UUID, GraphicUnit>> roomUnitList = new HashMap<UUID, HashMap<UUID, GraphicUnit>>();
    private RoomProvider mRoomProvider = null;
    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;
    private RuleOperationProvider mRuleOperationProvider = null;

    public Room getConfigRoom() {
        Room room = getRoom(currentConfigRoom);
        currentConfigRoom = room.getId();
        return room;
    }

    public void setConfigRoom(Room room) {
        currentConfigRoom = room.getId();
    }

    public Room getFlipperRoom() {
        Room room = getRoom(currentFlipperRoom);
        currentFlipperRoom = room.getId();
        return room;
    }

    public void setFlipperRoom(Room room) {
        currentFlipperRoom = room.getId();
    }

    private Room getRoom(UUID roomId) {
        if(mRoomProvider == null)
            mRoomProvider = new RoomProvider(getApplicationContext());

        if(roomId == null)
            return mRoomProvider.getInitialRoom();
        else
            return mRoomProvider.get(roomId);
    }

    public RoomProvider getRoomProvider() {
        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        if(mRuleOperationProvider == null)
            mRuleOperationProvider = new RuleOperationProvider();

        return mRuleOperationProvider;
    }

}
