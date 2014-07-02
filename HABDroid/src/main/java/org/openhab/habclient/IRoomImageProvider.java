package org.openhab.habclient;

import android.graphics.Bitmap;

public interface IRoomImageProvider {
    Bitmap getRoomImage(Room room);
}
