package org.openhab.habclient;

import android.util.Log;

import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.WidgetPhraseMatchResult;
import org.openhab.habclient.util.RegExAccuracyResult;
import org.openhab.habclient.util.StringHandler;

import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABItemType;
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
    private Map<String, OpenHABWidget> mOpenHABItemNameMap;
    private Map<OpenHABWidgetType, List<String>> mOpenHABWidgetTypeMap;
    private Map<OpenHABItemType, List<String>> mOpenHABItemTypeMap;
    private UUID mUpdateSetUUID;

    public OpenHABWidgetProvider() {
        mOpenHABWidgetTypeMap = new HashMap<OpenHABWidgetType, List<String>>();
        mOpenHABItemTypeMap = new HashMap<OpenHABItemType, List<String>>();
        mOpenHABWidgetIdMap = new HashMap<String, OpenHABWidget>();
        mOpenHABItemNameMap = new HashMap<String, OpenHABWidget>();
    }

    //Long polling method..?
    public void requestWidgetUpdate() {
        //TODO - Implement this method.
    }

    public Map<OpenHABWidgetType, List<OpenHABWidget>> getOpenHABWidgets() {
        Map<OpenHABWidgetType, List<OpenHABWidget>> resultingMap = new HashMap<OpenHABWidgetType, List<OpenHABWidget>>();
        for (OpenHABWidgetType type : mOpenHABWidgetTypeMap.keySet()) {
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
                if(widget.hasItem())
                    mOpenHABItemNameMap.put(widget.getItem().getName(), widget);
                stringList.add(widget.getItem().getName());
            }
            mOpenHABWidgetTypeMap.put(type, stringList);
            //TODO - TA: implement population of mOpenHABItemTypeMap.put(...)
        }
    }

    public void setOpenHABItem(OpenHABItem item) {
        mOpenHABItemNameMap.get(item.getName()).setItem(item);
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
            if(widget.hasItem())
                mOpenHABItemNameMap.put(widget.getItem().getName(), widget);

            if(widget.getType() == OpenHABWidgetType.Group || widget.getType() == OpenHABWidgetType.SitemapText) {
                String widgetName = (widget.hasLinkedPage()? widget.getLinkedPage().getTitle() : widget.getId());
                Log.d(HABApplication.getLogTag(), String.format("Setting data for group widget '%s' of type '%s'", widgetName, widget.getType().name()));
            }

            Log.d(HABApplication.getLogTag(), String.format("Setting data for widget '%s' of type '%s'", widget.getId(), widget.getType()));

            if(!widgetExists) { //Don't add existing widgets to the type<->name_list mapping.
                //Add widget to widget type mapping
                if(widget.getType() != null && !mOpenHABWidgetTypeMap.containsKey(widget.getType()))
                    mOpenHABWidgetTypeMap.put(widget.getType(), new ArrayList<String>());

                List<String> widgetList = mOpenHABWidgetTypeMap.get(widget.getType());
                widgetList.add(widget.getId());

                if(widget.hasItem()) {
                    //Add item to widget type mapping
                    if (widget.getItem().getType() != null && !mOpenHABItemTypeMap.containsKey(widget.getItem().getType()))
                        mOpenHABItemTypeMap.put(widget.getItem().getType(), new ArrayList<String>());

                    List<String> itemList = mOpenHABItemTypeMap.get(widget.getItem().getType());
                    itemList.add(widget.getItemName());
                }
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

    public List<String> getItemNamesByType(OpenHABItemType type) {
        return mOpenHABItemTypeMap.get(type);
    }

    private static final double APPROVED_UNIT_ACCURACY_VALUE = 0.75;
    private static final double DENIED_UNIT_ACCURACY_VALUE = 0.4;
    private static final double APPROVED_PARENT_ACCURACY_VALUE = 0.6;
    private static final double COMBINED_ACCURACY_FACTOR = 1.6;

    public List<WidgetPhraseMatchResult> getWidgetByLabel(String searchLabel, CommandAnalyzer commandAnalyzer) {
        String[] splittedSearchLabel = searchLabel.split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSearchLabel)
            sourceWordsList.add(sourceWord.toUpperCase());

        List<WidgetPhraseMatchResult> resultList = new ArrayList<WidgetPhraseMatchResult>();
        for(OpenHABWidget widget : mOpenHABWidgetIdMap.values().toArray(new OpenHABWidget[0])) {
            RegExAccuracyResult regExResult = HABApplication.getRegularExpression().getStringMatchAccuracy(sourceWordsList, commandAnalyzer.getPopularNameFromWidgetLabel(widget.getLabel()));
            double accuracy = regExResult.getAccuracy();
            if(accuracy < APPROVED_UNIT_ACCURACY_VALUE && accuracy > DENIED_UNIT_ACCURACY_VALUE) {
                List<String> sitemapGroupWordList = StringHandler.getStringListDiff(sourceWordsList, regExResult.getMatchingWords());
                double parentAccuracy = getHighestWidgetParentMatch(widget, sitemapGroupWordList);
                if(parentAccuracy > APPROVED_PARENT_ACCURACY_VALUE) {
                    Double combinedAccuracyPercent = Double.valueOf(((accuracy + parentAccuracy) / COMBINED_ACCURACY_FACTOR) * 100);
                    if(combinedAccuracyPercent > 100)
                        combinedAccuracyPercent = 100d;
                    resultList = addWidgetPhraseMatchResultItemToSortedList(resultList, new WidgetPhraseMatchResult(combinedAccuracyPercent.intValue(), widget));
                }
            } else if(accuracy >= APPROVED_UNIT_ACCURACY_VALUE) resultList = addWidgetPhraseMatchResultItemToSortedList(resultList, new WidgetPhraseMatchResult(Double.valueOf(accuracy * 100).intValue(), widget));
        }
        return resultList;
    }

    private List<WidgetPhraseMatchResult> addWidgetPhraseMatchResultItemToSortedList(List<WidgetPhraseMatchResult> list, WidgetPhraseMatchResult itemToAdd) {
        if(list.size() == 0)
            list.add(itemToAdd);
        else {
            boolean isAdded = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMatchPercent() < itemToAdd.getMatchPercent()) {
                    list.add(i, itemToAdd);
                    isAdded = true;
                    break;
                }
            }
            if(!isAdded)
                list.add(list.size(), itemToAdd);
        }
        return list;
    }

    public double getHighestWidgetParentMatch(OpenHABWidget unit, List<String> sourceWordsList) {
        //"Switch on kitchen ceiling lights" => "KITCHEN CEILING LIGHTS" => "KITCHEN LIGHTS"
//        OpenHABWidget resultingParentWidget = null;

        double maxResult = 0;
        while(unit.hasParent()) {
            unit = unit.getParent();
            if(!unit.hasLinkedPage())
                continue;
            String linkTitle = unit.getLinkedPage().getTitle();
//            double result = mCommandAnalyzer.getStringMatchAccuracy(sourceWordsList, regExString, linkTitle);
            double result = HABApplication.getRegularExpression().getStringMatchAccuracy(sourceWordsList, linkTitle).getAccuracy();
            if (result > maxResult) {
                maxResult = result;
//                resultingParentWidget = unit;
            }
        }

        return maxResult;
    }

    public OpenHABWidget getWidgetByID(String widgetID) {
        OpenHABWidget widget = mOpenHABWidgetIdMap.get(widgetID);
        if(widget == null)
            Log.w(HABApplication.getLogTag(), String.format("Widget ID '%s' doesn't exist i current widget mapping", widgetID));

        return widget;
    }

    public boolean hasWidgetID(String widgetId) {
        if(widgetId == null)
            return false;

        boolean result = mOpenHABWidgetIdMap.containsKey(widgetId);
        if(!result)
            Log.w(HABApplication.getLogTag(), String.format("Widget ID '%s' doesn't exist i current widget mapping", widgetId));
        return result;
    }

    public OpenHABWidget getWidgetByItemName(String openHabItemName) {
        OpenHABWidget widget = mOpenHABItemNameMap.get(openHabItemName);
        if(widget == null)
            Log.w(HABApplication.getLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", openHabItemName));
        return widget;
    }

    public boolean hasItemName(String openHabItemName) {
        if(openHabItemName == null)
            return false;

        boolean result = mOpenHABItemNameMap.containsKey(openHabItemName);
        if(!result)
            Log.w(HABApplication.getLogTag(), String.format("Item name '%s' doesn't exist i current widget mapping", openHabItemName));
        return result;
    }

    public List<String> getItemNameList() {
        List<String> list = new ArrayList<String>();
        list.addAll(mOpenHABItemNameMap.keySet());
        return list;
    }

    public List<String> getItemNameListByWidgetType(Set<OpenHABWidgetType> widgetTypes) {
        List<String> itemNameList = new ArrayList<String>();
        List<OpenHABWidget> widgetList = getWidgetList(widgetTypes);
        for(OpenHABWidget widget : widgetList) {
            if(widget.hasItem())
                itemNameList.add(widget.getItemName());
        }
        return itemNameList;
    }
}
