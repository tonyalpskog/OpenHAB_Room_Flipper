package com.zenit.habclient;

import android.graphics.Bitmap;

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
    private HashMap<UUID, GraphicUnit> unitHash = null;
    private Bitmap mBackgroundImage = null;

    public Room(String name, Bitmap roomImage) {
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

    public UUID getId() {
        return id;
    }

    public Bitmap getRoomImage() {
        return mBackgroundImage;
    }

    public void setRoomImage(Bitmap bitmap) {
        mBackgroundImage = bitmap;
    }

    public void addUnit(GraphicUnit gUnit) {
        unitHash.put(gUnit.getId(), gUnit);
    }

    public Iterator<GraphicUnit> getUnitIterator() {
        return unitHash.values().iterator();
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
}
