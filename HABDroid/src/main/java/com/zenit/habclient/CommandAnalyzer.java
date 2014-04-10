package com.zenit.habclient;

import android.util.Log;

import com.zenit.habclient.command.CommandAnalyzerResult;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;
import org.openhab.habdroid.model.OpenHABWidgetTypeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandAnalyzer implements ICommandAnalyzer {

    protected RoomProvider mRoomProvider;
    protected OpenHABWidgetProvider mOpenHABWidgetProvider;
    protected RoomFlipper mRoomFlipper;
    protected TextToSpeechProvider mTextToSpeechProvider;

    @Override
    public TextToSpeechProvider getTextToSpeechProvider() {
        return mTextToSpeechProvider;
    }

    @Override
    public void setTextToSpeechProvider(TextToSpeechProvider textToSpeechProvider) {
        mTextToSpeechProvider = textToSpeechProvider;
    }

    @Override
    public OpenHABWidgetProvider getOpenHABWidgetProvider() {
        return mOpenHABWidgetProvider;
    }

    @Override
    public RoomProvider getRoomProvider() {
        return mRoomProvider;
    }

    @Override
    public RoomFlipper getRoomFlipper() {
        return mRoomFlipper;
    }

    @Override
    public void setRoomFlipper(RoomFlipper roomFlipper) {
        mRoomFlipper = roomFlipper;
    }

    public CommandAnalyzer(RoomProvider roomProvider, OpenHABWidgetProvider openHABWidgetProvider) {
        mRoomProvider = roomProvider;
        mOpenHABWidgetProvider = openHABWidgetProvider;
    }

    @Override
    public SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        //
        return SpeechAnalyzerResult.ContinueListening;
    }

    @Override
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

                    if(mTextToSpeechProvider != null)
                        mTextToSpeechProvider.speakText(roomToShow.getName());
                    else
                        Log.e(HABApplication.getLogTag(), "TextToSpeechProvider is NULL!");
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

    @Override
    public CommandAnalyzerResult analyzeCommand(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        CommandAnalyzerResult result = null;

        List<Room> roomList = getRoomsFromPhrases(speechResult, applicationMode);
        List<OpenHABWidget> unitList = getUnitsFromPhrases(speechResult, roomList);

        return result;
    }

    protected Map<String, Room> getMapOfRoomNamesFromProvider() {
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

        return roomNameMap;
    }

    protected List<Room> getRoomsFromPhrases(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        List<Room> resultList = new ArrayList<Room>();

        Map<String, Room> roomNameMap = getMapOfRoomNamesFromProvider();

        if(applicationMode == ApplicationMode.RoomFlipper) {
            for(String roomName : roomNameMap.keySet().toArray(new String[0])) {
                for (String match : speechResult.toArray(new String[0])) {
                    if(match.toUpperCase().contains(roomName.toUpperCase()) && !resultList.contains(roomNameMap.get(roomName.toUpperCase())))
                    {
                        //Got a room match.
                        Room foundRoom = roomNameMap.get(roomName.toUpperCase());
                        resultList.add(foundRoom);
                        //Log.d(HABApplication.getLogTag(), "Found room in command phrase <" + foundRoom.getName() + ">");
                    }
                }
            }
        }

        return resultList;
    }

    protected List<OpenHABWidget> getListOfWidgetsFromListOfRooms(List<Room> listOfRooms) {
        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();

        //Get widgets from room list
        if(listOfRooms != null && !listOfRooms.isEmpty()){
            Iterator<Room> roomIterator = listOfRooms.iterator();
            Map<String, Room> roomNameMap = new HashMap<String, Room>();
            while (roomIterator.hasNext()) {
                Room nextRoom = roomIterator.next();
                Iterator<GraphicUnit> gunitIterator = nextRoom.getUnitIterator();
                while(gunitIterator.hasNext())
                    widgetList.add(gunitIterator.next().getOpenHABWidget());
            }
        }

        //Get all unit widgets
        else if(mOpenHABWidgetProvider != null)
            widgetList = mOpenHABWidgetProvider.getWidgetList(OpenHABWidgetTypeSet.UnitItem);

        return widgetList;
    }

    protected List<OpenHABWidget> getUnitsFromPhrases(List<String> commandPhrases, List<Room> listOfRooms) {
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<OpenHABWidget> widgetList = getListOfWidgetsFromListOfRooms(listOfRooms);
        if(widgetList.size() == 0) {
            widgetList = HABApplication.getOpenHABWidgetProvider().getWidgetList((Set<OpenHABWidgetType>) null);
        }
        //Create widget name Map
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            widgetNameMap.put(nextWidget.getLabel().toUpperCase(), nextWidget);
        }

        //Look for match
        for(String match : commandPhrases.toArray(new String[0])) {
            if(widgetNameMap.keySet().contains(match.toUpperCase())) {
                //Got a unit match.
                OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
                resultList.add(foundWidget);
                Log.d(HABApplication.getLogTag(), "Found unit in command phrase <" + foundWidget.getLabel() + ">");
            }
        }

        return resultList;
    }

    protected String getPopularNameFromWidgetLabel(String openHABWidgetLabel) {
        return getReplaceAllTags(openHABWidgetLabel);
    }

    protected String getReplaceAllTags(String source) {
        String result = source;
        int firstBeginIndex = result.indexOf("[");
        int firstEndIndex = result.indexOf("]");
        while(firstBeginIndex > -1 && firstEndIndex > -1  && firstBeginIndex < firstEndIndex) {
            result = (firstBeginIndex > 1? result.substring(0, firstBeginIndex - 1) : "") + (firstEndIndex < result.length()? result.substring(firstEndIndex + 1) : "");
            firstBeginIndex = result.indexOf("[");
            firstEndIndex = result.indexOf("]");
        }
        return result.trim();
    }
}
