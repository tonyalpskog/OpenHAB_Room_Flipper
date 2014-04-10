package com.zenit.habclient.command.test;

import android.util.Log;

import com.zenit.habclient.ApplicationMode;
import com.zenit.habclient.HABApplication;
import com.zenit.habclient.OpenHABWidgetProvider;
import com.zenit.habclient.Room;
import com.zenit.habclient.RoomFlipper;
import com.zenit.habclient.RoomProvider;
import com.zenit.habclient.CommandAnalyzer;
import com.zenit.habclient.TextToSpeechProvider;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        // Fill the list view with the strings the recognizer thought it
        // could have heard
        List<OpenHABWidget> widgetList = getListOfWidgetsFromListOfRooms(listOfRooms);
        if(widgetList.size() == 0) {
            widgetList = habApplication.getOpenHABWidgetProvider().getWidgetList((Set<OpenHABWidgetType>) null);
        }
        //Create widget name Map
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            widgetNameMap.put(/*nextWidget.hasItem()? nextWidget.getItem().getName() : */getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase(), nextWidget);
        }

        //Look for match
        for(String match : commandPhrases.toArray(new String[0])) {
//            OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
//            resultList.add(foundWidget);
            //for(String unitName : (widgetNameMap.ke)
            if(widgetNameMap.keySet().contains(match.toUpperCase())) {
                //Got a unit match.
                OpenHABWidget foundWidget = widgetNameMap.get(match.toUpperCase());
                resultList.add(foundWidget);
                Log.d(habApplication.getLogTag(), "Found unit in command phrase <" + foundWidget.getLabel() + ">");
            }
        }

        return resultList;
    }

    public String getPopularNameFromWidgetLabel(String openHABWidgetLabel) {
        return super.getPopularNameFromWidgetLabel(openHABWidgetLabel);
    }
}
