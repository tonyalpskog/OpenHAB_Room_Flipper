package org.openhab.habclient;

import android.app.Application;
import android.util.Log;

import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.RegularExpression;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;
import org.openhab.habdroid.ui.WidgetTypeLayoutProvider;

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
    private RuleOperationProvider mRuleOperationProvider = null;
    private IRestCommunication mRestCommunication = null;
    private ApplicationMode mAppMode = ApplicationMode.Unknown;
    private TextToSpeechProvider mTextToSpeechProvider = null;
    private OpenHABWidgetProvider mOpenHABWidgetProvider = null;
    private ICommandAnalyzer mSpeechResultAnalyzer = null;
    private RegularExpression mRegularExpression = null;
    private OpenHABWidgetControl mOpenHABWidgetControl = null;
    private IWidgetTypeLayoutProvider mWidgetTypeLayoutProvider;
    private ILogger mLogger;
    private IColorParser mColorParser;
    private OpenHABSetting mOpenHABSetting;

    public ILogger getLogger() {
        if(mLogger == null)
            mLogger = new AndroidLogger();

        return mLogger;
    }

    public IColorParser getColorParser() {
        if(mColorParser == null)
            mColorParser = new ColorParser();

        return mColorParser;
    }

    public ICommandAnalyzer getSpeechResultAnalyzer() {
        if(mSpeechResultAnalyzer == null)
            mSpeechResultAnalyzer = new CommandAnalyzer(getRoomProvider(),
                    getOpenHABWidgetProvider(), getApplicationContext(), getOpenHABWidgetControl(),
                    getRegularExpression());

        mSpeechResultAnalyzer.setTextToSpeechProvider(getTextToSpeechProvider());
        return mSpeechResultAnalyzer;
    }

    public TextToSpeechProvider getTextToSpeechProvider() {
        if(mTextToSpeechProvider == null)
            mTextToSpeechProvider = new TextToSpeechProvider(getApplicationContext(), Locale.ENGLISH);
        return mTextToSpeechProvider;
    }

    public ApplicationMode getAppMode() {
        return mAppMode;
    }

    public void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
    }

    public OpenHABWidgetProvider getOpenHABWidgetProvider() {
        if(mOpenHABWidgetProvider == null)
            mOpenHABWidgetProvider = new OpenHABWidgetProvider(getRegularExpression());

        return mOpenHABWidgetProvider;
    }

    public IRestCommunication getRestCommunication() {
        if(mRestCommunication == null)
            mRestCommunication = new RestCommunication(this, getLogger(), getColorParser(),
                    getOpenHABSetting(),
                    getOpenHABWidgetProvider());

        return mRestCommunication;
    }


    public OpenHABSetting getOpenHABSetting() {
        if(mOpenHABSetting == null)
            mOpenHABSetting = new OpenHABSetting(this);

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
        if(mRoomProvider == null)
            mRoomProvider = new RoomProvider(getApplicationContext(), getLogger(),
                    getColorParser(), getOpenHABSetting(), getOpenHABWidgetProvider());

        return mRoomProvider;
    }

    public RuleOperationProvider getRuleOperationProvider() {
        if(mRuleOperationProvider == null)
            mRuleOperationProvider = new RuleOperationProvider();

        return mRuleOperationProvider;
    }

    public RegularExpression getRegularExpression() {
        if(mRegularExpression == null)
            mRegularExpression = new RegularExpression();

        return mRegularExpression;
    }

    public OpenHABWidgetControl getOpenHABWidgetControl() {
        if(mOpenHABWidgetControl == null)
            mOpenHABWidgetControl = new OpenHABWidgetControl(this, getOpenHABWidgetProvider());

        return mOpenHABWidgetControl;
    }

    public IWidgetTypeLayoutProvider getWidgetTypeLayoutProvider() {
        if(mWidgetTypeLayoutProvider == null)
            mWidgetTypeLayoutProvider = new WidgetTypeLayoutProvider();

        return mWidgetTypeLayoutProvider;
    }
}
