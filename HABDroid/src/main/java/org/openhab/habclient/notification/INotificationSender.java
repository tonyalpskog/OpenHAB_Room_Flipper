package org.openhab.habclient.notification;

import android.support.v4.app.NotificationCompat;

import org.openhab.domain.INotificationHost;
import org.openhab.domain.SenderType;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INotificationSender extends INotificationHost {
    void showNotification(SenderType senderType, String title, String titleIconUrl, String message, NotificationCompat.Action[] actions, long[] vibratePattern/*, NotificationCompat.Builder preBuiltPrio, ...*/);
}
