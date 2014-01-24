package com.zenit.habclient;

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
    private Map<String, OpenHABWidget> mOpenHABWidgetIdMap;
    private Map<OpenHABWidgetType, List<String>> mOpenHABWidgetTypeMap;
    private UUID mUpdateSetUUID;

    public OpenHABWidgetProvider() {
        mOpenHABWidgetTypeMap = new HashMap<OpenHABWidgetType, List<String>>();
        mOpenHABWidgetIdMap = new HashMap<String, OpenHABWidget>();
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
                mOpenHABWidgetIdMap.put(widget.getId(), widget);
                stringList.add(widget.getId());
            }
            mOpenHABWidgetTypeMap.put(type, stringList);
        }
    }

    public void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource) {
        clearData();
        addOpenHABWidget(openHABWidgetDataSource.getRootWidget());
    }

    private void clearData() {
//        mOpenHABWidgetIdMap.clear();
//        mOpenHABWidgetTypeMap.clear();
//
        mUpdateSetUUID = UUID.randomUUID();
    }

    public UUID getUpdateUUID() {
        return mUpdateSetUUID;
    }

    private void addOpenHABWidget(OpenHABWidget widget) {
        widget.setUpdateUUID(mUpdateSetUUID);

        mOpenHABWidgetIdMap.put(widget.getId(), widget);

        if(!mOpenHABWidgetTypeMap.containsKey(widget.getType()))
            mOpenHABWidgetTypeMap.put(widget.getType(), new ArrayList<String>());

        List<String> widgetList = mOpenHABWidgetTypeMap.get(widget.getType());
        widgetList.add(widget.getId());

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
                resultList.add(mOpenHABWidgetIdMap.get(idIterator.next()));
            }
        }
        return resultList;
    }

    public OpenHABWidget getWidget(String widgetId) {
        return mOpenHABWidgetIdMap.get(widgetId);
    }

    public boolean hasWidget(String widgetId) {
        return mOpenHABWidgetIdMap.containsKey(widgetId);
    }
}
