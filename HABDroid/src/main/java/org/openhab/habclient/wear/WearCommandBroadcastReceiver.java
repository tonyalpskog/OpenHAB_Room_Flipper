package org.openhab.habclient.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import org.openhab.domain.IDeviceCommunicator;
import org.openhab.domain.INodeMessageHandler;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class WearCommandBroadcastReceiver extends BroadcastReceiver {
    private final INodeMessageHandler mCommandHandler;
    private final IDeviceCommunicator mDeviceCommunicator;

    @Inject
    public WearCommandBroadcastReceiver(INodeMessageHandler commandHandler, IDeviceCommunicator deviceCommunicator) {
        if(commandHandler == null) throw new IllegalArgumentException("commandHandler is null");
        if(deviceCommunicator == null) throw new IllegalArgumentException("deviceCommunicator is null");
        mCommandHandler = commandHandler;
        mDeviceCommunicator = deviceCommunicator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (intent.getAction().equals(WearListenerService.INTERNAL_WEAR_COMMAND) && intent.hasExtra(WearListenerService.INTERNAL_WEAR_COMMAND)) {
            String commandMessage = intent.getStringExtra(WearListenerService.INTERNAL_WEAR_COMMAND);
            String nodeId = intent.getStringExtra(WearListenerService.WEAR_NODE_ID);

            mCommandHandler.handleNodeMessage(mDeviceCommunicator, null, commandMessage, nodeId);
        }
    }
}
