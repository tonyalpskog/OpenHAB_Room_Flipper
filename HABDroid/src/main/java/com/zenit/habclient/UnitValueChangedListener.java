package com.zenit.habclient;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitValueChangedListener {
    Map<String, List<OnValueChangedListener>> mOnValueChangedListeners = new HashMap<String, List<OnValueChangedListener>>();

//    public void registerOnValueChangedListener(OnValueChangedListener eventListener, List<OnValueChangedListener> listenersToListenFor) {
//        Iterator<OnValueChangedListener> iterator = listenersToListenFor.iterator();
//        while(iterator.hasNext()) {
//            OnValueChangedListener subListener = iterator.next();
//            String listenerId = subListener.getDataSourceId();
//            if(!StringHandler.isNullOrEmpty(listenerId))
//                registerOnValueChangedListener(eventListener, listenerId);
//        }
//    }

    public void registerOnValueChangedListener(OnValueChangedListener eventListener, String dataSourceId) {
        if(!mOnValueChangedListeners.containsKey(dataSourceId)) {
            List<OnValueChangedListener> newList = new ArrayList<OnValueChangedListener>();
            newList.add(eventListener);
            mOnValueChangedListeners.put(dataSourceId, newList);
        } else {
            List<OnValueChangedListener> listenerList = mOnValueChangedListeners.get(dataSourceId);
            if(!listenerList.contains(eventListener))
                listenerList.add(eventListener);
        }
    }

    public void unregisterOnValueChangedListener(OnValueChangedListener eventListener) {
        Iterator<List<OnValueChangedListener>> iterator = mOnValueChangedListeners.values().iterator();
        while(iterator.hasNext()) {
            List<OnValueChangedListener> listenerList = iterator.next();
            if(listenerList.contains(eventListener))
                listenerList.remove(eventListener);
        }
    }

    public void fireValueChangedEvent(String sourceID, String value) {
        Log.v(HABApplication.getLogTag(), String.format("ValueChanged event: %s = %s", sourceID, value));
        if(!mOnValueChangedListeners.containsKey(sourceID)) {
            List<OnValueChangedListener> listenerList = mOnValueChangedListeners.get(sourceID);
            Iterator<OnValueChangedListener> iterator = listenerList.iterator();
            while(iterator.hasNext()) {
                iterator.next().onValueChanged(sourceID, value);
            }
        }
    }
}
