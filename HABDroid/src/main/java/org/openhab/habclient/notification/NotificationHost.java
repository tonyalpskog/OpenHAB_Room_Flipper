package org.openhab.habclient.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.NotificationManagerCompat;

import org.openhab.domain.INotificationHost;
import org.openhab.habclient.auto.AutoMessageReadReceiver;
import org.openhab.habclient.auto.AutoMessageReplyReceiver;
import org.openhab.habclient.auto.AutoUnreadConversationManager;
import org.openhab.habclient.dagger.ApplicationContext;
import org.openhab.habclient.wear.WearNotificationActions;
import org.openhab.habclient.wear.WearNotificationReplyHandler;

import javax.inject.Inject;

public class NotificationHost implements INotificationHost {
    private final Context mContext;
    private final AutoMessageReadReceiver mAutoMessageReadReceiver;
    private final AutoMessageReplyReceiver mAutoMessageReplyReceiver;
    private final WearNotificationReplyHandler mWearNotificationReplyHandler;
    private boolean mIsRegistered;

    @Inject
    public NotificationHost(@ApplicationContext Context context,
                            AutoMessageReadReceiver autoMessageReadReceiver,
                            AutoMessageReplyReceiver autoMessageReplyReceiver,
                            WearNotificationReplyHandler wearNotificationReplyHandler) {
        mContext = context;
        mAutoMessageReadReceiver = autoMessageReadReceiver;
        mAutoMessageReplyReceiver = autoMessageReplyReceiver;
        mWearNotificationReplyHandler = wearNotificationReplyHandler;
    }

    @Override
    public void registerReceivers() {
        if(mIsRegistered)
            return;

        //Android Wear
        IntentFilter wearReadFilter = new IntentFilter();
        wearReadFilter.addAction(WearNotificationActions.WEAR_COMMAND_REPLY);
        mContext.registerReceiver(mWearNotificationReplyHandler, wearReadFilter);

        //Android Auto
        IntentFilter autoReadFilter = new IntentFilter();
        autoReadFilter.addAction(AutoUnreadConversationManager.AUTO_NOTIFICATION_READ_ACTION);
        mContext.registerReceiver(mAutoMessageReadReceiver, autoReadFilter);

        IntentFilter autoReplyFilter = new IntentFilter();
        autoReplyFilter.addAction(AutoUnreadConversationManager.AUTO_NOTIFICATION_REPLY_ACTION);
        mContext.registerReceiver(mAutoMessageReplyReceiver, autoReplyFilter);

        mIsRegistered = true;
    }

    @Override
    public void unregisterReceivers() {
        NotificationManagerCompat.from(mContext).cancel(0);
        mContext.unregisterReceiver(mWearNotificationReplyHandler);

        //Android Auto
        mContext.unregisterReceiver(mAutoMessageReadReceiver);
        mContext.unregisterReceiver(mAutoMessageReplyReceiver);

        mIsRegistered = false;
    }

    @Override
    public boolean isRegistered() {
        return mIsRegistered;
    }
}
