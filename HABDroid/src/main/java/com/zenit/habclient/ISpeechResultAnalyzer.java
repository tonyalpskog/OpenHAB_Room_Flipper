package com.zenit.habclient;

import java.util.ArrayList;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface ISpeechResultAnalyzer {
    TextToSpeechProvider getTextToSpeechProvider();

    void setTextToSpeechProvider(TextToSpeechProvider textToSpeechProvider);

    RoomFlipper getRoomFlipper();

    void setRoomFlipper(RoomFlipper roomFlipper);

    SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode);

    SpeechAnalyzerResult analyze(ArrayList<String> speechResult, ApplicationMode applicationMode);
}
