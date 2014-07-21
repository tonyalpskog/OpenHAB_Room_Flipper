package org.openhab.habclient.rule;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class AdapterProvider implements IAdapterProvider {
    @Inject IRuleOperationProvider mRuleOperationProvider;
    @Inject IOpenHABWidgetProvider mOpenHABWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    @Inject
    public AdapterProvider() {
    }

    @Override
    public List<String> getRuleOperatorList(Context context, IEntityDataType operand, boolean includeNonSelectionValue) {
        List<String> adapterList = new ArrayList<String>();
        if(includeNonSelectionValue) adapterList.add(context.getString(R.string.no_value));

        return adapterList;
    }

    @Override
    public BaseAdapter getStaticUnitValueAdapter(Context context, String openHABItemName) {
        OpenHABWidget openHABWidget = mOpenHABWidgetProvider.getWidgetByItemName(openHABItemName);
        return getStaticUnitValueAdapter(context, openHABWidget);
    }

    @Override
    public BaseAdapter getStaticUnitValueAdapter(Context context, OpenHABWidget openHABWidget) {
        List<String> toList = new ArrayList<String>();
        toList.add(context.getString(R.string.no_value));
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, toList);
    }

    @Override
    public BaseAdapter getStaticOperationValueAdapter(Context context, boolean includeNonSelectionValue) {
        List<String> toList = new ArrayList<String>();
        if(includeNonSelectionValue)
            toList.add(context.getString(R.string.no_value));
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, toList);
    }
}
