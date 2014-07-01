package org.openhab.habclient.wear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;

import org.openhab.habclient.HABApplication;
import org.openhab.habclient.command.ICommandAnalyzer;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WearCommandHost implements org.openhab.domain.wear.IWearCommandHost {
    private static final String ACTION_RESPONSE = "com.zenit.android.wearable.openhab.COMMAND";
    private BroadcastReceiver mReceiver;
    @Inject HABApplication mApplication;
    @Inject ICommandAnalyzer mCommandAnalyzer;

    public WearCommandHost(HABApplication application) {
        mApplication = application;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processResponse(intent);
            }
        };
    }

    @Override
    public void registerReceiver() {
        mApplication.registerReceiver(mReceiver, new IntentFilter(ACTION_RESPONSE));
    }

    @Override
    public void unregisterReceiver() {
        NotificationManagerCompat.from(mApplication).cancel(0);
        mApplication.unregisterReceiver(mReceiver);
    }

    @Override
    public void startSession(String title, String message) {
        showNotification(title, message);
    }

//    public void endSession() {
//    }

    private void showNotification(String title, String message) {
        // Create intent for reply action
        Intent intent = new Intent(ACTION_RESPONSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mApplication, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mApplication.getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent);
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bg_eliza));

        //Create primary action
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REPLY).setLabel("Command").setAllowFreeFormInput(true).build();

        // Create wearable notification and add remote input
        Notification notification = new WearableNotifications.Builder(builder)
                .setMinPriority()
                .addRemoteInputForContentIntent(remoteInput)
                .build();

        NotificationManagerCompat.from(mApplication).notify(0, notification);
    }

    private void processResponse(Intent intent) {
        String text = intent.getStringExtra(EXTRA_REPLY);
        if (text != null && !text.equals("")) {
            ArrayList<String> replyToBeAnalyzed = new ArrayList<String>(1);
            replyToBeAnalyzed.add(text);
//            mApplication.getSpeechResultAnalyzer().analyze(replyToBeAnalyzed, HABApplication.getAppMode());
            mCommandAnalyzer.analyzeCommand(replyToBeAnalyzed, mApplication.getAppMode(), mApplication.getApplicationContext());

            showNotification("Reply", "Hard coded message");
        }
    }
}
