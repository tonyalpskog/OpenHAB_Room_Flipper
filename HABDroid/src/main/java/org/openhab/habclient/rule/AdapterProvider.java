package org.openhab.habclient.rule;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tony Alpskog in 2014.
 */
public class AdapterProvider {
    public static List<String> getRuleOperatorList(Context context, IEntityDataType operand, boolean includeNonSelectionValue, IRuleOperationProvider ruleOperationProvider) {
        Set<RuleOperatorType> ruleOperatorTypes = ruleOperationProvider.getRuleOperatorTypes(operand.getDataType());
        List<String> adapterList = new ArrayList<String>();
        if(includeNonSelectionValue) adapterList.add(context.getString(R.string.no_value));
        for(RuleOperatorType operatorType : ruleOperatorTypes)
            adapterList.add(operatorType.getName());
        return adapterList;
    }

    public static BaseAdapter getStaticUnitValueAdapter(Context context, String openHABItemName, IOpenHABWidgetProvider widgetProvider) {
        OpenHABWidget openHABWidget = widgetProvider.getWidgetByItemName(openHABItemName);
        return getStaticUnitValueAdapter(context, openHABWidget);
    }

    public static BaseAdapter getStaticUnitValueAdapter(Context context, OpenHABWidget openHABWidget) {
        UnitEntityDataType unitEntityDataType = UnitEntityDataType.getUnitEntityDataType(openHABWidget);
        Map<String, ?> staticValueHash = unitEntityDataType.getStaticValues();
        if(staticValueHash == null)
            return null;
        List<String> toList = new ArrayList<String>();
        toList.add(context.getString(R.string.no_value));
        toList.addAll(staticValueHash.keySet());
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, toList);
    }

    public static BaseAdapter getStaticOperationValueAdapter(Context context, boolean includeNonSelectionValue) {
        RuleOperation staticOperation = RuleOperation.getStaticEntityDataType(null);
        Map<String, ?> staticValueHash = staticOperation.getStaticValues();
        if(staticValueHash == null)
            return null;
        List<String> toList = new ArrayList<String>();
        if(includeNonSelectionValue)
            toList.add(context.getString(R.string.no_value));
        toList.addAll(staticValueHash.keySet());
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, toList);
    }
}
