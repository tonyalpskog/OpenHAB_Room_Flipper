package com.zenit.habclient.wear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.zenit.habclient.HABApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WearCommandHost {
    public static final String EXTRA_REPLY = "Command";
    private static final String ACTION_RESPONSE = "com.zenit.android.wearable.openhab.COMMAND";
    private String mTextToWearable = null;
    private String mTitleToWearable = null;
    private BroadcastReceiver mReceiver;
    private HABApplication mApplication;

    public WearCommandHost(HABApplication application) {
        mApplication = application;
        mTitleToWearable = "Room navigation";
        mTextToWearable = "Please, response with the name of a room";

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processResponse(intent);
            }
        };
    }

    public void registerReceiver() {
        mApplication.registerReceiver(mReceiver, new IntentFilter(ACTION_RESPONSE));
    }

    public void unregisterReceiver() {
        NotificationManagerCompat.from(mApplication).cancel(0);
        mApplication.unregisterReceiver(mReceiver);
    }

    public void startSession() {
        showNotification();
    }

//    public void endSession() {
//    }

    private void showNotification() {
        // Create intent for reply action
        Intent intent = new Intent(ACTION_RESPONSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mApplication, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mApplication.getApplicationContext())
                .setContentTitle(mTitleToWearable)
                .setContentText(mTextToWearable)
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
            mApplication.getSpeechResultAnalyzer().analyze(replyToBeAnalyzed, HABApplication.getAppMode());
            showNotification();
        }
    }
}