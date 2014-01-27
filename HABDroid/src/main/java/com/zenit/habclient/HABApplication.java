package com.zenit.habclient;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;
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


    public static ApplicationMode getAppMode() {
        return mAppMode;
    }

    public static void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
    }

    private static OpenHABWidgetProvider mOpenHABWidgetProvider = null;
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

    public static String GetLogTag() {
        return GetLogTag(1);//Actually gets index 0(zero) but this call adds one more level to the stacktrace.
    }

    public static String GetLogTag(int relativeTraceIndex) {
        int traceIndex = 3 + relativeTraceIndex;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[traceIndex];
        return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "()";
    }

    public Room getConfigRoom() {
        Room room = getRoom(currentConfigRoom);
        currentConfigRoom = room.getId();
        Log.d(GetLogTag(), String.format("Getting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupItemName(), room.getId()));
        return room;
    }

    public void setConfigRoom(Room room) {
        Log.d(GetLogTag(), String.format("Setting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupItemName(), room.getId()));
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
        if(mRoomProvider == null)
            mRoomProvider = new RoomProvider(getApplicationContext());

        if(roomId == null)
            return mRoomProvider.getInitialRoom();
        else
            return mRoomProvider.get(roomId);
    }

    public RoomProvider getRoomProvider() {
        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        if(mRuleOperationProvider == null)
            mRuleOperationProvider = new RuleOperationProvider();

        return mRuleOperationProvider;
    }

}
