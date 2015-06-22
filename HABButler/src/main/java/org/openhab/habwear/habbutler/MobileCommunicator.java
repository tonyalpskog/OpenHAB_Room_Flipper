/*
 * Copyright 2015 Dejan Djurovski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhab.habwear.habbutler;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.openhab.habdroid.R;

import java.nio.charset.StandardCharsets;

public class MobileCommunicator implements IDeviceCommunicator {
    public static final String WEAR_COMMAND = "org.openhab.habdroid.command.Wear_App_Command";
    private final String TAG = "Wear";

    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private MessageApi.MessageListener messageListener;
    private String mobileNodeId;
    private Handler handler;
    private Activity mActivity;

    public MobileCommunicator(Activity activity) {
        mActivity = activity;
        handler = new Handler();

        Log.v(TAG, "Initializing");

        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                Log.i(TAG, "Peer connected");
                mobileNodeId = node.getId();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
                Intent intent = new Intent(mActivity.getApplicationContext(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mActivity.getString(R.string.peer_connected));
                mActivity.startActivity(intent);
            }

            @Override
            public void onPeerDisconnected(Node node) {
                Log.i(TAG, "Peer disconnected");
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Do stuff...
//                    }
//                });
                Intent intent = new Intent(mActivity.getApplicationContext(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mActivity.getString(R.string.peer_disconnected));
                mActivity.startActivity(intent);
            }
        };

        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(final MessageEvent messageEvent) {
                Log.v(TAG, "Receiving...");
                if (messageEvent.getPath().equals(WEAR_COMMAND)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "Received: '" + new String(messageEvent.getData(), StandardCharsets.UTF_8) + "'");
                            Toast.makeText(mActivity, new String(messageEvent.getData(), StandardCharsets.UTF_8), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "Unknown => " + messageEvent.getPath());
                            Toast.makeText(mActivity, "Unknown => " + messageEvent.getPath(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        googleApiClient = new GoogleApiClient.Builder(mActivity.getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(TAG, "Connected");

                Wearable.NodeApi.addListener(googleApiClient, nodeListener);
                Wearable.MessageApi.addListener(googleApiClient, messageListener);

                Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            mobileNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                            //Do "connected" stuff...
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.w(TAG, "Connection suspended");
                //Do "not connected" stuff...
            }
        }).addApi(Wearable.API).build();
    }

    @Override
    public void resume() {
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getApplicationContext());
        if (connectionResult != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult, mActivity, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    public void sendMessage(String path, @Nullable byte[] data) {
        Log.v(TAG, String.format("...to node %s", mobileNodeId));
        Wearable.MessageApi.sendMessage(googleApiClient, mobileNodeId, path, data).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                Intent intent = new Intent(mActivity.getApplicationContext(), ConfirmationActivity.class);
                if (sendMessageResult.getStatus().isSuccess()) {
                    Log.v(TAG, "Successful send");
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mActivity.getString(R.string.message_sent));
                } else {
                    Log.v(TAG, "Send error");
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mActivity.getString(R.string.send_error));
                }
//                mActivity.startActivity(intent);//TODO - TA: This line caused the connection to be disconnected.
            }
        });
    }

    @Override
    public boolean isNodeOnline(String nodeId) {
        return googleApiClient.isConnected();
    }

    @Override
    public void dispose() {
        Log.v(TAG, "Disconnecting by dispose");
        Wearable.NodeApi.removeListener(googleApiClient, nodeListener);
        Wearable.MessageApi.removeListener(googleApiClient, messageListener);
        googleApiClient.disconnect();
    }
}
