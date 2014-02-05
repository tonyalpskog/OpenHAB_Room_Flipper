package com.zenit.habclient;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SpeechResultAnalyzer {

    private RoomProvider mRoomProvider;
    private OpenHABWidgetProvider mOpenHABWidgetProvider;
    private RoomFlipper mRoomFlipper;
    private TextToSpeechProvider mTextToSpeechProvider;

    public TextToSpeechProvider getTextToSpeechProvider() {
        return mTextToSpeechProvider;
    }

    public void setTextToSpeechProvider(TextToSpeechProvider textToSpeechProvider) {
        mTextToSpeechProvider = textToSpeechProvider;
    }

    public RoomFlipper getRoomFlipper() {
        return mRoomFlipper;
    }

    public void setRoomFlipper(RoomFlipper roomFlipper) {
        mRoomFlipper = roomFlipper;
    }

    public SpeechResultAnalyzer(RoomProvider roomProvider, OpenHABWidgetProvider openHABWidgetProvider) {
        mRoomProvider = roomProvider;
        mOpenHABWidgetProvider = openHABWidgetProvider;
    }

    public SpeechAnalyzerResult executeSpeech(ArrayList <String> speechResult, ApplicationMode applicationMode) {
        //
        return SpeechAnalyzerResult.ContinueListening;
    }

    public SpeechAnalyzerResult analyze(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<Room> roomList = new ArrayList<Room>();

        if(mRoomProvider != null)
            roomList.addAll(mRoomProvider.roomHash.values());

        Iterator<Room> iterator = roomList.iterator();
        Map<String, Room> roomNameMap = new HashMap<String, Room>();
        while (iterator.hasNext()) {
            Room nextRoom = iterator.next();
            roomNameMap.put(nextRoom.getName().toUpperCase(), nextRoom);
        }

        if(applicationMode == ApplicationMode.RoomFlipper) {
            for(String match : (String[]) speechResult.toArray(new String[0])) {
                if(roomNameMap.keySet().contains(match.toUpperCase())) {
                    //Got a speech match.
                    Room roomToShow = roomNameMap.get(match.toUpperCase());
                    Log.d(HABApplication.getLogTag(), "showRoom() - Show room<" + roomToShow.getId() + ">");
                    if(mRoomFlipper != null)
                        mRoomFlipper.showRoom(roomToShow);

                    mTextToSpeechProvider.speakText(roomToShow.getName());
                }
            }
        }

        for(String match : (String[]) speechResult.toArray(new String[0])) {
            if("hej då huset".equalsIgnoreCase(match)) {
                //End HAB application
                Log.d(HABApplication.getLogTag(), "Got a match: '" + match + "'");
                break;
            }
        }

        return SpeechAnalyzerResult.ContinueListening;
    }
}
