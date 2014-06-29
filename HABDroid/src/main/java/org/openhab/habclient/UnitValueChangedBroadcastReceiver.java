package org.openhab.habclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitValueChangedBroadcastReceiver extends BroadcastReceiver {
//    private static final String INTENT_ACTION = "org.openhab.habclient.UNIT_STATUS_UPDATE";

    protected UnitValueChangedListener mUnitValueChangedListener;

    public UnitValueChangedBroadcastReceiver(UnitValueChangedListener unitValueChangedListener) {
        mUnitValueChangedListener = unitValueChangedListener;
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        Log.d(HABApplication.getLogTag(), "Intent received");
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                processIntent(intent);
                return null;
            }
        }.execute(null, null, null);
    }

    private void processIntent(Intent intent) {
        String sourceId = intent.getStringExtra("ID");
        String status = intent.getStringExtra("STATUS");
        mUnitValueChangedListener.fireValueChangedEvent(sourceId, status);
    }
}
