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
    @Inject INotificationReplyHandler mNotificationReplyHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        processResponse(intent);
    }

    private void processResponse(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        CharSequence reply = remoteInput.getCharSequence(WearCommandHost.EXTRA_REPLY);
        String text = reply.toString();
        if (text != null && text.length() > 0) {
            mNotificationReplyHandler.handleReplyMessage(text, getLowPriorityVibratePattern());
        }
    }

    public long[] getMediumPriorityVibratePattern() {
        return new long[]{0, 350, 150, 350};
    }

    public long[] getLowPriorityVibratePattern() {
        return new long[]{0, 500};
    }

    public long[] getHighPriorityVibratePattern() {
        return new long[]{0, 350, 150, 350, 500, 350, 150, 350, 500};
    }
}
