package com.zenit.habclient;

import android.util.Log;

import com.zenit.habclient.command.WidgetPhraseMatchResult;

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
                stringList.add(widget.getItem().getName());
            }
            mOpenHABWidgetTypeMap.put(type, stringList);
        }
    }

    public void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource) {
        if(openHABWidgetDataSource.getRootWidget() == null)
            return;

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
        if(widget == null)
            return;

        widget.setUpdateUUID(mUpdateSetUUID);

        boolean widgetExists = mOpenHABWidgetIdMap.containsKey(widget.getId());

        if(widget.getType() != null) {
            //Overwrite / Update widget if it already exists.
            mOpenHABWidgetIdMap.put(widget.getId(), widget);

            if(widget.getType() == OpenHABWidgetType.Group || widget.getType() == OpenHABWidgetType.SitemapText) {
                String widgetName = (widget.hasLinkedPage()? widget.getLinkedPage().getTitle() : widget.getId());
                Log.d(HABApplication.getLogTag(), String.format("Setting data for group widget '%s' of type '%s'", widgetName, widget.getType().name()));
            }

            Log.d(HABApplication.getLogTag(), String.format("Setting data for widget '%s' of type '%s'", widget.getId(), widget.getType()));

            if(!widgetExists) { //Don't add existing widgets to the type<->name_list mapping.
                if(widget.getType() != null && !mOpenHABWidgetTypeMap.containsKey(widget.getType()))
                    mOpenHABWidgetTypeMap.put(widget.getType(), new ArrayList<String>());

                List<String> widgetList = mOpenHABWidgetTypeMap.get(widget.getType());
                widgetList.add(widget.getId());
            }
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
            resultList.addAll(mOpenHABWidgetIdMap.values());
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
                resultList.add(mOpenHABWidgetIdMap.get(idIterator.next()));
            }
        }
        return resultList;
    }

    public List<WidgetPhraseMatchResult> getWidgetByLabel(String searchLabel, CommandAnalyzer commandAnalyzer) {
        String[] splittedSearchLabel = searchLabel.split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSearchLabel)
            sourceWordsList.add(sourceWord.toUpperCase());

        String regExString = commandAnalyzer.getRegExStringForMatchAccuracySource(splittedSearchLabel);

        List<WidgetPhraseMatchResult> resultList = new ArrayList<WidgetPhraseMatchResult>();
        for(OpenHABWidget widget : mOpenHABWidgetIdMap.values().toArray(new OpenHABWidget[0])) {
            double accuracy = commandAnalyzer.getStringMatchAccuracy(searchLabel, sourceWordsList, regExString, commandAnalyzer.getPopularNameFromWidgetLabel(widget.getLabel()));
            if(accuracy > 0.5)
                resultList.add(new WidgetPhraseMatchResult(Double.valueOf(accuracy * 100).intValue(), widget));
        }
        return resultList;
    }

    public OpenHABWidget getWidgetByID(String widgetID) {
        if(!hasWidgetID(widgetID))
            Log.w(HABApplication.getLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", widgetID));

        return mOpenHABWidgetIdMap.get(widgetID);
    }

    public boolean hasWidgetID(String itemName) {
        if(itemName == null)
            return false;

        boolean result = mOpenHABWidgetIdMap.containsKey(itemName);
        if(!result)
            Log.w(HABApplication.getLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", itemName));
        return result;
    }
}
