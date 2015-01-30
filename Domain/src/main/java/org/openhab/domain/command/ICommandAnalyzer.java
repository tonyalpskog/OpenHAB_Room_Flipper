package org.openhab.domain.command;

import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface ICommandAnalyzer {
    SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode);

    SpeechAnalyzerResult analyzeRoomNavigation(ArrayList<String> speechResult, ApplicationMode applicationMode);

    CommandAnalyzerResult analyzeCommand(List<String> speechResult, ApplicationMode applicationMode);

    String getCommandReply(CommandAnalyzerResult commandAnalyzerResult);
        
    void setOnShowRoomListener(OnShowRoomListener listener);

    interface OnShowRoomListener {
        void onShowRoom(Room room);
    }
}
