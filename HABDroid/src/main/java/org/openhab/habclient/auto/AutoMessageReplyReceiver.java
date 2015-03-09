package org.openhab.habclient.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Message Received", Toast.LENGTH_LONG).show();

        int conversationId = intent.getIntExtra(AutoUnreadConversationManager.MESSAGE_CONVERSATION_ID_KEY, -1);
        Log.d("Message", "id: " + conversationId);
        NotificationManagerCompat.from(context).cancel(conversationId);

        String message = getMessageFromIntent(intent);
    }

    private String getMessageFromIntent(Intent intent) {
        //Note that Android Auto does not currently allow voice responses in their simulator
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if( remoteInput != null && remoteInput.containsKey("extra_voice_reply")) {
            return remoteInput.getCharSequence("extra_voice_reply").toString();
        }
        return null;
    }
}