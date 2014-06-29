package org.openhab.habclient.rule;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import org.openhab.habclient.HABApplication;

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
    public static List<String> getRuleOperatorList(Context context, String openHABItemName) {
        HashMap<RuleOperatorType, RuleOperator<?>> operatorHash = HABApplication.getRuleOperationProvider(context).getUnitRuleOperator(HABApplication.getOpenHABWidgetProvider2().getWidgetByItemName(openHABItemName));
        List<String> adapterList = new ArrayList<String>();
        adapterList.add(context.getString(R.string.no_value));
        for(RuleOperatorType operatorType : operatorHash.keySet())
            adapterList.add(operatorType.getName());
        return adapterList;
    }

    public static BaseAdapter getRuleOperatorAdapter(Context context, String openHABItemName) {
        return new ArrayAdapter(context, android.R.layout.simple_list_item_1, getRuleOperatorList(context, openHABItemName).toArray(new String[0]));
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
    }
}
