package org.openhab.habclient.wear;

import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IWearCommandHost {
    NotificationCompat.Action[] getNotificationActions();
}
