package com.zenit.habclient.command;

import android.content.Context;

import com.zenit.habclient.ApplicationMode;
import com.zenit.habclient.OpenHABWidgetProvider;
import com.zenit.habclient.RoomFlipper;
import com.zenit.habclient.RoomProvider;
import com.zenit.habclient.SpeechAnalyzerResult;
import com.zenit.habclient.TextToSpeechProvider;
import com.zenit.habclient.command.CommandAnalyzerResult;

import java.util.ArrayList;
import java.util.List;

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

    CommandAnalyzerResult analyzeCommand(List<String> speechResult, ApplicationMode applicationMode, Context context);
}
