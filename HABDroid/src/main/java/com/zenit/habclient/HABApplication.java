package com.zenit.habclient;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {

    //TA: TODO - Replace all static usage of members and methods. Call HABApplication as (HABApplication)getActivity().getApplication() -> Create a method for this in all classes.

    private HashMap<UUID, HashMap<UUID, GraphicUnit>> roomUnitList = new HashMap<UUID, HashMap<UUID, GraphicUnit>>();
    private RoomProvider mRoomProvider = null;
    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;
    private RuleOperationProvider mRuleOperationProvider = null;
    private static RestCommunication mRestCommunication = null;
    private static ApplicationMode mAppMode = ApplicationMode.Unknown;
    private TextToSpeechProvider mTextToSpeechProvider = null;
    private static OpenHABWidgetProvider mOpenHABWidgetProvider = null;
    private static SpeechResultAnalyzer mSpeechResultAnalyzer = null;

    public SpeechResultAnalyzer getSpeechResultAnalyzer() {
        if(mSpeechResultAnalyzer == null)
            mSpeechResultAnalyzer = new SpeechResultAnalyzer(getRoomProvider(), getOpenHABWidgetProvider());

        return mSpeechResultAnalyzer;
    }

    public TextToSpeechProvider getTextToSpeechProvider() {
        if(mTextToSpeechProvider == null)
            mTextToSpeechProvider = new TextToSpeechProvider(getApplicationContext(), Locale.ENGLISH);
        return mTextToSpeechProvider;
    }

    public static ApplicationMode getAppMode() {
        return mAppMode;
    }

    public static void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
    }

    public static OpenHABWidgetProvider getOpenHABWidgetProvider() {
        if(mOpenHABWidgetProvider == null)
            mOpenHABWidgetProvider = new OpenHABWidgetProvider();

        return mOpenHABWidgetProvider;
    }

    public static RestCommunication getRestCommunication() {
        if(mRestCommunication == null)
            mRestCommunication = new RestCommunication();

        return mRestCommunication;
    }

    private static OpenHABSetting mOpenHABSetting;
    public static OpenHABSetting getOpenHABSetting() {
        if(mOpenHABSetting == null)
            mOpenHABSetting = new OpenHABSetting();
        return mOpenHABSetting;
    }

    public static String getLogTag() {
        return getLogTag(1);//Actually gets index 0(zero) but this call adds one more level to the stacktrace.
    }

    public static String getLogTag(int relativeTraceIndex) {
        int traceIndex = 3 + relativeTraceIndex;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[traceIndex];
        return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "()";
    }

    public Room getConfigRoom() {
        Room room = getRoom(currentConfigRoom);
        currentConfigRoom = room.getId();
        Log.d(getLogTag(), String.format("Getting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupItemName(), room.getId()));
        return room;
    }

    public void setConfigRoom(Room room) {
        Log.d(getLogTag(), String.format("Setting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupItemName(), room.getId()));
        currentConfigRoom = room.getId();
    }

    public Room getFlipperRoom() {
        Room room = getRoom(currentFlipperRoom);
        currentFlipperRoom = room.getId();
        return room;
    }

    public void setFlipperRoom(Room room) {
        currentFlipperRoom = room.getId();
    }

    private Room getRoom(UUID roomId) {
        if(roomId == null)
            return getRoomProvider().getInitialRoom();
        else
            return getRoomProvider().get(roomId);
    }

    public RoomProvider getRoomProvider() {
        if(mRoomProvider == null)
            mRoomProvider = new RoomProvider(getApplicationContext());

        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        if(mRuleOperationProvider == null)
            mRuleOperationProvider = new RuleOperationProvider();

        return mRuleOperationProvider;
    }

}
