package org.openhab.habclient.wear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.RemoteInput;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.command.CommandAnalyzerResult;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.habclient.RoomFlipperFragment;
import org.openhab.habdroid.R;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WearCommandHost implements org.openhab.domain.wear.IWearCommandHost {
    private static final String ACTION_RESPONSE = "com.zenit.android.wearable.openhab.COMMAND";
    private final Context mContext;
    private final IApplicationModeProvider mApplicationModeProvider;
    private BroadcastReceiver mReceiver;
    private ICommandAnalyzer mCommandAnalyzer;

    @Inject
    public WearCommandHost(Context context,
                           IApplicationModeProvider applicationModeProvider,
                           ICommandAnalyzer commandAnalyzer) {
        mContext = context;
        mApplicationModeProvider = applicationModeProvider;
        mCommandAnalyzer = commandAnalyzer;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processResponse(intent);
            }
        };
    }

    @Override
    public void registerReceiver() {
        mContext.registerReceiver(mReceiver, new IntentFilter(ACTION_RESPONSE));
    }

    @Override
    public void unregisterReceiver() {
        NotificationManagerCompat.from(mContext).cancel(0);
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void startSession(String title, String message) {
        showNotification(title, message);
    }

    private void showNotification(String title, String message/*, Action[] actions, NotificationCompat.Builder preBuiltPrio, ...*/) {//TODO - More injection
        // Create intent for reply action
        Intent intent = new Intent(ACTION_RESPONSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        //A simple, non-limited, voice input without pre-defined input patterns.
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REPLY).setLabel("Listening...").setAllowFreeFormInput(true).build();

        //Create primary action
        NotificationCompat.Action commandoAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_menu_add_action,
                        "Send command", pendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification
        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_action_copy)
                .setVibrate(new long[]{0, 350, 150, 350, 500, 350, 150, 350, 500})
                .extend(new WearableExtender().addAction(commandoAction))
                .build();

        NotificationManagerCompat.from(mContext).notify(0, notification);
    }

    private void processResponse(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        CharSequence reply = remoteInput.getCharSequence(EXTRA_REPLY);
        String text = reply.toString();
        if (text != null && text.length() > 0) {
            ArrayList<String> replyToBeAnalyzed = new ArrayList<String>(1);
            replyToBeAnalyzed.add(text);
//            mApplication.getSpeechResultAnalyzer().analyzeRoomNavigation(replyToBeAnalyzed, HABApplication.getAppMode());
            CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(replyToBeAnalyzed, mApplicationModeProvider.getAppMode());

            showNotification("Command reply",  mCommandAnalyzer.getCommandReply(commandAnalyzerResult));
        }
    }
}
