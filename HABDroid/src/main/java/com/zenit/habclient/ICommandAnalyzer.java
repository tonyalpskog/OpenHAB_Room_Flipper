package com.zenit.habclient;

import com.zenit.habclient.command.CommandAnalyzerResult;

import org.openhab.habdroid.model.OpenHABWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface ICommandAnalyzer {
    TextToSpeechProvider getTextToSpeechProvider();

    void setTextToSpeechProvider(TextToSpeechProvider textToSpeechProvider);

    RoomFlipper getRoomFlipper();

    void setRoomFlipper(RoomFlipper roomFlipper);

    public OpenHABWidgetProvider getOpenHABWidgetProvider();

    public RoomProvider getRoomProvider();

    SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode);

    SpeechAnalyzerResult analyze(ArrayList<String> speechResult, ApplicationMode applicationMode);

    CommandAnalyzerResult analyzeCommand(ArrayList<String> speechResult, ApplicationMode applicationMode);
}
