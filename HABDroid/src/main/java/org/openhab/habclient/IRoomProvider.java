package org.openhab.habclient;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface IRoomProvider {
    Collection<Room> getAllRooms();

    UUID getInitialRoomId();

    void saveRoom(Room room);

    Room get(UUID uuid);

    Room getInitialRoom();

    Room createNewRoom();

    Room addRoom(Room room);

    Room removeRoom(Room room);

    Map<String, Room> getMapOfRoomNames();
}
