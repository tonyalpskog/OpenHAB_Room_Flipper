package org.openhab.habclient.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import javax.inject.Inject;

public class AutoMessageReadReceiver extends BroadcastReceiver {
    @Inject public IAutoUnreadConversationManager mAutoUnreadConversationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        int conversationId = intent.getIntExtra(AutoUnreadConversationManager.MESSAGE_CONVERSATION_ID_KEY, -1 );
        Log.d("Message", "id: " + conversationId);
        NotificationManagerCompat.from(context).cancel(conversationId);
        mAutoUnreadConversationManager.removeMessageFromUnreadConversations(conversationId);
    }
}
