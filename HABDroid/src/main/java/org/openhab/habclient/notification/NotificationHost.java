package org.openhab.habclient.notification;

import android.app.Notification;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.openhab.domain.SenderType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.user.User;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.auto.AutoMessageReadReceiver;
import org.openhab.habclient.auto.AutoMessageReplyReceiver;
import org.openhab.habclient.auto.AutoUnreadConversationManager;
import org.openhab.habclient.auto.IAutoUnreadConversationManager;
import org.openhab.habclient.wear.IWearCommandHost;
import org.openhab.habclient.wear.WearCommandHost;
import org.openhab.habclient.wear.WearNotificationReplyHandler;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.MyWebImage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tony Alpskog in 2015.
 */
public class NotificationHost implements INotificationSender {
    private final Context mContext;
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;
    private IWearCommandHost mWearCommandHost;
    private AutoMessageReadReceiver mAutoMessageReadReceiver;
    private AutoMessageReplyReceiver mAutoMessageReplyReceiver;
    private WearNotificationReplyHandler mWearNotificationReplyHandler;
    private boolean mIsRegistered;
    @Inject IOpenHABSetting mOpenHABSetting;

    @Inject
    public NotificationHost(Context context,
                           IAutoUnreadConversationManager autoUnreadConversationManager,
                           IWearCommandHost wearCommandHost) {
        mContext = context;
        mAutoUnreadConversationManager = autoUnreadConversationManager;
        mWearCommandHost = wearCommandHost;
    }

    @Override
    public void registerReceivers() {
        if(mIsRegistered)
            return;

        //Android Wear
        IntentFilter wearReadFilter = new IntentFilter();
        wearReadFilter.addAction(WearCommandHost.EXTRA_REPLY);
        mWearNotificationReplyHandler = new WearNotificationReplyHandler();
        mContext.registerReceiver(mWearNotificationReplyHandler, wearReadFilter);

        //Android Auto
        IntentFilter autoReadFilter = new IntentFilter();
        autoReadFilter.addAction(AutoUnreadConversationManager.NOTIFICATION_READ_ACTION);
        mAutoMessageReadReceiver = new AutoMessageReadReceiver();
        mContext.registerReceiver(mAutoMessageReadReceiver, autoReadFilter);

        IntentFilter autoReplyFilter = new IntentFilter();
        autoReplyFilter.addAction(AutoUnreadConversationManager.NOTIFICATION_REPLY_ACTION);
        mAutoMessageReplyReceiver = new AutoMessageReplyReceiver();
        mContext.registerReceiver(mAutoMessageReplyReceiver, autoReplyFilter);

        mIsRegistered = true;
    }

    @Override
    public void unregisterReceivers() {
        NotificationManagerCompat.from(mContext).cancel(0);
        mContext.unregisterReceiver(mWearNotificationReplyHandler);

        //Android Auto
//        mContext.unregisterReceiver(mAutoMessageReadReceiver);
//        mContext.unregisterReceiver(mAutoMessageReplyReceiver);

        mIsRegistered = false;
    }

    @Override
    public void startSession(SenderType senderType, String title, String message) {
        showNotification(senderType, title, null, message, mWearCommandHost.getNotificationActions(), new long[]{});
    }

    @Override
    public void startSession(String title, OpenHABWidget openHABWidget, String message) {
        showNotification(SenderType.System, title, getOpenHABImageUrlString(openHABWidget), message, mWearCommandHost.getNotificationActions(), mWearNotificationReplyHandler.getMediumPriorityVibratePattern());
    }

    @Override
    public void startSession(String title, User user, String message) {
        showNotification(SenderType.User, title, user.getImageUrl(), message, mWearCommandHost.getNotificationActions(), mWearNotificationReplyHandler.getMediumPriorityVibratePattern());
    }

    private String getOpenHABImageUrlString(OpenHABWidget openHABWidget) {
        if(openHABWidget == null)
            return null;
        return mOpenHABSetting.getBaseUrl() + "images/" + Uri.encode(openHABWidget.getIcon() + ".png");
    }

    @Override
    public void showNotification(SenderType senderType, String title, String titleIconUrl, String message, NotificationCompat.Action[] actions, long[] vibratePattern/*, NotificationCompat.Builder preBuiltPrio, ...*/) {//TODO - More injection
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
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
