package org.openhab.habclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.habclient.dagger.DaggerSpeechComponent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SpeechService extends Service
{
    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    protected boolean mIsListening;
    protected volatile boolean mIsCountDownOn;

    @Inject ICommandAnalyzer mSpeechResultAnalyzer;
    @Inject IApplicationModeProvider mApplicationModeProvider;

    private static final int MSG_RECOGNIZER_START_LISTENING = 1;
    private static final int MSG_RECOGNIZER_CANCEL = 2;

    @Override
    public void onCreate()
    {
        super.onCreate();

        DaggerSpeechComponent.builder()
                .appComponent(((HABApplication) getApplication()).appComponent())
                .build()
                .inject(this);

        Log.d(HABApplication.getLogTag(), "SpeechService created");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());

        //TA: Three rows below added by me, tying to extend the speech input time before timeout occur.
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

//        //aqcuire the wakelock to keep the screen on until user exits/closes app
//        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        this.mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, HABApplication.getLogTag());
//        this.mWakeLock.acquire();
    }

    protected static class IncomingHandler extends Handler
    {
        private WeakReference<SpeechService> mtarget;

        IncomingHandler(SpeechService target)
        {
            mtarget = new WeakReference<SpeechService>(target);
        }


        @Override
        public void handleMessage(Message msg)
        {
            final SpeechService target = mtarget.get();

            switch (msg.what)
            {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        // turn off beep sound
                        target.mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                        Log.d(HABApplication.getLogTag(), "Audio STREAM_SYSTEM mute ON");
                    }
                    if (!target.mIsListening)
                    {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                        Log.d(HABApplication.getLogTag(), "message start listening");
                    } else {
                        Log.d(HABApplication.getLogTag(), "start listening was NOT called");
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    Log.d(HABApplication.getLogTag(), "message canceled recognizer");
                    break;

                default:
                    Log.d(HABApplication.getLogTag(), "Unknown message type = " + msg.what);
                    break;
            }
        }
    }

    // Count down timer for Jelly Bean work around
    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(11000, 11000)//TA: used to be 5000, 5000
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            Log.d(HABApplication.getLogTag(), "onFinish");

            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try
            {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {
                Log.e(HABApplication.getLogTag(), "RemoteException: " + e.toString());
            }
        }
    };

    @Override
    public void onDestroy()
    {
        Log.d(HABApplication.getLogTag(), "SpeechService is destroyed");
        super.onDestroy();

//        this.mWakeLock.release();

        if (mIsCountDownOn)
        {
            mNoSpeechCountDown.cancel();
        }
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            Log.d(HABApplication.getLogTag(), "onBeginingOfSpeech");
            // speech input will be processed, so there is no need for count down anymore
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
                Log.d(HABApplication.getLogTag(), "mNoSpeechCountDown.cancel()");
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(HABApplication.getLogTag(), "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(HABApplication.getLogTag(), "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            switch (error) {
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    Log.d(HABApplication.getLogTag(), "ERROR_NETWORK_TIMEOUT");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    Log.d(HABApplication.getLogTag(), "ERROR_NETWORK");
                    break;
                case SpeechRecognizer.ERROR_AUDIO:
                    Log.d(HABApplication.getLogTag(), "ERROR_AUDIO");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    Log.d(HABApplication.getLogTag(), "ERROR_SERVER");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    Log.d(HABApplication.getLogTag(), "ERROR_CLIENT");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    Log.d(HABApplication.getLogTag(), "ERROR_SPEECH_TIMEOUT");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //Log.d(HABApplication.getLogTag(), "ERROR_NO_MATCH");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    Log.d(HABApplication.getLogTag(), "ERROR_RECOGNIZER_BUSY");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    Log.d(HABApplication.getLogTag(), "ERROR_INSUFFICIENT_PERMISSIONS");
                    break;
                default:
                    Log.d(HABApplication.getLogTag(), "Unknown error = " + error);
            }

            if(error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
            {
                if (mIsCountDownOn)
                {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Log.d(HABApplication.getLogTag(), "mNoSpeechCountDown.cancel()");
                }
                mIsListening = false;
                Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                try
                {
                    Log.d(HABApplication.getLogTag(), "Sending message MSG_RECOGNIZER_START_LISTENING => " + message.toString());
                    mServerMessenger.send(message);
                }
                catch (RemoteException e)
                {
                    Log.e(HABApplication.getLogTag(), "RemoteException: " + e.toString());
                }
            } else
                Log.d(HABApplication.getLogTag(), "No timer interference");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//            {
//                // turn off beep sound
//                target.mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//            }
//            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(HABApplication.getLogTag(), "SpeechService event");

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(HABApplication.getLogTag(), "SpeechService result");
        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(HABApplication.getLogTag(), "onReadyForSpeech");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                Log.d(HABApplication.getLogTag(), "mNoSpeechCountDown.start()");
                mNoSpeechCountDown.start();
                Log.d(HABApplication.getLogTag(), "Audio STREAM_SYSTEM mute OFF");
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(HABApplication.getLogTag(), "-----------> onResults");

            if (mIsCountDownOn)
                mIsCountDownOn = false;

            mIsListening = false;

            ArrayList<String> matches = null;
            if(results != null){
                matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null){
                    Log.d(HABApplication.getLogTag(), "===> Results are " + matches.toString());


                    if (mIsCountDownOn)
                    {
                        mIsCountDownOn = false;
                        mNoSpeechCountDown.cancel();
                        Log.d(HABApplication.getLogTag(), "mNoSpeechCountDown.cancel()");
                    }
                    mIsListening = false;
                    Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    try
                    {
                        Log.d(HABApplication.getLogTag(), "Sending message MSG_RECOGNIZER_START_LISTENING => " + message.toString());
                        mServerMessenger.send(message);
                    }
                    catch (RemoteException e)
                    {
                        Log.e(HABApplication.getLogTag(), "RemoteException: " + e.toString());
                    }
                    final HABApplication application = (HABApplication) getApplication();
                    mSpeechResultAnalyzer.analyzeRoomNavigation(matches, mApplicationModeProvider.getAppMode());
                } else
                    Log.d(HABApplication.getLogTag(), "Matches = NULL");
            } else
                Log.d(HABApplication.getLogTag(), "Results = NULL");

        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
            //Log.d(HABApplication.getLogTag(), "onRmsChanged");
        }

    }
}