package org.openhab.habwear.habbutler;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

/**
 * Created by Tony Alpskog in 2015.
 */
public class WearListenerService extends WearableListenerService {
    public static final String WEAR_COMMAND_CONFIRM = "org.openhab.habdroid.wear.Command_Confirm";
    public static final String WEAR_COMMAND_RESULT = "org.openhab.habdroid.wear.Command_Result";

    public static final String WEAR_COMMAND_RESULT_MESSAGE = "org.openhab.habdroid.wear.Command_Result_Message";

    private final String TAG = "WearListenerService";

    private boolean isConnected = false;
    private String phoneNodeId;

    @Override
    public void onCreate() {
        super.onCreate();
//        DaggerWearListenerServiceComponent.builder()
//                .appComponent(((HABApplication) getApplication()).appComponent())
//                .build()
//                .inject(this);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        phoneNodeId = peer.getId();
        isConnected = true;
        Log.v(TAG, "Peer connected. ID = " + peer.getId());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        phoneNodeId = peer.getId();
        isConnected = false;
        Log.v(TAG, "Peer disconnected. ID = " + peer.getId());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "Receiving...");
        String nodeId = messageEvent.getSourceNodeId();
        String messageData = null;
        try {
            messageData = new String(messageEvent.getData(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        String path = messageEvent.getPath();
        Log.v(TAG, String.format("Receive '%s' from %s", messageData, nodeId));

        handleMessage(path, messageData, nodeId);
    }

    private void handleMessage(final String path, final String message, final String nodeId) {
//        if(path.equals(WEAR_COMMAND_CONFIRM)) {
//            //Fire bus message
//        }

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.setAction(WEAR_COMMAND_RESULT);//TODO - Centralize this parameter
        activityIntent.putExtra(WEAR_COMMAND_RESULT_MESSAGE, message);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
    }
}