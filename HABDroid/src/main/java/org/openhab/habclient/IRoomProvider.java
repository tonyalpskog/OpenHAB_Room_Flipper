package org.openhab.habclient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IRoomProvider {
    HashMap<UUID, Room> getRoomHash();

    UUID getInitialRoomId();

    void saveRoom(Room room);

    Room get(UUID uuid);

    Room getInitialRoom();

    Room createNewRoom();

    Room addRoom(Room room);

    Room removeRoom(Room room);

    Map<String, Room> getMapOfRoomNames();
}
