package org.openhab.domain.command;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.ITextToSpeechProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.domain.model.Room;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.StringHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static org.openhab.domain.command.OpenHABWidgetCommandType.GetStatus;
import static org.openhab.domain.command.OpenHABWidgetCommandType.values;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandAnalyzer implements ICommandAnalyzer {

    private static final String TAG = "CommandAnalyzer";
    private final IRoomProvider mRoomProvider;
    private final IOpenHABWidgetProvider mOpenHABWidgetProvider;
    private final IOpenHABWidgetControl mWidgetControl;
    private final IRegularExpression mRegularExpression;
    private final IPopularNameProvider mPopularNameProvider;
    private final ICommandPhrasesProvider mCommandPhrasesProvider;
    private final ILogger mLogger;
    private final ICommandColorProvider mCommandColorProvider;
    protected ITextToSpeechProvider mTextToSpeechProvider;
    protected Map<String, List<OpenHABWidgetType>> mWidgetTypeTagMapping = new HashMap<String, List<OpenHABWidgetType>>();
    protected Map<String, List<OpenHABItemType>> mItemTypeTagMapping = new HashMap<String, List<OpenHABItemType>>();
    protected Map<String, String> mCommandTagsRegex = new HashMap<String, String>();
    private OnShowRoomListener mOnShowRoomListener;

    @Inject
    public CommandAnalyzer(IRoomProvider roomProvider,
                           OpenHABWidgetProvider openHABWidgetProvider,
                           IOpenHABWidgetControl widgetControl,
                           IRegularExpression regularExpression,
                           IPopularNameProvider popularNameProvider,
                           ICommandPhrasesProvider commandPhrasesProvider,
                           ILogger logger,
                           ICommandColorProvider commandColorProvider) {
        mRoomProvider = roomProvider;
        mOpenHABWidgetProvider = openHABWidgetProvider;
        mWidgetControl = widgetControl;
        mRegularExpression = regularExpression;
        mPopularNameProvider = popularNameProvider;
        mCommandPhrasesProvider = commandPhrasesProvider;
        mLogger = logger;
        mCommandColorProvider = commandColorProvider;

        initializeWidgetTypeTagMapping();
        initializeCommandTagMapping();
    }

    @Override
    public SpeechAnalyzerResult executeSpeech(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        return SpeechAnalyzerResult.ContinueListening;
    }

    @Override
    public SpeechAnalyzerResult analyzeRoomNavigation(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<Room> roomList = new ArrayList<Room>();

        if(mRoomProvider != null)
            roomList.addAll(mRoomProvider.getAllRooms());

        Iterator<Room> iterator = roomList.iterator();
        Map<String, Room> roomNameMap = new HashMap<String, Room>();
        while (iterator.hasNext()) {
            Room nextRoom = iterator.next();
            roomNameMap.put(nextRoom.getName().toUpperCase(), nextRoom);
        }

        if(applicationMode == ApplicationMode.RoomFlipper) {
            for(String match : speechResult) {
                if(roomNameMap.keySet().contains(match.toUpperCase())) {
                    //Got a speech match.
                    Room roomToShow = roomNameMap.get(match.toUpperCase());
                    mLogger.d(TAG, "showRoom() - Show room<" + roomToShow.getId() + ">");
                    if(mOnShowRoomListener != null)
                        mOnShowRoomListener.onShowRoom(roomToShow);

                    if(mTextToSpeechProvider != null)
                        mTextToSpeechProvider.speakText(roomToShow.getName());
                    else
                        mLogger.e(TAG, "TextToSpeechProvider is NULL!");
                }
            }
        }

        for(String match : speechResult) {
            if("hej då huset".equalsIgnoreCase(match)) {
                //End HAB application
                mLogger.d(TAG, "Got a match: '" + match + "'");
                break;
            }
        }

        return SpeechAnalyzerResult.ContinueListening;
    }

    @Override
    public CommandAnalyzerResult analyzeCommand(List<String> commandPhrases, ApplicationMode applicationMode) {
        List<CommandPhraseMatchResult> commandMatchResultList = getCommandsFromPhrases(commandPhrases);
        Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> unitMatchResult = getHighestWidgetsFromCommandMatchResult(commandMatchResultList);
        if(unitMatchResult.size() == 0)
            return null;
        int topScore = 0;
        CommandPhraseMatchResult bestKeySoFar = null;

        for (CommandPhraseMatchResult cpmr : unitMatchResult.keySet()) {
            if(unitMatchResult.get(cpmr) == null)
                continue;

            int currentScore = (cpmr.getPoint() * 30) + unitMatchResult.get(cpmr).getMatchPercent();
            if (currentScore > topScore) {
                topScore = currentScore;
                bestKeySoFar = cpmr;
            }
        }

        final OpenHABWidgetCommandType commandType = bestKeySoFar != null ? bestKeySoFar.getCommandType() : null;
        String commandReply = null;

        if(bestKeySoFar == null)
            return null;

        if(bestKeySoFar.getCommandType() != GetStatus) {
            String value = getCommandValue(bestKeySoFar);
            if(value != null) {
                mWidgetControl.sendItemCommand(unitMatchResult.get(bestKeySoFar).getWidget(), value);
                commandReply = value;
            }
        }

        if (commandReply != null)
            return new CommandAnalyzerResult(null, unitMatchResult.get(bestKeySoFar).getWidget(), topScore, commandReply, commandType);
        else
            return new CommandAnalyzerResult(null, unitMatchResult.get(bestKeySoFar).getWidget(), topScore, unitMatchResult.get(bestKeySoFar).getWidget().getLabelValue(), commandType);
    }

    @Override
    public void setOnShowRoomListener(OnShowRoomListener listener) {
        mOnShowRoomListener = listener;
    }

    public String getCommandValue(CommandPhraseMatchResult commandPhraseMatchResult) {
        String valueTypeToLookFor;
        switch (commandPhraseMatchResult.getCommandType()) {
            case AdjustSetpoint: valueTypeToLookFor = "<decimal>";
                break;
            case SliderSetPercentage: valueTypeToLookFor = "<integer>";
                break;
            case SwitchOn: return "ON";
            case SwitchOff: return "OFF";
            case RollerShutterDown: return "DOWN";
            case RollerShutterUp: return "UP";
            default: return null;
            //TODO - Extend this list of supported item type commands
        }

        for(int i = 0; i < commandPhraseMatchResult.getTags().length; i++ ) {
            if(commandPhraseMatchResult.getTags()[i].equalsIgnoreCase(valueTypeToLookFor))
                return commandPhraseMatchResult.getTagPhrases()[i];
        }

        return null;
    }

    public String getCommandReply(CommandAnalyzerResult commandAnalyzerResult) {
        String result;
        if(commandAnalyzerResult.getCommandType() == GetStatus) {
            result = mPopularNameProvider.getPopularNameFromWidgetLabel(commandAnalyzerResult.getOpenHABWidget().getLabel()) + " is " + commandAnalyzerResult.getOpenHABItemState();//TODO add language support

        } else {
            result = mPopularNameProvider.getPopularNameFromWidgetLabel(commandAnalyzerResult.getOpenHABWidget().getLabel()) + " was set to " + commandAnalyzerResult.getOpenHABItemState();//TODO add language support
        }

        return result;
    }

    protected List<Room> getRoomsFromPhrases(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        List<Room> resultList = new ArrayList<Room>();

        Map<String, Room> roomNameMap = mRoomProvider.getMapOfRoomNames();

        if(applicationMode == ApplicationMode.RoomFlipper) {
            for(String roomName : roomNameMap.keySet()) {
                for (String match : speechResult) {
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

    protected List<OpenHABWidget> getUnitsFromPhrases(List<String> commandPhrases, List<Room> listOfRooms) {
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<OpenHABWidget> widgetList = mOpenHABWidgetProvider.getListOfWidgetsFromListOfRooms(listOfRooms);
        if(widgetList.size() == 0) {
            widgetList = mOpenHABWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null);
        }
        //Create widget name Map
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            widgetNameMap.put(nextWidget.getLabel().toUpperCase(), nextWidget);
        }

        //Look for match
        for(String match : commandPhrases) {
            if(widgetNameMap.keySet().contains(match.toUpperCase())) {
                //Got a unit match.
                OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
                resultList.add(foundWidget);
                mLogger.d(TAG, "Found unit in command phrase <" + foundWidget.getLabel() + ">");
            }
        }

        return resultList;
    }

    protected List<OpenHABWidget> getUnitsFromPhrases2(List<String> commandPhrases) {
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        List<OpenHABWidget> widgetList = mOpenHABWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null);
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            widgetNameMap.put(mPopularNameProvider.getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase(), nextWidget);
        }

        //Look for phrases
        for(String phrase : commandPhrases) {
            for(String unitName : widgetNameMap.keySet()) {
                if (!StringHandler.isNullOrEmpty(unitName) && unitName.contains(phrase.toUpperCase())) {
                    //Got a unit match.
                    OpenHABWidget foundWidget = widgetNameMap.get(unitName);
                    resultList.add(foundWidget);
                    mLogger.d("CommandAnalyzer", "Found unit in command phrase <" + foundWidget.getLabel() + ">");
                }
            }
        }

        return resultList;
    }

    public String replaceCommandTagsWithRegEx(String source) {
        String regexCommand = StringHandler.replaceSubStrings(source, "<", ">", "(.+)").toUpperCase() + "\\z";
        List<String> tags = mRegularExpression.getAllNextMatchAsList(regexCommand, source, true).GroupList;
        String result = source;
        for(String tag : tags) {
            if(mCommandTagsRegex.containsKey(tag.toLowerCase()))
                result = result.replaceAll(tag, mCommandTagsRegex.get(tag.toLowerCase()));
        }
        return "\\A" + result + "\\z";
    }

    public List<String> getMatchingRegExGroups(String regex, String target) {
        return mRegularExpression.getAllNextMatchAsList(regex, target, true).GroupList;
    }

    /**
     * Example commandPhrases = "Turn on Kitchen ceiling lights" will result in a single mapping
     * of "KITCHEN CEILING LIGHTS" as key and OpenHABWidgetCommandType.SwitchOn as value.
     * CommandPhraseMatchResult objects with the highest points will be placed first in list.
     * @param commandPhrases
     * @return a Map of keys as upper case phrase strings with the command excluded and command types as values.
     */
    public List<CommandPhraseMatchResult> getCommandsFromPhrases(List<String> commandPhrases) {
        final List<CommandPhraseMatchResult> commandPhraseMatchResultList = new ArrayList<CommandPhraseMatchResult>();
        for(String phrase : commandPhrases) {
            if(phrase == null)
                continue;

            commandPhraseMatchResultList.addAll(getCommandsFromPhrase(phrase.toUpperCase()));
        }
        return  commandPhraseMatchResultList;
    }

    private List<CommandPhraseMatchResult> getCommandsFromPhrase(String phrase) {
        final List<CommandPhraseMatchResult> commandPhraseMatchResultList = new ArrayList<CommandPhraseMatchResult>();
        for(OpenHABWidgetCommandType commandType : values()) {
            if(commandType == null)
                continue;

            commandPhraseMatchResultList.addAll(getCommandsFromCommandType(phrase, commandType));
        }
        return commandPhraseMatchResultList;
    }

    private List<CommandPhraseMatchResult> getCommandsFromCommandType(String phrase, OpenHABWidgetCommandType commandType) {
        final List<CommandPhraseMatchResult> commandPhraseMatchResultList = new ArrayList<CommandPhraseMatchResult>();

        for(String commandAsText : mCommandPhrasesProvider.getCommandPhrases(commandType)) {
            commandAsText = commandAsText.toUpperCase();
            final int matchPoints = StringHandler.replaceSubStrings(commandAsText, "<", ">", "").split("\\s+").length;
            final String regexCommand = "\\A" + StringHandler.replaceSubStrings(commandAsText, "<", ">", "(.+)").toUpperCase() + "\\z";
            final Pattern pattern = Pattern.compile(replaceCommandTagsWithRegEx(commandAsText), Pattern.CASE_INSENSITIVE);
            final Matcher matcher = pattern.matcher(phrase);

            if (!matcher.find())
                continue;

            final List<String> tagPhrases = new ArrayList<String>();
            for(int i = 1; i <= matcher.groupCount(); i++)
                if(matcher.group(i) != null && !matcher.group(i).isEmpty())
                    tagPhrases.add(matcher.group(i));

            final List<String> tags = getMatchingRegExGroups(regexCommand, commandAsText);
            int listIndex = 0;
            if(commandPhraseMatchResultList.size() > 0) {
                for(; listIndex < commandPhraseMatchResultList.size();) {
                    if(commandPhraseMatchResultList.get(listIndex).getPoint() < matchPoints)
                        break;
                    listIndex++;
                }
            }

            commandPhraseMatchResultList.add(listIndex, new CommandPhraseMatchResult(commandType,
                    tags.toArray(new String[tags.size()]),
                    tagPhrases.toArray(new String[tagPhrases.size()]),
                    matchPoints));
        }

        return commandPhraseMatchResultList;
    }

    public Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> getHighestWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        //TODO - Compare the tag type with item and widget type. No tag<->type match = No unit result.

        Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> resultMap = new HashMap<CommandPhraseMatchResult, WidgetPhraseMatchResult>();

        for (CommandPhraseMatchResult commandPhraseMatchResult : listOfCommandResult) {
            WidgetPhraseMatchResult widgetMatch = getMostProbableWidgetFromCommandMatchResult(commandPhraseMatchResult);
            //TODO - Implement call to method that compares the tag type with item and widget type. No tag<->type match = No unit result
            String[] valueTags = getValueTagType(commandPhraseMatchResult);
            if (valueTags.length > 1)
                mLogger.w(TAG, "Command phrase contains more than one value tag: " + valueTags.length);

            if (widgetMatch != null && (valueTags.length > 0 && doesTagTypeMatchWidgetType(valueTags[0], widgetMatch.getWidget())) || valueTags.length == 0)
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

        return tagList.toArray(new String[tagList.size()]);
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

        for (CommandPhraseMatchResult commandPhraseMatchResult : listOfCommandResult) {
            resultMap.put(commandPhraseMatchResult, getWidgetsFromCommandMatchResult(commandPhraseMatchResult));
        }

        return resultMap;
    }

    public List<WidgetPhraseMatchResult> getWidgetsFromCommandMatchResult(CommandPhraseMatchResult commandPhraseMatchResult) {
        String unitTagPhrase = getUnitPhrase(commandPhraseMatchResult);

        //Add all found widgets no matter their match points.
        return mOpenHABWidgetProvider.getWidgetByLabel(unitTagPhrase);
    }

    public WidgetPhraseMatchResult getMostProbableWidgetFromCommandMatchResult(CommandPhraseMatchResult commandPhraseMatchResult) {
        String unitTagPhrase = getUnitPhrase(commandPhraseMatchResult);

        //Only add the widget with the highest match point to the resulting list.
        int matchPoint = 0;
        WidgetPhraseMatchResult widgetMatch = null;
        WidgetPhraseMatchResult matchResult = null;
        List<WidgetPhraseMatchResult> widgetList = mOpenHABWidgetProvider.getWidgetByLabel(unitTagPhrase);
        for (WidgetPhraseMatchResult aWidgetList : widgetList) {
            matchResult = aWidgetList;
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
        mItemTypeTagMapping.put("<integer>", Arrays.asList(OpenHABItemType.Dimmer, OpenHABItemType.Number));
        mItemTypeTagMapping.put("<decimal>", Arrays.asList(OpenHABItemType.Number));
        mItemTypeTagMapping.put("<color>", Arrays.asList(OpenHABItemType.Color));
        mItemTypeTagMapping.put("<text>", Arrays.asList(OpenHABItemType.String));
    }

    protected void initializeCommandTagMapping() {
        mCommandTagsRegex.put("<integer>", "([0-9]+)");
        mCommandTagsRegex.put("<decimal>", "([0-9.,]+)");
        mCommandTagsRegex.put("<text>", "(.+)");
        mCommandTagsRegex.put("<unit>", "(.+)");
        mCommandTagsRegex.put("<selection>", "(.+)");

        //<color> is a bit special...
        String[] colorNames = mCommandColorProvider.getColorNames();
        StringBuilder colorRegEx = new StringBuilder();
        for(String colorName : colorNames)
            colorRegEx.append(colorRegEx.length() == 0? colorName : "|" + colorName);
        mCommandTagsRegex.put("<color>", "(" + colorRegEx.toString() + ")");
    }
}
