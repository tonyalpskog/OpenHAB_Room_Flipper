package org.openhab.habclient;

import android.app.Application;

import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;

import dagger.ObjectGraph;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {
    private ObjectGraph mObjectGraph;

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }


    public static String getLogTag() {
        return getLogTag(1);//Actually gets index 0(zero) but this call adds one more level to the stacktrace.
    }

    public static String getLogTag(int relativeTraceIndex) {
        int traceIndex = 3 + relativeTraceIndex;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[traceIndex];
        return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "()";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new AndroidModule(this), new ClientModule());
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
}
