package org.openhab.habclient;

public interface IRoomDataContainer {
    Room getConfigRoom();

    void setConfigRoom(Room room);

    Room getFlipperRoom();

    void setFlipperRoom(Room room);
}
