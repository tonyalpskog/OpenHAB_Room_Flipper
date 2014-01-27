package com.zenit.habclient;

import android.util.Log;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetDataSource;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetProvider {
    private Map<String, OpenHABWidget> mOpenHABWidgetItemNameMap;
    private Map<OpenHABWidgetType, List<String>> mOpenHABWidgetTypeMap;
    private UUID mUpdateSetUUID;

    public OpenHABWidgetProvider() {
        mOpenHABWidgetTypeMap = new HashMap<OpenHABWidgetType, List<String>>();
        mOpenHABWidgetItemNameMap = new HashMap<String, OpenHABWidget>();
    }

    //Long polling method..?
    public void requestWidgetUpdate() {
        //TODO - Implement this method.
    }

    public Map<OpenHABWidgetType, List<OpenHABWidget>> getOpenHABWidgets() {
        Map<OpenHABWidgetType, List<OpenHABWidget>> resultingMap = new HashMap<OpenHABWidgetType, List<OpenHABWidget>>();
        Iterator<OpenHABWidgetType> iterator = mOpenHABWidgetTypeMap.keySet().iterator();
        while (iterator.hasNext()) {
            OpenHABWidgetType type = iterator.next();
            resultingMap.put(type, getWidgetList(type));
        }
        return resultingMap;
    }

    public void setOpenHABWidgets(Map<OpenHABWidgetType, List<OpenHABWidget>> openHABWidgets) {
        clearData();

        Iterator<OpenHABWidgetType> typeKeyIterator  = openHABWidgets.keySet().iterator();
        while (typeKeyIterator.hasNext()) {
            OpenHABWidgetType type = typeKeyIterator.next();
            List<OpenHABWidget> widgetList = openHABWidgets.get(type);
            List<String> stringList = new ArrayList<String>(widgetList.size());
            Iterator<OpenHABWidget> widgetIterator = widgetList.iterator();
            while (widgetIterator.hasNext()) {
                OpenHABWidget widget = widgetIterator.next();
                mOpenHABWidgetItemNameMap.put(widget.getItem().getName(), widget);
                stringList.add(widget.getItem().getName());
            }
            mOpenHABWidgetTypeMap.put(type, stringList);
        }
    }

    public void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource) {
        clearData();
        addOpenHABWidget(openHABWidgetDataSource.getRootWidget());
    }

    private void clearData() {
//        mOpenHABWidgetItemNameMap.clear();
//        mOpenHABWidgetTypeMap.clear();
//
        mUpdateSetUUID = UUID.randomUUID();
    }

    public UUID getUpdateUUID() {
        return mUpdateSetUUID;
    }

    private void addOpenHABWidget(OpenHABWidget widget) {
        widget.setUpdateUUID(mUpdateSetUUID);

        if(widget.getItem() == null)
        {
            Log.w(HABApplication.GetLogTag(), String.format("Widget ID '%s' of type '%s' doesn't have an item instance and cannot be added to the Widget Provider.", widget.getId(), widget.getType()));
        } else {
            mOpenHABWidgetItemNameMap.put(widget.getItem().getName(), widget);
            Log.d(HABApplication.GetLogTag(), String.format("Setting data for widget '%s' of type '%s'", widget.getItem().getName(), widget.getType()));

            if(!mOpenHABWidgetTypeMap.containsKey(widget.getType()))
                mOpenHABWidgetTypeMap.put(widget.getType(), new ArrayList<String>());

            List<String> widgetList = mOpenHABWidgetTypeMap.get(widget.getType());
            widgetList.add(widget.getItem().getName());
        }

        if(widget.hasChildren()) {
            Iterator<OpenHABWidget> iterator = widget.getChildren().iterator();
            while(iterator.hasNext()) {
                addOpenHABWidget(iterator.next());
            }
        }
    }

    public Map<OpenHABWidgetType, List<OpenHABWidget>> getWidgetMap(Set<OpenHABWidgetType> category) {
        Map<OpenHABWidgetType, List<OpenHABWidget>> resultMap = new HashMap<OpenHABWidgetType, List<OpenHABWidget>>();
        Iterator<OpenHABWidgetType> iterator = category.iterator();
        while(iterator.hasNext()) {
            OpenHABWidgetType key = iterator.next();
            resultMap.put(key, getWidgetList(key));
        }
        return resultMap;
    }

    public List<OpenHABWidget> getWidgetList(Set<OpenHABWidgetType> category) {
        ArrayList<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        if(category == null) {
            resultList.addAll(mOpenHABWidgetItemNameMap.values());
            return resultList;
        }

        Iterator<OpenHABWidgetType> iterator = category.iterator();
        while(iterator.hasNext()) {
            resultList.addAll(getWidgetList(iterator.next()));
        }
        return resultList;
    }

    public List<OpenHABWidget> getWidgetList(OpenHABWidgetType type) {
        List<String> idList = new ArrayList<String>();
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
        if(mOpenHABWidgetTypeMap.containsKey(type)) {
            idList = mOpenHABWidgetTypeMap.get(type);
            Iterator<String> idIterator = idList.iterator();
            while(idIterator.hasNext()) {
                resultList.add(mOpenHABWidgetItemNameMap.get(idIterator.next()));
            }
        }
        return resultList;
    }

    public OpenHABWidget getWidget(String itemName) {
        if(!hasWidget(itemName))
            Log.w(HABApplication.GetLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", itemName));

        return mOpenHABWidgetItemNameMap.get(itemName);
    }

    public boolean hasWidget(String itemName) {
        if(itemName == null)
            return false;

        boolean result = mOpenHABWidgetItemNameMap.containsKey(itemName);
        if(!result)
            Log.w(HABApplication.GetLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", itemName));
        return result;
    }
}
