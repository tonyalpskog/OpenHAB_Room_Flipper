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
