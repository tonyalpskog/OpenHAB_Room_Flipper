package org.openhab.habclient.command;

import android.content.Context;

import org.openhab.habclient.ApplicationMode;
import org.openhab.habclient.IRoomProvider;
import org.openhab.habclient.OpenHABWidgetProvider;
import org.openhab.habclient.RoomFlipper;
import org.openhab.habclient.SpeechAnalyzerResult;
import org.openhab.habclient.TextToSpeechProvider;

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

    public IRoomProvider getRoomProvider();

    SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode);

    SpeechAnalyzerResult analyze(ArrayList<String> speechResult, ApplicationMode applicationMode);

    CommandAnalyzerResult analyzeCommand(List<String> speechResult, ApplicationMode applicationMode, Context context);
}
