package com.zenit.habclient;

import android.content.Context;
import android.util.Log;

import com.zenit.habclient.command.CommandAnalyzerResult;
import com.zenit.habclient.command.CommandPhraseMatchResult;
import com.zenit.habclient.command.OpenHABWidgetCommandType;
import com.zenit.habclient.command.WidgetPhraseMatchResult;
import com.zenit.habclient.util.RegExResult;

import org.openhab.habdroid.model.OpenHABItemType;
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
    protected Map<String, List<OpenHABWidgetType>> mWidgetTypeTagMapping = new HashMap<String, List<OpenHABWidgetType>>();
    protected Map<String, List<OpenHABItemType>> mItemTypeTagMapping = new HashMap<String, List<OpenHABItemType>>();

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

        initializeWidgetTypeTagMapping();
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
    public CommandAnalyzerResult analyzeCommand(ArrayList<String> commandPhrases, ApplicationMode applicationMode, Context context) {
        CommandAnalyzerResult result = null;

        List<CommandPhraseMatchResult> commandMatchResultList = getCommandsFromPhrases(commandPhrases, context);
        Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> unitMatchResult = getHighestWidgetsFromCommandMatchResult(commandMatchResultList);

        //TODO - Implement call to method that parses value tag data
        //result.setMatchPoint();
        List<Room> roomList = getRoomsFromPhrases(commandPhrases, applicationMode);
        List<OpenHABWidget> unitList = getUnitsFromPhrases(commandPhrases, roomList);

        return result;

        //For execution example see: OpenHABWidgetControl.sendItemCommand(OpenHABItem item, String command)
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

    protected List<OpenHABWidget> getUnitsFromPhrases2(List<String> commandPhrases, List<Room> listOfRooms, HABApplication habApplication) {
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        List<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();// getListOfWidgetsFromListOfRooms(listOfRooms);
        widgetList = habApplication.getOpenHABWidgetProvider().getWidgetList((Set<OpenHABWidgetType>) null);
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            widgetNameMap.put(getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase(), nextWidget);
        }

        //Look for match
        for(String match : commandPhrases.toArray(new String[0])) {
            for(String unitName : widgetNameMap.keySet().toArray(new String[0])) {
                if (match.toUpperCase().contains(unitName)) {
                    //Got a unit match.
                    OpenHABWidget foundWidget = widgetNameMap.get(unitName);
                    resultList.add(foundWidget);
                    Log.d(habApplication.getLogTag(), "Found unit in command phrase <" + foundWidget.getLabel() + ">");
                }
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

    public Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> getHighestWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        //TODO - Compare the tag type with item and widget type. No tag<->type match = No unit result.

        Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> resultMap = new HashMap<CommandPhraseMatchResult, WidgetPhraseMatchResult>();

        Iterator<CommandPhraseMatchResult> iterator = listOfCommandResult.iterator();
        while(iterator.hasNext()) {
            CommandPhraseMatchResult commandPhraseMatchResult = iterator.next();
            WidgetPhraseMatchResult widgetMatch = getMostProbableWidgetFromCommandMatchResult(commandPhraseMatchResult);
            //TODO - Implement call to method that compares the tag type with item and widget type. No tag<->type match = No unit result
            String[] valueTags = getValueTagType(commandPhraseMatchResult);
            if(valueTags.length > 1)
                Log.w(HABApplication.getLogTag(), "Command phrase contains more than one value tag: " + valueTags.length);

            if((valueTags.length > 0 && doesTagTypeMatchWidgetType(valueTags[0], widgetMatch.getWidget())) || valueTags.length == 0)
                resultMap.put(commandPhraseMatchResult, widgetMatch);
        }

        return resultMap;
    }

    protected String[] getValueTagType(CommandPhraseMatchResult commandPhraseMatchResult) {
        List<String> tagList = new ArrayList<String>();
        for(String tag : commandPhraseMatchResult.getTags()) {
            if(!tag.equalsIgnoreCase("<unit>"))
                tagList.add(tag);
        }

        return tagList.toArray(new String[0]);
    }

    public boolean doesTagTypeMatchWidgetType(String tag, OpenHABWidget widget) {
        //TODO - What about if the tag is missing in the Map members? -> Third return value type.
        if(widget.hasItem() && mItemTypeTagMapping.containsKey(tag))
            return mItemTypeTagMapping.get(tag).contains(widget.getItem().getType());
        else if(mWidgetTypeTagMapping.containsKey(tag))
            return mWidgetTypeTagMapping.get(tag).contains(widget.getType());
        return true;
    }

    public Map<CommandPhraseMatchResult, List<WidgetPhraseMatchResult>> getAllWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        Map<CommandPhraseMatchResult, List<WidgetPhraseMatchResult>> resultMap = new HashMap<CommandPhraseMatchResult, List<WidgetPhraseMatchResult>>();

        Iterator<CommandPhraseMatchResult> iterator = listOfCommandResult.iterator();
        while(iterator.hasNext()) {
            CommandPhraseMatchResult commandPhraseMatchResult = iterator.next();
            resultMap.put(commandPhraseMatchResult, getWidgetsFromCommandMatchResult(commandPhraseMatchResult));
        }

        return resultMap;
    }

    public List<WidgetPhraseMatchResult> getWidgetsFromCommandMatchResult(CommandPhraseMatchResult commandPhraseMatchResult) {
        List<WidgetPhraseMatchResult> resultList = new ArrayList<WidgetPhraseMatchResult>();
        String unitTagPhrase = getUnitPhrase(commandPhraseMatchResult);

        //Add all found widgets no matter their match points.
        return HABApplication.getOpenHABWidgetProvider().getWidgetByLabel(unitTagPhrase, this);
    }

    public WidgetPhraseMatchResult getMostProbableWidgetFromCommandMatchResult(CommandPhraseMatchResult commandPhraseMatchResult) {
        String unitTagPhrase = getUnitPhrase(commandPhraseMatchResult);

        //Only add the widget with the highest match point to the resulting list.
        int matchPoint = 0;
        WidgetPhraseMatchResult widgetMatch = null;
        WidgetPhraseMatchResult matchResult = null;
        List<WidgetPhraseMatchResult> widgetList = HABApplication.getOpenHABWidgetProvider().getWidgetByLabel(unitTagPhrase, this);
        Iterator<WidgetPhraseMatchResult> widgetResultIterator = widgetList.iterator();
        while(widgetResultIterator.hasNext()) {
            matchResult = widgetResultIterator.next();
            if (matchResult.getMatchPercent() > matchPoint) {
                matchPoint = matchResult.getMatchPercent();
                widgetMatch = new WidgetPhraseMatchResult(matchPoint, matchResult.getWidget());
            }
        }
        return widgetMatch;
    }

    public String getUnitPhrase(CommandPhraseMatchResult commandPhraseMatchResult) {
        String unitTagPhrase = null;
        for(int i = 0; i < commandPhraseMatchResult.getTags().length; i++) {
            if(commandPhraseMatchResult.getTags()[i].equalsIgnoreCase("<unit>")) {
                unitTagPhrase = commandPhraseMatchResult.getTagPhrases()[i];
                break;
            }
        }
        return unitTagPhrase;
    }

    protected String getRegExMatch(String source, Pattern pattern) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++)
                result += matcher.group(i) + " ";
        }
        return result.trim();
    }

    protected void initializeWidgetTypeTagMapping() {
        List<OpenHABItemType> itemTypes = new ArrayList<OpenHABItemType>();
        itemTypes.add(OpenHABItemType.Dimmer);
        itemTypes.add(OpenHABItemType.Number);
        mItemTypeTagMapping.put("<integer>", itemTypes);

        itemTypes.clear();
        itemTypes.add(OpenHABItemType.Number);
        mItemTypeTagMapping.put("<decimal>", itemTypes);

        itemTypes.clear();
        itemTypes.add(OpenHABItemType.Color);
        mItemTypeTagMapping.put("<color>", itemTypes);

        itemTypes.clear();
        itemTypes.add(OpenHABItemType.String);
        mItemTypeTagMapping.put("<text>", itemTypes);
    }

    public String getRegExStringForMatchAccuracySource(String[] source) {
        StringBuilder sb = new StringBuilder();
        for(String partOfLabel : source)
            sb.append((sb.length() > 1? "|" : "") + "(" + partOfLabel.toUpperCase() + ")");

        return sb.toString();
    }

    public double getStringMatchAccuracy(String source, List<String> sourceWordsList, String regEx, String target) {
        double wordCountAccuracy = 0;
        double orderAccuracy = 0;
        double lengthDifferenceAccuracy = 0;
        int totalMatchLength = 0;

        target = target.toUpperCase();
        RegExResult regExResult = HABApplication.getRegularExpression().getAllNextMatchAsList(regEx, target);
        wordCountAccuracy = regExResult.GroupList.size() / sourceWordsList.size();
        int lastListMatchIndex = -1;
        for (int i = 0; i < regExResult.GroupList.size(); i++) {
            totalMatchLength += regExResult.GroupList.get(i).length() + 1;
            int listMatchIndex = sourceWordsList.indexOf(regExResult.GroupList.get(i));
            if (listMatchIndex > lastListMatchIndex) {
                lastListMatchIndex = listMatchIndex;
                orderAccuracy++;
                continue;
            }
        }
        if (orderAccuracy > 0)
            orderAccuracy = orderAccuracy / sourceWordsList.size();

        totalMatchLength -= 1;
        lengthDifferenceAccuracy = Math.abs(totalMatchLength - target.length());
        if(lengthDifferenceAccuracy >= 0)
            lengthDifferenceAccuracy = 1 - (lengthDifferenceAccuracy * 0.15);
        if(lengthDifferenceAccuracy < 0)
            lengthDifferenceAccuracy = 0;

        return sourceWordsList.size() > 0? (wordCountAccuracy + orderAccuracy + lengthDifferenceAccuracy) / 3 : 0;
    }
}
