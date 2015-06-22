package org.openhab.habclient.wear;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.openhab.domain.IDeviceCommunicator;
import org.openhab.domain.INodeMessageHandler;
import org.openhab.habdroid.R;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2015.
 */
@Singleton
public class DeviceCommunicator implements IDeviceCommunicator {
    public static final String REMOTE_COMMAND = "Remote_Command";
    private final String TAG = "DeviceCommunicator";

    private GoogleApiClient apiClient;
    private NodeApi.NodeListener nodeListener;
    private MessageApi.MessageListener messageListener;
    private String remoteNodeId;
    private final Handler handler;
    private final Context mContext;
    private final INodeMessageHandler mNodeMessageHandler;

    @Inject
    public DeviceCommunicator(Context context, INodeMessageHandler nodeMessageHandler) {
        if(context == null) throw new IllegalArgumentException("context is null");
        if(nodeMessageHandler == null) throw new IllegalArgumentException("nodeMessageHandler is null");
        mContext = context;
        mNodeMessageHandler = nodeMessageHandler;
        handler = new Handler();
        initializeNodeListener();
        initializeMessageListener(this);
        initializeMessageClient();
    }

    @Override
    public void sendMessage(String path, String message, String nodeId) {

    }

    @Override
    public boolean isNodeOnline(String nodeId) {
        return false;
    }

    private void initializeNodeListener() {
        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
                Log.v(TAG, "Peer connected. ID = " + node);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
            }

            @Override
            public void onPeerDisconnected(Node node) {
                Log.v(TAG, "Peer disconnected. ID = " + node);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
            }
        };
    }

    private void initializeMessageListener(final IDeviceCommunicator deviceCommunicator) {
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                Log.v(TAG, REMOTE_COMMAND + " message received. ID = " + messageEvent.getSourceNodeId() + "  Path = '" + messageEvent.getPath() + "'  Message = '" + messageEvent.getData().toString() + "'");
                if (messageEvent.getPath().equals(WearListenerService.EXTERNAL_WEAR_COMMAND)) {
                    try {
                        mNodeMessageHandler.handleNodeMessage(deviceCommunicator, messageEvent.getPath(), new String(messageEvent.getData(), "UTF-8"), messageEvent.getSourceNodeId());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Do stuff...
//                        }
//                    });
                }
            }
        };
    }

    private void initializeMessageClient() {
        apiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.v(TAG, "GoogleApiClient.onConnected");

                Wearable.NodeApi.addListener(apiClient, nodeListener);
                Wearable.MessageApi.addListener(apiClient, messageListener);

                Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.v(TAG, "GoogleApiClient.onConnectionSuspended " + i);
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.w(TAG, "GoogleApiClient addOnConnection failed " + connectionResult.getErrorCode());
                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
                    Toast.makeText(mContext, mContext.getString(R.string.wearable_api_unavailable), Toast.LENGTH_LONG).show();
            }
        }).addApi(Wearable.API).build();
    }

}
