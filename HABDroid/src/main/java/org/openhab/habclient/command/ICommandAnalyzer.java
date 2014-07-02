package org.openhab.habclient.command;

import org.openhab.habclient.ApplicationMode;
import org.openhab.habclient.RoomFlipper;
import org.openhab.habclient.SpeechAnalyzerResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface ICommandAnalyzer {
    void setRoomFlipper(RoomFlipper roomFlipper);

    SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode);

    SpeechAnalyzerResult analyze(ArrayList<String> speechResult, ApplicationMode applicationMode);

    CommandAnalyzerResult analyzeCommand(List<String> speechResult, ApplicationMode applicationMode);
}
