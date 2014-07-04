package org.openhab.habclient;

import android.graphics.Bitmap;

import org.openhab.domain.model.Room;

public interface IRoomImageProvider {
    Bitmap getRoomImage(Room room);
}
