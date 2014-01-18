package com.zenit.habclient;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetDataSource;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetProvider {
    private Map<OpenHABWidgetType, List<OpenHABWidget>> mOpenHABWidgets;

    public OpenHABWidgetProvider() {
        mOpenHABWidgets = new HashMap<OpenHABWidgetType, List<OpenHABWidget>>();
    }

    //Long polling method..?
    public void requestWidgetUpdate() {
        //TODO - Implement this method.
    }

    public Map<OpenHABWidgetType, List<OpenHABWidget>> getOpenHABWidgets() {
        return mOpenHABWidgets;
    }

    public void setOpenHABWidgets(Map<OpenHABWidgetType, List<OpenHABWidget>> openHABWidgets) {
        mOpenHABWidgets.clear();
        mOpenHABWidgets.putAll(openHABWidgets);
    }

    public void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource) {
        mOpenHABWidgets.clear();
        addOpenHABWidget(openHABWidgetDataSource.getRootWidget());
    }

    private void addOpenHABWidget(OpenHABWidget widget) {
        if(!mOpenHABWidgets.containsKey(widget.getType()))
            mOpenHABWidgets.put(widget.getType(), new ArrayList<OpenHABWidget>());

        List<OpenHABWidget> widgetList = mOpenHABWidgets.get(widget.getType());
        widgetList.add(widget);

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
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
        if(mOpenHABWidgets.containsKey(type)) {
            resultList = mOpenHABWidgets.get(type);
        }
        return resultList;
    }
}
