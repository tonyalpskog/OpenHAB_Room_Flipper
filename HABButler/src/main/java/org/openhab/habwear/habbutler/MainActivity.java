package org.openhab.habwear.habbutler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.openhab.habdroid.R;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String WEAR_COMMAND_BROADCAST = "org.openhab.habdroid.command.Wear_App_Command";
    private static final int SPEECH_REQUEST_CODE = 0;
    private final String TAG = "Wear";

    private ImageView mImageNoConnection;
    private IDeviceCommunicator mobileCommunicator;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mImageNoConnection = (ImageView) stub.findViewById(R.id.imageNoConnection);
                mImageNoConnection.setVisibility(View.GONE);

                ImageButton buttonSpeak = (ImageButton) stub.findViewById(R.id.buttonSpeak);
                buttonSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displaySpeechRecognizer();
                    }
                });

                ImageButton buttonFavorites = (ImageButton) stub.findViewById(R.id.buttonFavorites);
                buttonFavorites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessageToPhone("hi");
                    }
                });
            }
        });
//        retrieveDeviceNode();
        mobileCommunicator = new MobileCommunicator(this);

        final Intent intent = getIntent();
        if (getIntent() != null)
            handleActivityIntent(intent);
    }

    private void handleActivityIntent(final Intent intent) {
        final Context context = this;
        if (WearListenerService.WEAR_COMMAND_RESULT.equals(intent.getAction())) {
            final String message = intent.getStringExtra(WearListenerService.WEAR_COMMAND_RESULT_MESSAGE);
            Log.v(TAG, "Received: '" + message + "'");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.v(TAG, "Unknown => " + intent.getAction());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Unknown => " + intent.getAction(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mobileCommunicator.resume();
    }

    @Override
    protected void onPause() {
        mobileCommunicator.dispose();
        super.onPause();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, new String[]{"sv-SE", "en-US"});
//        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"sv-SE","en-US"});
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle languageDetails = data.getBundleExtra(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        if(languageDetails != null) {
            if (languageDetails.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)){
                String languagePreference = languageDetails.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
                Log.d("language preference",languagePreference);
            }
            if (languageDetails.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)){
                List<String> supportedLanguages = languageDetails.getStringArrayList( RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
                for(String s:supportedLanguages){
                    Log.d("supported language",s);
                }
            }
        }
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            List<String> results2 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE);
            Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
            LangBroadcastReceiver myBroadcastReceiver = new LangBroadcastReceiver(this, results);
            sendOrderedBroadcast(intent, null, myBroadcastReceiver, null, Activity.RESULT_OK, null, null);

            String spokenText = results.get(0);
            Log.d(TAG, String.format("Spoken phrase: '%s'", spokenText));
//            Log.d(TAG, "EXTRA_SUPPORTED_LANGUAGES: " + data.getStringExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES));
            Log.d(TAG, "EXTRA_LANGUAGE: " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE));
            Log.d(TAG, "EXTRA_LANGUAGE_MODEL: " + data.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL));
            Log.d(TAG, "EXTRA_LANGUAGE_PREFERENCE: " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE));
            if(spokenText.length() > 0) {
//                Intent broadcastIntent = new Intent("Wear_Command");
//                broadcastIntent.putExtra("Wear_Command", "get kitchen temperature");
//                broadcastIntent.putExtra("Android_Wear_Conversation_Id", 0);
//                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                this.getApplicationContext().sendBroadcast(broadcastIntent);

                sendMessageToPhone(spokenText);

//                Intent broadcastIntent = new Intent(WEAR_COMMAND_BROADCAST);
//                broadcastIntent.putExtra(WEAR_COMMAND_BROADCAST, spokenText);
//                broadcastIntent.putExtra(COMMAND_CONVERSATION_ID, OPENHAB_SYSTEM_CONVERSATION_ID);
//                sendBroadcast(broadcastIntent);
//                Log.d(TAG, "'Wear_Command' broadcast sent with 'Android_Wear_Conversation_Id' = 0");
            }
        }
    }

//    private GoogleApiClient getGoogleApiClient(Context context) {
//        return new GoogleApiClient.Builder(context)
//                .addApi(Wearable.API)
//                .build();
//    }
//
//    public String nodeId;
//
//    private void retrieveDeviceNode() {
//        final GoogleApiClient client = getGoogleApiClient(this);
//        final long CONNECTION_TIME_OUT_MS = 5000;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
//                NodeApi.GetConnectedNodesResult result =
//                        Wearable.NodeApi.getConnectedNodes(client).await();
//                List<Node> nodes = result.getNodes();
//                if (nodes.size() > 0) {
//                    nodeId = nodes.get(0).getId();
//                }
//                client.disconnect();
//            }
//        }).start();
//    }

    private void sendMessageToPhone(final String message) {
        Log.v(TAG, String.format("About to send '%s'...", message));
        mobileCommunicator.sendMessage(MobileCommunicator.WEAR_COMMAND, message.getBytes(StandardCharsets.UTF_8));
//        final GoogleApiClient client = getGoogleApiClient(this);
//        final long CONNECTION_TIME_OUT_MS = 5000;
//        if (nodeId != null) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
//                    Wearable.MessageApi.sendMessage(client, nodeId, WEAR_COMMAND_BROADCAST, message.getBytes());
//                    client.disconnect();
//                }
//            }).start();
//        }
    }
}

class LangBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "Wear";

    ArrayList<String> recognisedText;
    Activity parentActivity;

    LangBroadcastReceiver(Activity activity, ArrayList<String> arrayList) {
        recognisedText = arrayList;
        parentActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle results = getResultExtras(true);
        String lang = results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        Log.d(TAG, "LangBroadcastReceiver: Got 'EXTRA_LANGUAGE_PREFERENCE' = " + lang);
        // now handle the recognisedText with the known language.
    }
}