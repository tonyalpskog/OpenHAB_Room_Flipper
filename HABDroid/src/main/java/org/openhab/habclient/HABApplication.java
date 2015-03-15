package org.openhab.habclient;

import android.app.Application;

import org.openhab.habclient.dagger.AndroidApplicationModule;
import org.openhab.habclient.dagger.AppComponent;
import org.openhab.habclient.dagger.Dagger_AppComponent;

/**
 * Created by Tony Alpskog in 2013.
 */
public class HABApplication extends Application {
    private AppComponent mAppComponent;

    public AppComponent appComponent() {
        return mAppComponent;
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

        mAppComponent = Dagger_AppComponent.builder()
                .androidApplicationModule(new AndroidApplicationModule(this))
                .build();
    }
}
