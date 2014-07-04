package org.openhab.domain;

import java.util.Locale;

public interface ITextToSpeechProvider {
    boolean setExternalTTSEngine(String ttsPackageName);

    boolean setLanguage(Locale locale);

    void speakText(String textToSpeak);
}
