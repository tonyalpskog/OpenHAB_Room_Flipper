package org.openhab.habclient.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import javax.inject.Inject;

public class AutoMessageReadReceiver extends BroadcastReceiver {
    @Inject IAutoUnreadConversationManager mAutoUnreadConversationManager;

    @Inject
    public AutoMessageReadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int conversationId = intent.getIntExtra(AutoUnreadConversationManager.NOTIFICATION_CONVERSATION_ID_KEY, -1);
        Log.d("Auto message READ", "conversation id = " + conversationId);
        if(conversationId == -1)
            return;

        NotificationManagerCompat.from(context).cancel(conversationId);
        if(mAutoUnreadConversationManager == null)
            Log.e("Auto message READ", "AutoUnreadConversationManager is NULL");
        else
            mAutoUnreadConversationManager.removeMessageFromUnreadConversations(conversationId);
    }
}
