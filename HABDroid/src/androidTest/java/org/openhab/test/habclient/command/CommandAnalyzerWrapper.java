package org.openhab.test.habclient.command;

import android.content.Context;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.habclient.ApplicationMode;
import org.openhab.habclient.IRoomProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.habclient.Room;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.CommandPhraseMatchResult;
import org.openhab.domain.command.WidgetPhraseMatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class CommandAnalyzerWrapper extends CommandAnalyzer {
    @Inject
    public CommandAnalyzerWrapper(IRoomProvider roomProvider,
                                  OpenHABWidgetProvider openHABWidgetProvider, Context context,
                                  IOpenHABWidgetControl widgetControl,
                                  IRegularExpression regularExpression,
                                  IPopularNameProvider popularNameProvider) {
        super(roomProvider, openHABWidgetProvider, context, widgetControl, regularExpression, popularNameProvider);
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

    public List<OpenHABWidget> getUnitsFromPhrases(List<String> commandPhrases, List<Room> listOfRooms) {
        return super.getUnitsFromPhrases2(commandPhrases);
//        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
//
//        // Fill the list view with the strings the recognizer thought it
//        // could have heard
//        List<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();// getListOfWidgetsFromListOfRooms(listOfRooms);
////        if(widgetList.size() == 0) {
//            widgetList = habApplication.getOpenHABWidgetProvider2().getWidgetList((Set<OpenHABWidgetType>) null);
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
//            widgetList = habApplication.getOpenHABWidgetProvider2().getWidgetList((Set<OpenHABWidgetType>) null);
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

    @Override
    public Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> getHighestWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        return super.getHighestWidgetsFromCommandMatchResult(listOfCommandResult);
    }

    @Override
    public String getRegExMatch(String source, Pattern pattern) {
        return super.getRegExMatch(source, pattern);
    }
}
