package org.openhab.habclient;

import android.util.Log;

import org.openhab.util.ILogger;

public class AndroidLogger implements ILogger {
    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void e(String tag, String message, Throwable e) {
        Log.e(tag, message, e);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void w(String tag, String message, Throwable e) {
        Log.w(tag, message, e);
    }
}
