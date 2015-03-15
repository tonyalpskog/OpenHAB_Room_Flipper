package org.openhab.habclient.notification;

import android.app.Notification;
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
import org.openhab.habclient.wear.IWearCommandHost;
import org.openhab.habclient.wear.WearNotificationReplyHandler;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.MyWebImage;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class NotificationSender implements INotificationSender {
    private final Context mContext;
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;
    private final IWearCommandHost mWearCommandHost;
    private final IOpenHABSetting mOpenHABSetting;

    @Inject
    public NotificationSender(@ApplicationContext Context context,
                              IAutoUnreadConversationManager autoUnreadConversationManager,
                              IWearCommandHost wearCommandHost,
                              IOpenHABSetting openHABSetting) {
        mContext = context;
        mAutoUnreadConversationManager = autoUnreadConversationManager;
        mWearCommandHost = wearCommandHost;
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
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        NotificationCompat.Action[] actions = mWearCommandHost.getNotificationActions();
        for(NotificationCompat.Action action : actions) {
            wearableExtender.addAction(action);
        }

        wearableExtender.setBackground(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.openhab_320x320));

        //Android Auto
        mAutoUnreadConversationManager.addMessageToUnreadConversations(title, message);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.openhabicon)//App icon
                .setWhen(System.currentTimeMillis())
                .setVibrate(vibratePattern)
                .extend(wearableExtender);//Wear
        addUnreadConversations(builder, mAutoUnreadConversationManager.getUnreadConversations());

        new SendNotification(mContext).execute(new NotificationContent(builder, senderType, titleIconUrl));
    }

    //Auto
    private void addUnreadConversations(NotificationCompat.Builder builder, NotificationCompat.CarExtender.UnreadConversation[] conversationArray) {
        for(NotificationCompat.CarExtender.UnreadConversation conversation : conversationArray) {
            builder.extend(new NotificationCompat.CarExtender().setUnreadConversation(conversation));
        }
    }

    private class SendNotification extends AsyncTask<NotificationContent, Void, Notification> {
        Context mContext;

        public SendNotification(Context context) {
            super();
            this.mContext = context;
        }

        @Override
        protected Notification doInBackground(NotificationContent... params) {
            NotificationContent notificationContent = params[0];
            NotificationCompat.Builder builder = notificationContent.getBuilder();
            String largeIconUrl = notificationContent.getLargeIconUrl();
            MyWebImage webImage = new MyWebImage(largeIconUrl, false, mOpenHABSetting.getUsername(), mOpenHABSetting.getPassword());
            Bitmap largeIcon = webImage.getBitmap(mContext);
            if(largeIcon == null) {
                largeIcon = BitmapFactory.decodeResource(mContext.getResources(),
                        notificationContent.getSenderType().equals(SenderType.System)? R.drawable.openhabicon_light : R.drawable.default_user);
            }
            builder.setLargeIcon(largeIcon);// Conversation/Sender icon
            return builder.build();
        }

        @Override
        protected void onPostExecute(Notification notification) {

            super.onPostExecute(notification);
            try {
                NotificationManagerCompat.from(mContext).notify(0, notification);
            } catch (Exception e) {
                Log.e("NotificationHost", "Could not send a notification.", e);
            }
        }
    }

    public class NotificationContent {
        String mLargeIconUrl;
        NotificationCompat.Builder mBuilder;
        SenderType mSenderType;

        public NotificationContent(NotificationCompat.Builder builder, SenderType senderType, String largeIconUrl) {
            mBuilder = builder;
            mSenderType = senderType;
            mLargeIconUrl = largeIconUrl;
        }

        public String getLargeIconUrl() {
            return mLargeIconUrl;
        }

        public NotificationCompat.Builder getBuilder() {
            return mBuilder;
        }

        public SenderType getSenderType() { return mSenderType; }
    }
}
