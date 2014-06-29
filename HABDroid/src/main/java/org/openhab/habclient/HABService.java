package org.openhab.habclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by Tony Alpskog in 2014.
 */
public class HABService extends Service {

    private final IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(HABApplication.getLogTag(), "Service created");
    }

    @Override
    public void onDestroy() {
        Log.d(HABApplication.getLogTag(), "Service destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(HABApplication.getLogTag(), "Service start intent received");
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(HABApplication.getLogTag(), "UnBind request intent received");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(HABApplication.getLogTag(), "Bind request intent received");
        return mBinder;
    }

    public class MyBinder extends Binder {
        HABService getService() {
            return HABService.this;
        }
    }
}