package org.openhab.habclient.wear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.openhab.domain.IDeviceCommunicator;
import org.openhab.domain.INodeMessageHandler;
import org.openhab.domain.util.StringHandler;
import org.openhab.habclient.HABApplication;
import org.openhab.habdroid.R;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class WearListenerService extends WearableListenerService implements IDeviceCommunicator {
    public static final String WEAR_COMMAND = "org.openhab.habdroid.wear.Command";
    public static final String WEAR_COMMAND_CONFIRM = "org.openhab.habdroid.wear.Command_Confirm";
    public static final String WEAR_COMMAND_RESULT = "org.openhab.habdroid.wear.Command_Result";

    private final String TAG = "WearListenerService";

    private GoogleApiClient googleApiClient;
    private String wearNodeId;
    private boolean isConnected = false;

    @Inject INodeMessageHandler wearCommandHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Initializing");
        ((HABApplication) getApplication()).appComponent()
                .wearListenerService()
                .inject(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        addConnectionCallbacks();
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Disconnecting by onDestroy");
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        wearNodeId = peer.getId();
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

        wearNodeId = peer.getId();
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

        if(path.equals(WEAR_COMMAND) && !StringHandler.isNullOrEmpty(messageData)) {
            if(messageData.equals("hi"))
                sendMessage(path, "hello there", nodeId);
            else
                wearCommandHandler.handleNodeMessage(this, path, messageData, nodeId);
        }
    }

    private void addConnectionCallbacks() {
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.v(TAG, "GoogleApiClient.onConnected");

                Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            wearNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                            Log.v(TAG, "NodeApi.getConnectedNodes result[0] = " + wearNodeId);
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.v(TAG, "GoogleApiClient.onConnectionSuspended " + i);
            }
        });

        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.w(TAG, "GoogleApiClient addOnConnection failed " + connectionResult.getErrorCode());
                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
                    Log.w(TAG, getString(R.string.wearable_api_unavailable));
            }
        });
    }

    @Override
    public void sendMessage(final String path, final String message, final String nodeId) {
        try {
            Wearable.MessageApi.sendMessage(googleApiClient, nodeId == null? wearNodeId : nodeId, path, message.getBytes("UTF-8")).
            setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if (sendMessageResult.getStatus().isSuccess())
                        Log.w(TAG, String.format("Message '%s' successfully sent to %s", message, nodeId));
                    else
                        Log.w(TAG, "Message send failed! Node = " + nodeId);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean isNodeOnline(String nodeId) {
        return isConnected;
    }
}