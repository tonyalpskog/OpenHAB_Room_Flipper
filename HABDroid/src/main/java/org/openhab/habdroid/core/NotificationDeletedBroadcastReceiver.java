package org.openhab.habdroid.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.openhab.habclient.HABApplication;

import org.openhab.habdroid.ui.OpenHABMainActivity;

import java.io.IOException;

/**
 * Created by belovictor on 25/11/13.
 */
public class NotificationDeletedBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = "NotificationDeletedBroadcastReceiver";
    private GoogleCloudMessaging gcm;
    private Bundle sendBundle;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(HABApplication.getLogTag(), "Intent received");
        if (intent.hasExtra("notificationId")) {
            gcm = GoogleCloudMessaging.getInstance(context);
            sendBundle = new Bundle();
            sendBundle.putString("type", "hideNotification");
            sendBundle.putString("notificationId", String.valueOf(intent.getExtras().getInt("notificationId")));
            Log.d(HABApplication.getLogTag(), "notificationId = " + intent.getExtras().getInt("notificationId"));
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    try {
                        gcm.send(OpenHABMainActivity.GCM_SENDER_ID + "@gcm.googleapis.com",
                                "1", sendBundle);
                    } catch (IOException e) {
                        Log.e(HABApplication.getLogTag(), e.getMessage());
                    }
                    return null;
                }
            }.execute(null, null, null);
//            context.unregisterReceiver(this);
        }
    }
}
