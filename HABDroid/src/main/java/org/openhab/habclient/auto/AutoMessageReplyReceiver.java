package org.openhab.habclient.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import org.openhab.domain.INotificationReplyHandler;

import javax.inject.Inject;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    @Inject INotificationReplyHandler mNotificationReplyHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Message Received", Toast.LENGTH_LONG).show();

        int conversationId = intent.getIntExtra(AutoUnreadConversationManager.NOTIFICATION_CONVERSATION_ID_KEY, -1);
        Log.d("Auto message REPLY", "conversation id = " + conversationId);
        NotificationManagerCompat.from(context).cancel(conversationId);

        String message = getMessageFromIntent(intent);
        Log.d("Auto message REPLY", "Reply string = " + message);
        mNotificationReplyHandler.handleReplyMessage(message, new long[] {});
    }

    private String getMessageFromIntent(Intent intent) {
        //Note that Android Auto does not currently allow voice responses in their simulator
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if( remoteInput != null && remoteInput.containsKey(AutoUnreadConversationManager.AUTO_VOICE_REPLY_KEY)) {
            return remoteInput.getCharSequence(AutoUnreadConversationManager.AUTO_VOICE_REPLY_KEY).toString();
        }
        return null;
    }
}