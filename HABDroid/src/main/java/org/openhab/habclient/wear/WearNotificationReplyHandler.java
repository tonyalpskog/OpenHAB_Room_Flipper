package org.openhab.habclient.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import org.openhab.domain.INotificationReplyHandler;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class WearNotificationReplyHandler extends BroadcastReceiver {
    public static final long[] LOW_PRIORITY_VIBRATE_PATTERN = new long[]{0, 500};
    public static final long[] MEDIUM_PRIORITY_VIBRATE_PATTERN = new long[]{0, 350, 150, 350};
    public static final long[] HIGH_PRIORITY_VIBRATE_PATTERN = new long[]{0, 350, 150, 350, 500, 350, 150, 350, 500};

    private final INotificationReplyHandler mNotificationReplyHandler;

    @Inject
    public WearNotificationReplyHandler(INotificationReplyHandler notificationReplyHandler) {
        mNotificationReplyHandler = notificationReplyHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        processResponse(intent);
    }

    private void processResponse(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        CharSequence reply = remoteInput.getCharSequence(WearCommandHost.EXTRA_REPLY);
        if (reply != null && reply.toString().length() > 0) {
            mNotificationReplyHandler.handleReplyMessage(reply.toString(), LOW_PRIORITY_VIBRATE_PATTERN);
        }
    }
}
