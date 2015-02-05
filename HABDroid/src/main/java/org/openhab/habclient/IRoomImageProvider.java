package org.openhab.habclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.openhab.domain.model.Room;

public interface IRoomImageProvider {
    Bitmap getRoomImage(Room room, int maxWidth, int maxHeight);
}
