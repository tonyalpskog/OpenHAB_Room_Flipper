package com.zenit.habclient.rule;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class AdapterProvider {
    public static SimpleAdapter getRuleOperatorAdapter(Context context, String openHABItemName) {
        HashMap<RuleOperatorType, RuleOperator<?>> operatorHash = HABApplication.getRuleOperationProvider(context).getUnitRuleOperator(HABApplication.getOpenHABWidgetProvider2().getWidgetByItemName(openHABItemName));
        List<Map<String, RuleOperator<?>>> adapterList = new ArrayList<Map<String, RuleOperator<?>>>();
        String[] fromArray = new String[operatorHash.size()];
        int i = 0;
        for(RuleOperatorType operatorType : operatorHash.keySet()) {
            HashMap<String, RuleOperator<?>> adapterHash = new HashMap<String, RuleOperator<?>>(1);
            adapterHash.put(operatorType.getName(), operatorHash.get(operatorType));
            adapterList.add(adapterHash);
            fromArray[i++] = operatorType.getName();
        }
        return new SimpleAdapter(context, adapterList, android.R.layout.simple_list_item_1, fromArray, new int[]{android.R.id.text1});
    }

    public static BaseAdapter getStaticUnitValueAdapter(Context context, String openHABItemName) {
        OpenHABWidget openHABWidget = HABApplication.getOpenHABWidgetProvider2().getWidgetByItemName(openHABItemName);
        UnitEntityDataType unitEntityDataType = UnitEntityDataType.getUnitEntityDataType(openHABWidget);
        Map<String, ?> staticValueHash = unitEntityDataType.getStaticValues();
        if(staticValueHash == null)
            return null;
        List<String> toList = new ArrayList<String>();
        toList.add(context.getString(R.string.no_value));
        toList.addAll(staticValueHash.keySet());
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, toList.toArray(new String[0]));
//        List<Map<String, ?>> adapterList = new ArrayList<Map<String, ?>>(staticValueHash.size());
//        String[] fromArray = new String[staticValueHash.size()];
//        int i = 0;
//        for(String key : staticValueHash.keySet()) {
//            adapterList.add(staticValueHash);
//            fromArray[i++] = key;
//        }
//        return new SimpleAdapter(context, adapterList, android.R.layout.simple_list_item_1, fromArray, new int[]{android.R.id.text1});
    }
}
