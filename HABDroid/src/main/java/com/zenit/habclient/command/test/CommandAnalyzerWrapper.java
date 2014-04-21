package com.zenit.habclient.command.test;

import com.zenit.habclient.ApplicationMode;
import com.zenit.habclient.CommandAnalyzer;
import com.zenit.habclient.HABApplication;
import com.zenit.habclient.OpenHABWidgetProvider;
import com.zenit.habclient.Room;
import com.zenit.habclient.RoomProvider;
import com.zenit.habclient.command.CommandPhraseMatchResult;
import com.zenit.habclient.command.WidgetPhraseMatchResult;

import org.openhab.habdroid.model.OpenHABWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandAnalyzerWrapper extends CommandAnalyzer {
    public CommandAnalyzerWrapper(RoomProvider roomProvider, OpenHABWidgetProvider openHABWidgetProvider) {
        super(roomProvider, openHABWidgetProvider);
    }

    public Map<String, Room> getMapOfRoomNamesFromProvider() {
        return super.getMapOfRoomNamesFromProvider();
    }

    public List<Room> getRoomsFromPhrases(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        return super.getRoomsFromPhrases(speechResult, applicationMode);
    }

    public List<OpenHABWidget> getListOfWidgetsFromListOfRooms(List<Room> listOfRooms) {
        return super.getListOfWidgetsFromListOfRooms(listOfRooms);
    }

//    public List<OpenHABWidget> getUnitsFromPhrases(List<String> commandPhrases, List<Room> listOfRooms) {
//        return super.getUnitsFromPhrases(commandPhrases, listOfRooms);
//    }

    public List<OpenHABWidget> getUnitsFromPhrases(HABApplication habApplication, List<String> commandPhrases, List<Room> listOfRooms) {
        return super.getUnitsFromPhrases2(commandPhrases, listOfRooms, habApplication);
//        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
//
//        // Fill the list view with the strings the recognizer thought it
//        // could have heard
//        List<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();// getListOfWidgetsFromListOfRooms(listOfRooms);
////        if(widgetList.size() == 0) {
//            widgetList = habApplication.getOpenHABWidgetProvider().getWidgetList((Set<OpenHABWidgetType>) null);
////        }
//        //Create widget name Map
//        Iterator<OpenHABWidget> iterator = widgetList.iterator();
//        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
//        while (iterator.hasNext()) {
//            OpenHABWidget nextWidget = iterator.next();
//            widgetNameMap.put(/*nextWidget.hasItem()? nextWidget.getItem().getName() : */getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase(), nextWidget);
//        }
//
//        //Look for match
//        for(String match : commandPhrases.toArray(new String[0])) {
////            OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
////            resultList.add(foundWidget);
//            for(String unitName : widgetNameMap.keySet().toArray(new String[0])) {
//                if (match.toUpperCase().contains(unitName)) {
//                    //Got a unit match.
//                    OpenHABWidget foundWidget = widgetNameMap.get(unitName);
//                    resultList.add(foundWidget);
//                    Log.d(habApplication.getLogTag(), "Found unit in command phrase <" + foundWidget.getLabel() + ">");
//                }
//            }
//        }
//
//        return resultList;
    }

//    public List<OpenHABWidget> getUnitsFromPhrases(HABApplication habApplication, List<String> commandPhrases) {
//        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
//
//        // Fill the list view with the strings the recognizer thought it
//        // could have heard
//        List<OpenHABWidget> widgetList = getListOfWidgetsFromListOfRooms(listOfRooms);
//        if(widgetList.size() == 0) {
//            widgetList = habApplication.getOpenHABWidgetProvider().getWidgetList((Set<OpenHABWidgetType>) null);
//        }
//        //Create widget name Map
//        Iterator<OpenHABWidget> iterator = widgetList.iterator();
//        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
//        while (iterator.hasNext()) {
//            OpenHABWidget nextWidget = iterator.next();
//            widgetNameMap.put(/*nextWidget.hasItem()? nextWidget.getItem().getName() : */getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase(), nextWidget);
//        }
//
//        //Look for match
//        for(String match : commandPhrases.toArray(new String[0])) {
////            OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
////            resultList.add(foundWidget);
//            for(String unitName : widgetNameMap.keySet().toArray(new String[0])) {
//                if (match.toUpperCase().contains(unitName)) {
//                    //Got a unit match.
//                    OpenHABWidget foundWidget = widgetNameMap.get(unitName);
//                    resultList.add(foundWidget);
//                    Log.d(habApplication.getLogTag(), "Found unit in command phrase <" + foundWidget.getLabel() + ">");
//                }
//            }
//        }
//
//        return resultList;
//    }

    public String getPopularNameFromWidgetLabel(String openHABWidgetLabel) {
        return super.getPopularNameFromWidgetLabel(openHABWidgetLabel);
    }

    @Override
    public String replaceSubStrings(String source, String beginIncluded, String endIncluded, String replacement) {
        return super.replaceSubStrings(source, beginIncluded, endIncluded, replacement);
    }

    @Override
    public Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> getHighestWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        return super.getHighestWidgetsFromCommandMatchResult(listOfCommandResult);
    }

    @Override
    public String getRegExMatch(String source, Pattern pattern) {
        return super.getRegExMatch(source, pattern);
    }
}
