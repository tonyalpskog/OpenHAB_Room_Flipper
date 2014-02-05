package com.zenit.habclient;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Tony Alpskog in 2014.
 */
public class TextToSpeechProvider {

    private TextToSpeech mTextToSpeech;

    public TextToSpeechProvider(Context context, final Locale locale) {
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    setLanguage(locale);
                } else {
                    Log.e(HABApplication.getLogTag(), "Text-To-Speech initialization failed");
                }
            }
        });

//        speakText("Available TTS engines, before");
        speakText("Tillgängliga TTS maskiner");

        String availableEnginesLogText = "Available TTS engines: ";
        for(TextToSpeech.EngineInfo ei : mTextToSpeech.getEngines()) {
            availableEnginesLogText += "/n-> " + ei.toString();
        }
        Log.i(HABApplication.getLogTag(), availableEnginesLogText);

//        setExternalTTSEngine("com.svox.classic");//com.svox.pico
//
//        speakText("Tillgängliga TTS maskiner");
//
//        availableEnginesLogText = "Available TTS engines AFTER: ";
//        for(TextToSpeech.EngineInfo ei : mTextToSpeech.getEngines()) {
//            availableEnginesLogText += "/n-> " + ei.toString();
//        }
//        Log.i(HABApplication.getLogTag(), availableEnginesLogText);
    }


    public boolean setExternalTTSEngine(String ttsPackageName) {
        int result = mTextToSpeech.setEngineByPackageName(ttsPackageName);
        if( result == TextToSpeech.ERROR ) {
            Log.e(HABApplication.getLogTag(), String.format("Could not find Text-To-Speech package '%s'", ttsPackageName));
            return false;
        } else
        return true;
    }

    public boolean setLanguage(Locale locale) {
        int result = mTextToSpeech.setLanguage(locale);

        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(HABApplication.getLogTag(), "This Language is not supported");
            return false;
        }

        return true;
    }

    public void speakText(String textToSpeak) {
        mTextToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
}
