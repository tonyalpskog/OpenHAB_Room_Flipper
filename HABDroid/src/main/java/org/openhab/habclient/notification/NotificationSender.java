package org.openhab.habclient.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.openhab.domain.INotificationSender;
import org.openhab.domain.SenderType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.user.User;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.auto.IAutoUnreadConversationManager;
import org.openhab.habclient.dagger.ApplicationContext;
import org.openhab.habclient.wear.IWearNotificationActions;
import org.openhab.habclient.wear.WearNotificationReplyHandler;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.MyWebImage;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class NotificationSender implements INotificationSender {
    private final Context mContext;
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;
    private final IWearNotificationActions mWearNotificationActions;
    private final IOpenHABSetting mOpenHABSetting;
    private final String TAG = "NotificationSender";

    @Inject
    public NotificationSender(@ApplicationContext Context context,
                              IAutoUnreadConversationManager autoUnreadConversationManager,
                              IWearNotificationActions wearNotificationActions,
                              IOpenHABSetting openHABSetting) {
        mContext = context;
        mAutoUnreadConversationManager = autoUnreadConversationManager;
        mWearNotificationActions = wearNotificationActions;
        mOpenHABSetting = openHABSetting;
    }



    @Override
    public void startSession(SenderType senderType, String title, String message) {
        showNotification(senderType, title, null, message, new long[]{});
    }

    @Override
    public void startSession(String title, OpenHABWidget openHABWidget, String message) {
        showNotification(SenderType.System, title, getOpenHABImageUrlString(openHABWidget), message, WearNotificationReplyHandler.MEDIUM_PRIORITY_VIBRATE_PATTERN);
    }

    @Override
    public void startSession(String title, User user, String message) {
        showNotification(SenderType.User, title, user.getImageUrl(), message, WearNotificationReplyHandler.MEDIUM_PRIORITY_VIBRATE_PATTERN);
    }

    private String getOpenHABImageUrlString(OpenHABWidget openHABWidget) {
        if(openHABWidget == null)
            return null;
        return mOpenHABSetting.getBaseUrl() + "images/" + Uri.encode(openHABWidget.getIcon() + ".png");
    }

    @Override
    public void showNotification(SenderType senderType, String title, String titleIconUrl, String message, long[] vibratePattern/*, NotificationCompat.Builder preBuiltPrio, ...*/) {//TODO - More injection
        //Android Auto
        int conversationId = mAutoUnreadConversationManager.getConservationId(senderType, title);
        Log.d("Notification.showNotification()", String.format("Before adding %s - \"%s\" as Conservation ID %d", title, message, conversationId));
        mAutoUnreadConversationManager.addMessageToUnreadConversations(conversationId, title, message);

        //Wear
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        NotificationCompat.Action[] actions = mWearNotificationActions.getNotificationActions(conversationId);
        for(NotificationCompat.Action action : actions) {
            wearableExtender.addAction(action);
        }

        Bitmap wearBackgroundBitmap = getBitmapFromUrl(titleIconUrl);
        if(wearBackgroundBitmap == null) {
            wearBackgroundBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    senderType.equals(SenderType.System)? R.drawable.openhab_320x320 : R.drawable.default_user);
        }
        wearableExtender.setBackground(wearBackgroundBitmap);

        //Mobile & Auto
        Bitmap senderBitmap = getBitmapFromUrl(titleIconUrl);
        if(senderBitmap == null) {
            senderBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    senderType.equals(SenderType.System)? R.drawable.openhabicon_light : R.drawable.default_user);
        }

        // Build the notification (for all device types)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.openhabicon)//App icon
                .setLargeIcon(senderBitmap)//Auto and Mobile
//                .setContentIntent(mAutoUnreadConversationManager.getMessageReadPendingIntent(conversationId))
                .setWhen(System.currentTimeMillis())
                .setVibrate(vibratePattern)
                .extend(wearableExtender);//Wear
        Log.d("Notification.showNotification()", String.format("Before adding unread messages. Conservation ID = %d", conversationId));
        addUnreadConversations(builder, mAutoUnreadConversationManager.getUnreadConversations());
        try {
            Log.d("Notification.showNotification()", String.format("Before sending %s - \"%s\" as Conservation ID %d", title, message, conversationId));
            NotificationManagerCompat.from(mContext).notify(conversationId, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Could not send a notification.", e);
        }
    }

    //Auto
    private void addUnreadConversations(NotificationCompat.Builder builder, NotificationCompat.CarExtender.UnreadConversation[] conversationArray) {
        for(NotificationCompat.CarExtender.UnreadConversation conversation : conversationArray) {
            builder.extend(new NotificationCompat.CarExtender().setUnreadConversation(conversation));
        }
    }

    private Bitmap getBitmapFromUrl(String url) {
        try {
            return new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    MyWebImage webImage = new MyWebImage(url, false, mOpenHABSetting.getUsername(), mOpenHABSetting.getPassword());
                    return webImage.getBitmap(mContext);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
