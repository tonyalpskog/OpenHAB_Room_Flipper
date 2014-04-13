package com.zenit.habclient;

import android.content.Context;
import android.util.Log;

import com.zenit.habclient.command.CommandAnalyzerResult;
import com.zenit.habclient.command.CommandPhraseMatchResult;
import com.zenit.habclient.command.OpenHABWidgetCommandType;

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
        return replaceSubStrings(openHABWidgetLabel, "[", "]", "");
    }

    protected String replaceSubStrings(String source, String beginIncluded, String endIncluded, String replacement) {
        String result = source;
        int firstBeginIndex = result.indexOf(beginIncluded);
        int firstEndIndex = result.indexOf(endIncluded);
        while(firstBeginIndex > -1 && firstEndIndex > -1  && firstBeginIndex < firstEndIndex) {
            result = (firstBeginIndex > 1? result.substring(0, firstBeginIndex) : "") + replacement + (firstEndIndex < result.length()? result.substring(firstEndIndex + 1) : "");
            firstBeginIndex = result.indexOf(beginIncluded);
            firstEndIndex = result.indexOf(endIncluded);
        }
        Log.d(HABApplication.getLogTag(), "Got unit popular name from label: " + source+ " => " + result.trim());
        return result.trim();
    }

    /**
     * Example commandPhrases = "Turn on Kitchen ceiling lights" will result in a single mapping
     * of "KITCHEN CEILING LIGHTS" as key and OpenHABWidgetCommandType.SwitchOn as value.
     * @param commandPhrases
     * @return a Map of keys as upper case phrase strings with the command excluded and command types as values.
     */
    public List<CommandPhraseMatchResult> getCommandsFromPhrases(List<String> commandPhrases, Context context) {
        List<CommandPhraseMatchResult> commandPhraseMatchResultList = new ArrayList<CommandPhraseMatchResult>();
        for(String phrase : commandPhrases.toArray(new String[0])) {
            phrase = phrase.toUpperCase();
            for(OpenHABWidgetCommandType commandType : OpenHABWidgetCommandType.values()) {
                for(String commandAsText : commandType.getTextCommands(context)) {
                    commandAsText = commandAsText.toUpperCase();
                    int matchPoints = replaceSubStrings(commandAsText, "<", ">", "").split("\\s+").length;
                    String regexCommand = replaceSubStrings(commandAsText, "<", ">", "(.+)").toUpperCase() + "\\z";
                    Pattern pattern = Pattern.compile(regexCommand);
                    Matcher matcher = pattern.matcher(phrase);
                    if(matcher.find()) {
                        List<String> tagPhrases = new ArrayList<String>();
                        for(int i = 1; i <= matcher.groupCount(); i++)
                            tagPhrases.add(matcher.group(i));

                        Pattern pattern2 = Pattern.compile(regexCommand);
                        Matcher matcher2 = pattern2.matcher(commandAsText);
                        List<String> tags = new ArrayList<String>();
                        if(matcher2.find()) {
                            tags = new ArrayList<String>();
                            for (int i = 1; i <= matcher2.groupCount(); i++)
                                tags.add(matcher2.group(i));
                        }
                        int listIndex = 0;
                        if(commandPhraseMatchResultList.size() > 0) {
                            for(; listIndex < commandPhraseMatchResultList.size();) {
                                if(commandPhraseMatchResultList.get(listIndex).getPoint() < matchPoints)
                                    break;
                                listIndex++;
                            }
                        }
                        commandPhraseMatchResultList.add(listIndex, new CommandPhraseMatchResult(commandType, tags.toArray(new String[0]), tagPhrases.toArray(new String[0]), matchPoints));
                    }
                }
            }
        }
        return  commandPhraseMatchResultList;
    }

//    public String getValue

    protected String getRegExMatch(String source, Pattern pattern) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++)
                result += matcher.group(i) + " ";
        }
        return result.trim();
    }
}
