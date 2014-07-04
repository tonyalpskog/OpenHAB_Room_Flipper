package org.openhab.habclient;

import org.openhab.domain.model.Room;

public interface IRoomDataContainer {
    Room getConfigRoom();

    void setConfigRoom(Room room);

    Room getFlipperRoom();

    void setFlipperRoom(Room room);
}
