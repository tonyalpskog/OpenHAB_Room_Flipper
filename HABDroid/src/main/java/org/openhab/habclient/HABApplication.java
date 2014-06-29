package org.openhab.habclient;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.util.RegularExpression;
import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;
import org.openhab.habdroid.ui.WidgetTypeLayoutProvider;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {

    //TA: TODO - Replace all static usage of members and methods. Call HABApplication as (HABApplication)getActivity().getApplication() -> Create a method for this in all classes.

    private RoomProvider mRoomProvider = null;
    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;
    private static RuleOperationProvider mRuleOperationProvider = null;
    private static RestCommunication mRestCommunication = null;
    private static ApplicationMode mAppMode = ApplicationMode.Unknown;
    private TextToSpeechProvider mTextToSpeechProvider = null;
    private static OpenHABWidgetProvider mOpenHABWidgetProvider = null;
    private static ICommandAnalyzer mSpeechResultAnalyzer = null;
    private static RegularExpression mRegularExpression = null;
    private static OpenHABWidgetControl mOpenHABWidgetControl = null;
    private IWidgetTypeLayoutProvider mWidgetTypeLayoutProvider;

    public ICommandAnalyzer getSpeechResultAnalyzer() {
        if(mSpeechResultAnalyzer == null)
            mSpeechResultAnalyzer = new CommandAnalyzer(getRoomProvider(), getOpenHABWidgetProvider(), getApplicationContext(), getOpenHABWidgetControl());

        mSpeechResultAnalyzer.setTextToSpeechProvider(getTextToSpeechProvider());
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

    public OpenHABWidgetProvider getOpenHABWidgetProvider() {
        return getOpenHABWidgetProvider2();
    }

    public static OpenHABWidgetProvider getOpenHABWidgetProvider2() {
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
    public static OpenHABSetting getOpenHABSetting(Context context) {
        if(mOpenHABSetting == null)
            mOpenHABSetting = new OpenHABSetting(context);
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
        if(currentConfigRoom == null)
            return null;

        return getRoom(currentConfigRoom);
//        Log.d(getLogTag(), String.format("Getting config room: name = '%s'  item name = '%s'  UUID = '%s'", room.getName(), room.getGroupWidgetId(), room.getId()));
    }

    public void setConfigRoom(Room room) {
        if(room != null) {
            Log.d(getLogTag(), String.format("Setting config room: name = '%s'  widget UUID = '%s' room UUID = '%s'", room.getName(), room.getRoomWidget() != null? room.getRoomWidget().getId() : "<Widget is NULL>", room.getId()));
            currentConfigRoom = room.getId();
        } else {
            Log.d(getLogTag(), "Setting config room: New Room");
            currentConfigRoom = null;
        }
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
        final ILogger logger = new AndroidLogger();
        final IColorParser colorParser = new ColorParser();

        if(mRoomProvider == null)
            mRoomProvider = new RoomProvider(getApplicationContext(), logger, colorParser);

        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        if(mRuleOperationProvider == null)
            mRuleOperationProvider = new RuleOperationProvider();

        return mRuleOperationProvider;
    }

    public static RegularExpression getRegularExpression() {
        if(mRegularExpression == null)
            mRegularExpression = new RegularExpression();

        return mRegularExpression;
    }

    public OpenHABWidgetControl getOpenHABWidgetControl() {
        if(mOpenHABWidgetControl == null)
            mOpenHABWidgetControl = new OpenHABWidgetControl(this);

        return mOpenHABWidgetControl;
    }

    public IWidgetTypeLayoutProvider getWidgetTypeLayoutProvider() {
        if(mWidgetTypeLayoutProvider == null)
            mWidgetTypeLayoutProvider = new WidgetTypeLayoutProvider();

        return mWidgetTypeLayoutProvider;
    }
}
