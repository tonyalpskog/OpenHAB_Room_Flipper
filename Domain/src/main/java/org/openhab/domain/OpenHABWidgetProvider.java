package org.openhab.domain;

import org.openhab.domain.command.WidgetPhraseMatchResult;
import org.openhab.domain.model.GraphicUnit;
import org.openhab.domain.model.OpenHABItem;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.model.OpenHABWidgetEvent;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.domain.model.Room;
import org.openhab.domain.model.SitemapUpdateEvent;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.RegExAccuracyResult;
import org.openhab.domain.util.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class OpenHABWidgetProvider implements IOpenHABWidgetProvider {
    private static final String TAG = "OpenHABWidgetProvider";
    private final IRegularExpression mRegularExpression;
    private final ILogger mLogger;
    private final IEventBus mEventBus;
    private Map<String, OpenHABWidget> mOpenHABWidgetIdMap;
    private Map<String, OpenHABWidget> mOpenHABItemNameMap;
    private Map<OpenHABWidgetType, List<String>> mOpenHABWidgetTypeMap;
    private Map<OpenHABItemType, List<String>> mOpenHABItemTypeMap;
    private UUID mUpdateSetUUID;
    private IPopularNameProvider mPopularNameProvider;
    private Map<String, List<UnitEntityDataType>> mOpenHABItemListenerMap;
    private List<UnitEntityDataType> mRecalculationListenerList;

    @Inject
    public OpenHABWidgetProvider(IRegularExpression regularExpression,
                                 ILogger logger,
                                 IPopularNameProvider popularNameProvider,
                                 IEventBus eventBus) {
        if(regularExpression == null) throw new IllegalArgumentException("regularExpression is null");
        if(logger == null) throw new IllegalArgumentException("logger is null");
        if(eventBus == null) throw new IllegalArgumentException("eventBus is null");

        mEventBus = eventBus;
        mRegularExpression = regularExpression;
        mLogger = logger;
        mPopularNameProvider = popularNameProvider;
        mOpenHABWidgetTypeMap = new HashMap<OpenHABWidgetType, List<String>>();
        mOpenHABItemTypeMap = new HashMap<OpenHABItemType, List<String>>();
        mOpenHABWidgetIdMap = new HashMap<String, OpenHABWidget>();
        mOpenHABItemNameMap = new HashMap<String, OpenHABWidget>();
        mOpenHABItemListenerMap = new HashMap<String, List<UnitEntityDataType>>();

        mRecalculationListenerList = new ArrayList<UnitEntityDataType>();
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

        for (OpenHABWidgetType type : openHABWidgets.keySet()) {
            List<OpenHABWidget> widgetList = openHABWidgets.get(type);
            List<String> stringList = new ArrayList<String>(widgetList.size());
            for (OpenHABWidget widget : widgetList) {
                mOpenHABWidgetIdMap.put(widget.getId(), widget);
                if (widget.hasItem())
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

    @Override
    public void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource) {
        if(openHABWidgetDataSource.getRootWidget() == null)
            return;

        clearData();
        addOpenHABWidget(openHABWidgetDataSource.getRootWidget(), true);
    }

    private void clearData() {
//        mOpenHABWidgetIdMap.clear();
//        mOpenHABWidgetTypeMap.clear();
//
        mUpdateSetUUID = UUID.randomUUID();
    }

    @Override
    public UUID getUpdateUUID() {
        return mUpdateSetUUID;
    }

    private void addOpenHABWidget(OpenHABWidget widget, boolean isStartOfBatch) {
        if(widget == null)
            return;

        if(widget.getType() == null)
            mLogger.d(TAG, String.format("[SITEMAP] typeless widget found: label = '%s'  link = '%s'", widget.getLabel(), widget.getLinkedPage()));

        if(widget.getId() != null && widget.getId().equalsIgnoreCase("0001_3"))
            mLogger.d(TAG, String.format("[SITEMAP] widget '%s' was found. Label = '%s'", widget.getId(), widget.getLabel()));

        widget.setUpdateUUID(mUpdateSetUUID);

        boolean widgetExists = mOpenHABWidgetIdMap.containsKey(widget.getId());
        if(widgetExists && widget.hasItem()) {
            mEventBus.postSticky(new OpenHABWidgetEvent(widget));
            updateListeners(widget.getItem());
        }

        if(!widgetExists && widget.getType() != null) {
            //Overwrite / Update widget if it already exists.
            mOpenHABWidgetIdMap.put(widget.getId(), widget);
            if(widget.hasItem())
                mOpenHABItemNameMap.put(widget.getItem().getName(), widget);

            if(widget.getType() == OpenHABWidgetType.Group || widget.getType() == OpenHABWidgetType.SitemapText) {
                String widgetName = (widget.hasLinkedPage()? widget.getLinkedPage().getTitle() : widget.getId());
                mLogger.d(TAG, String.format("Setting data for group widget '%s' of type '%s'", widgetName, widget.getType().name()));
            }

            mLogger.d(TAG, String.format("Setting data for widget '%s' of type '%s'", widget.getId(), widget.getType()));

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
                    if(itemList == null)
                        mLogger.e("OpenHABWidgetProvider", String.format("Cannot find openHABWidget item type. Item name = '%s'. Widget ID = %s. Item was not added in mOpenHABItemTypeMap.", widget.getItem().getName(), widget.getId()));
                    else
                        itemList.add(widget.getItemName());
                }
            }
        }

        if(widget.hasChildren()) {
            for (OpenHABWidget widget1 : widget.getChildren()) {
                addOpenHABWidget(widget1, false);//TODO - Don't add as parent if parent type = null
            }
        }

        if(isStartOfBatch) {
            mEventBus.post(new SitemapUpdateEvent(true));
            recalculateUnits();
        }
    }

    private void recalculateUnits() {
        for(UnitEntityDataType unit : mRecalculationListenerList)
            unit.resumeOnValueChangedEvent();
    }

    public Map<OpenHABWidgetType, List<OpenHABWidget>> getWidgetMap(Set<OpenHABWidgetType> category) {
        Map<OpenHABWidgetType, List<OpenHABWidget>> resultMap = new HashMap<OpenHABWidgetType, List<OpenHABWidget>>();
        for (OpenHABWidgetType key : category) {
            resultMap.put(key, getWidgetList(key));
        }
        return resultMap;
    }

    @Override
    public List<OpenHABWidget> getWidgetList(Set<OpenHABWidgetType> category) {
        ArrayList<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();

        if(category == null) {
            resultList.addAll(mOpenHABWidgetIdMap.values());
            return resultList;
        }

        for (OpenHABWidgetType aCategory : category) {
            resultList.addAll(getWidgetList(aCategory));
        }
        return resultList;
    }

    @Override
    public List<OpenHABWidget> getWidgetList(OpenHABWidgetType type) {
        List<String> idList;
        List<OpenHABWidget> resultList = new ArrayList<OpenHABWidget>();
        if(mOpenHABWidgetTypeMap.containsKey(type)) {
            idList = mOpenHABWidgetTypeMap.get(type);
            for (String anIdList : idList) {
                resultList.add(mOpenHABWidgetIdMap.get(anIdList));
            }
        }
        return resultList;
    }

    @Override
    public List<String> getItemNamesByType(OpenHABItemType type) {
        return mOpenHABItemTypeMap.get(type);
    }

    private static final double APPROVED_UNIT_ACCURACY_VALUE = 0.75;
    private static final double DENIED_UNIT_ACCURACY_VALUE = 0.4;
    private static final double APPROVED_PARENT_ACCURACY_VALUE = 0.6;
    private static final double COMBINED_ACCURACY_FACTOR = 1.6;

    @Override
    public List<WidgetPhraseMatchResult> getWidgetByLabel(String searchLabel) {
        String[] splittedSearchLabel = searchLabel.split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSearchLabel)
            sourceWordsList.add(sourceWord.toUpperCase());

        List<WidgetPhraseMatchResult> resultList = new ArrayList<WidgetPhraseMatchResult>();
        for(OpenHABWidget widget : mOpenHABWidgetIdMap.values().toArray(new OpenHABWidget[0])) {
            RegExAccuracyResult regExResult = mRegularExpression.getStringMatchAccuracy(sourceWordsList, mPopularNameProvider.getPopularNameFromWidgetLabel(widget.getLabel()));
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
        double maxResult = 0;
        while(unit.hasParent()) {
            unit = unit.getParent();
            if(!unit.hasLinkedPage())
                continue;
            String linkTitle = unit.getLinkedPage().getTitle();
            double result = mRegularExpression.getStringMatchAccuracy(sourceWordsList, linkTitle).getAccuracy();
            if (result > maxResult) {
                maxResult = result;
            }
        }

        return maxResult;
    }

    @Override
    public OpenHABWidget getWidgetByID(String widgetID) {
        OpenHABWidget widget = mOpenHABWidgetIdMap.get(widgetID);
        if(widget == null)
            mLogger.w(TAG, String.format("Widget ID '%s' doesn't exist i current widget mapping", widgetID));

        return widget;
    }

    public boolean hasWidgetID(String widgetId) {
        if(widgetId == null)
            return false;

        boolean result = mOpenHABWidgetIdMap.containsKey(widgetId);
        if(!result)
            mLogger.w(TAG, String.format("Widget ID '%s' doesn't exist i current widget mapping", widgetId));
        return result;
    }

    @Override
    public OpenHABWidget getWidgetByItemName(String openHabItemName) {
        OpenHABWidget widget = mOpenHABItemNameMap.get(openHabItemName);
        if(widget == null)
            mLogger.w(TAG, String.format("Item name '%s' doesn't exist i current widget mapping", openHabItemName));
        return widget;
    }

    public boolean hasItemName(String openHabItemName) {
        if(openHabItemName == null)
            return false;

        boolean result = mOpenHABItemNameMap.containsKey(openHabItemName);
        if(!result)
            mLogger.w(TAG, String.format("Item name '%s' doesn't exist i current widget mapping", openHabItemName));
        return result;
    }

    public List<String> getItemNameList() {
        List<String> list = new ArrayList<String>();
        list.addAll(mOpenHABItemNameMap.keySet());
        return list;
    }

    @Override
    public List<String> getItemNameListByWidgetType(Set<OpenHABWidgetType> widgetTypes) {
        List<String> itemNameList = new ArrayList<String>();
        List<OpenHABWidget> widgetList = getWidgetList(widgetTypes);
        for(OpenHABWidget widget : widgetList) {
            if(widget.hasItem())
                itemNameList.add(widget.getItemName());
        }
        return itemNameList;
    }

    @Override
    public List<OpenHABWidget> getListOfWidgetsFromListOfRooms(List<Room> listOfRooms) {
        //Get widgets from room list
        if(listOfRooms != null && !listOfRooms.isEmpty()){
            final List<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();
            for (Room nextRoom : listOfRooms) {
                for(GraphicUnit gu : nextRoom.getUnits())
                    widgetList.add(gu.getOpenHABWidget());
            }
            return widgetList;
        }

        //Get all unit widgets
        return getWidgetList(OpenHABWidgetTypeSet.UnitItem);
    }

    @Override
    public void addItemListener(UnitEntityDataType listener) {
        if(!mOpenHABItemListenerMap.containsKey(listener.getDataSourceId()))
            mOpenHABItemListenerMap.put(listener.getDataSourceId(), new ArrayList<UnitEntityDataType>());
        if(!mOpenHABItemListenerMap.get(listener.getDataSourceId()).contains(listener))
            mOpenHABItemListenerMap.get(listener.getDataSourceId()).add(listener);
    }

    @Override
    public void removeItemListener(UnitEntityDataType listener) {
        if(!mOpenHABItemListenerMap.containsKey(listener.getDataSourceId()))
            return;

        List<UnitEntityDataType> listenerList = mOpenHABItemListenerMap.get(listener.getDataSourceId());
        if(listenerList.contains(listener))
            listenerList.remove(listener);
    }

    private void updateListeners(OpenHABItem item) {
        if(mOpenHABItemListenerMap.containsKey(item.getName()))
            for(UnitEntityDataType listener : mOpenHABItemListenerMap.get(item.getName()))
                if(!listener.valueOf(item.getState()).equals(listener.getFormattedString()/*getValue()*/)) {
                    listener.setValue(listener.valueOf(item.getState()), false);
                    mRecalculationListenerList.add(listener);
                }
    }
}
