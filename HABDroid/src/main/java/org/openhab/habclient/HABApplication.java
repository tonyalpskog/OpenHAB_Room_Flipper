package org.openhab.habclient;

import android.app.Application;
import android.util.Log;

import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;

import java.util.UUID;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {

    @Inject RoomProvider mRoomProvider;

    private UUID currentConfigRoom = null;
    private UUID currentFlipperRoom = null;
    private ApplicationMode mAppMode = ApplicationMode.Unknown;

    private ObjectGraph mObjectGraph;

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    public ApplicationMode getAppMode() {
        return mAppMode;
    }

    public void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
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
            return mRoomProvider.getInitialRoom();
        else
            return mRoomProvider.get(roomId);
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
