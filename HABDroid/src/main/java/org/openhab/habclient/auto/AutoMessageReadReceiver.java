package org.openhab.habclient.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import javax.inject.Inject;

public class AutoMessageReadReceiver extends BroadcastReceiver {
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;

    @Inject
    public AutoMessageReadReceiver(IAutoUnreadConversationManager autoUnreadConversationManager) {
        if(autoUnreadConversationManager == null) throw new IllegalArgumentException("autoUnreadConversationManager is null");
        mAutoUnreadConversationManager = autoUnreadConversationManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int conversationId = intent.getIntExtra(AutoUnreadConversationManager.AUTO_NOTIFICATION_CONVERSATION_ID_KEY, -1);
        Log.d("Auto message READ", "conversation id = " + conversationId);
        if(conversationId == -1)
            return;

        mAutoUnreadConversationManager.removeMessageFromUnreadConversations(conversationId);
        NotificationManagerCompat.from(context).cancel(conversationId);
    }
}
