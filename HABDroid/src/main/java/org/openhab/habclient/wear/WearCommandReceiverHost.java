package org.openhab.habclient.wear;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.NotificationManagerCompat;

import org.openhab.domain.IWearCommandHost;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WearCommandReceiverHost implements IWearCommandHost {
    private final Context mContext;
    private final WearCommandBroadcastReceiver mWearCommandBroadcastReceiver;
    private boolean mIsRegistered;

    @Inject
    public WearCommandReceiverHost(Context context,
                                   WearCommandBroadcastReceiver wearCommandBroadcastReceiver) {
        mContext = context;
        mWearCommandBroadcastReceiver = wearCommandBroadcastReceiver;
    }

    @Override
    public void registerReceivers() {
        if(mIsRegistered)
            return;

        IntentFilter wearReadFilter = new IntentFilter();
        wearReadFilter.addAction(WearListenerService.INTERNAL_WEAR_COMMAND);
        mContext.registerReceiver(mWearCommandBroadcastReceiver, wearReadFilter);

        mIsRegistered = true;
    }

    @Override
    public void unregisterReceivers() {
        NotificationManagerCompat.from(mContext).cancel(0);
        mContext.unregisterReceiver(mWearCommandBroadcastReceiver);
        mIsRegistered = false;
    }

    @Override
    public boolean isRegistered() {
        return mIsRegistered;
    }
}
