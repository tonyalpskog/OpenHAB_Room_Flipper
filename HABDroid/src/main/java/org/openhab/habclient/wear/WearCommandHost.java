package org.openhab.habclient.wear;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import org.openhab.habclient.dagger.ApplicationContext;
import org.openhab.habdroid.R;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WearCommandHost implements IWearCommandHost {
    public static final String EXTRA_REPLY = "Command";
    public static final String ACTION_RESPONSE = "com.zenit.android.wearable.openhab.COMMAND";//TODO rename all zenit
    private final Context mContext;

    @Inject
    public WearCommandHost(@ApplicationContext Context context) {
        mContext = context;
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

    public NotificationCompat.Action[] getNotificationActions() {
        return new NotificationCompat.Action[] {getGroupMessageAction(), getVoiceCommandAction()};
    }

    private NotificationCompat.Action getVoiceCommandAction() {
        // Create intent for action
        Intent intent = new Intent(ACTION_RESPONSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REPLY).setLabel("Listening...").setAllowFreeFormInput(true).build();

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
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REPLY)
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

