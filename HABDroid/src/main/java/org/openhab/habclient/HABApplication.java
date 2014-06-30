package org.openhab.habclient;

import android.app.Application;
import android.util.Log;

import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;
import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;

import java.util.UUID;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {

    @Inject RoomProvider mRoomProvider = null;
    @Inject RuleOperationProvider mRuleOperationProvider = null;
    @Inject IRestCommunication mRestCommunication;
    @Inject TextToSpeechProvider mTextToSpeechProvider;
    @Inject OpenHABWidgetProvider mOpenHABWidgetProvider;
    @Inject ICommandAnalyzer mSpeechResultAnalyzer;
    @Inject IRegularExpression mRegularExpression;
    @Inject OpenHABWidgetControl mOpenHABWidgetControl;
    @Inject IWidgetTypeLayoutProvider mWidgetTypeLayoutProvider;
    @Inject ILogger mLogger;
    @Inject IColorParser mColorParser;
    @Inject IOpenHABSetting mOpenHABSetting;

    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;
    private ApplicationMode mAppMode = ApplicationMode.Unknown;
    private ObjectGraph mObjectGraph;

    public ILogger getLogger() {
        return mLogger;
    }

    public IColorParser getColorParser() {
        return mColorParser;
    }

    public ICommandAnalyzer getSpeechResultAnalyzer() {
        return mSpeechResultAnalyzer;
    }

    public ApplicationMode getAppMode() {
        return mAppMode;
    }

    public void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
    }

    public OpenHABWidgetProvider getOpenHABWidgetProvider() {
        return mOpenHABWidgetProvider;
    }

    public IRestCommunication getRestCommunication() {
        return mRestCommunication;
    }
    public IOpenHABSetting getOpenHABSetting() {
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
        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        return mRuleOperationProvider;
    }

    public IRegularExpression getRegularExpression() {
        return mRegularExpression;
    }

    public OpenHABWidgetControl getOpenHABWidgetControl() {
        return mOpenHABWidgetControl;
    }

    public IWidgetTypeLayoutProvider getWidgetTypeLayoutProvider() {
        return mWidgetTypeLayoutProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new AndroidModule(this), new ClientModule());
        inject(this);
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
}
