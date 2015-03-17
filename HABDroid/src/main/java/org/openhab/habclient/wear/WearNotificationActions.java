package org.openhab.habclient.wear;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import org.openhab.habclient.auto.IAutoUnreadConversationManager;
import org.openhab.habdroid.R;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WearNotificationActions implements IWearNotificationActions {
    public static final String WEAR_COMMAND_REPLY = "Wear_Command";
    public static final String WEAR_GROUP_MESSAGE = "Wear_GroupMessage";
    public static final String WEAR_PERSON_REPLY = "Wear_Reply";
    public static final String WEAR_NOTIFICATION_CONVERSATION_ID_KEY = "Android_Wear_Conversation_Id";
    public static final String ACTION_RESPONSE = "org.openhab.habclient.wear.notification.ACTION";

    private final Context mContext;
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;

    @Inject
    public WearNotificationActions(Context context, IAutoUnreadConversationManager unreadConversationManager) {
        mContext = context;
        mAutoUnreadConversationManager = unreadConversationManager;
    }
//
//    @Override
//    public void registerReceiver() {
//        mContext.registerReceiver(this, new IntentFilter(ACTION_RESPONSE));
//    }
//
//    @Override
//    public void unregisterReceiver() {
////        NotificationManagerCompat.from(mContext).cancel(0);
//        mContext.unregisterReceiver(mReceiver);
//    }

    public NotificationCompat.Action[] getNotificationActions(int conversationId) {
        if(mAutoUnreadConversationManager.isOpenHABSystemConversation(conversationId))
            return new NotificationCompat.Action[] {getGroupMessageAction(), getVoiceCommandAction(conversationId)};
        else
            return new NotificationCompat.Action[] {getPersonReplyMessageAction(conversationId)};
    }

    private NotificationCompat.Action getPersonReplyMessageAction(int conversationId) {
        // Create intent for action
        Intent intent = new Intent(ACTION_RESPONSE);
        intent.putExtra(WEAR_NOTIFICATION_CONVERSATION_ID_KEY, conversationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        //A simple, non-limited, voice input without pre-defined input patterns.
        RemoteInput remoteInput = new RemoteInput.Builder(WEAR_PERSON_REPLY)
                .setLabel("Listening...")
                .setAllowFreeFormInput(true)
                .build();

        //Create action
        return new NotificationCompat.Action.Builder(R.drawable.ic_white_reply,
                "Reply", pendingIntent)
                .addRemoteInput(remoteInput)
                .build();
    }

    private NotificationCompat.Action getVoiceCommandAction(int conversationId) {
        // Create intent for action
        Intent intent = new Intent(ACTION_RESPONSE);
        intent.putExtra(WEAR_NOTIFICATION_CONVERSATION_ID_KEY, conversationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(WEAR_COMMAND_REPLY).setLabel("Listening...").setAllowFreeFormInput(true).build();

        //Create action
        return new NotificationCompat.Action.Builder(R.drawable.ic_white_microphone,
            "Command", pendingIntent)
            .addRemoteInput(remoteInput)
            .build();
    }

    private NotificationCompat.Action getGroupMessageAction() {
        // Create intent for action
        Intent intent = new Intent(ACTION_RESPONSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        String[] replyChoices = mContext.getResources().getStringArray(R.array.wear_reply_choices);

        //A simple, non-limited, voice input without pre-defined input patterns.
        RemoteInput remoteInput = new RemoteInput.Builder(WEAR_GROUP_MESSAGE)
                .setLabel("Select or speak")
                .setChoices(replyChoices)
                .build();

        //Create action
        return new NotificationCompat.Action.Builder(R.drawable.ic_message_members_white,
                "Message", pendingIntent)
                .addRemoteInput(remoteInput)
                .build();
    }
}

