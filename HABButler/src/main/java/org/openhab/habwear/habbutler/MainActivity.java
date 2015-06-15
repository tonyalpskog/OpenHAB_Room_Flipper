package org.openhab.habwear.habbutler;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String WEAR_COMMAND_BROADCAST = "Wear_Command";
    private final int OPENHAB_SYSTEM_CONVERSATION_ID = 0;

    private static final int SPEECH_REQUEST_CODE = 0;
    private ImageView mImageNoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mImageNoConnection = (ImageView) stub.findViewById(R.id.imageNoConnection);
                mImageNoConnection.setVisibility(View.GONE);
                ImageButton button = (ImageButton) stub.findViewById(R.id.buttonSpeak);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displaySpeechRecognizer();
                    }
                });
            }
        });
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, new String[]{"sv-SE","en-US"});
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
            Log.d("Wear", String.format("Spoken phrase: '%s'", spokenText));
//            Log.d("Wear", "EXTRA_SUPPORTED_LANGUAGES: " + data.getStringExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES));
            Log.d("Wear", "EXTRA_LANGUAGE: " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE));
            Log.d("Wear", "EXTRA_LANGUAGE_MODEL: " + data.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL));
            Log.d("Wear", "EXTRA_LANGUAGE_PREFERENCE: " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE));
            if(spokenText.length() > 0) {
                Intent broadcastIntent = new Intent("Wear_Command");
                broadcastIntent.putExtra("Wear_Command", "get kitchen temperature");
                broadcastIntent.putExtra("Android_Wear_Conversation_Id", 0);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                this.getApplicationContext().sendBroadcast(broadcastIntent);

                Log.d("Wear", "'Wear_Command' broadcast sent with 'Android_Wear_Conversation_Id' = 0");
//                Intent broadcastIntent = new Intent(WEAR_COMMAND_BROADCAST);
//                broadcastIntent.putExtra(WEAR_COMMAND_BROADCAST, spokenText);
//                sendBroadcast(broadcastIntent);
            }
        }
    }

}

class LangBroadcastReceiver extends BroadcastReceiver {
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
        Log.d("Wear", "LangBroadcastReceiver: Got 'EXTRA_LANGUAGE_PREFERENCE' = " + lang);
        // now handle the recognisedText with the known language.
    }
}