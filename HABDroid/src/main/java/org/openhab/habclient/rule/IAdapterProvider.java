package org.openhab.habclient.rule;

import android.content.Context;
import android.widget.BaseAdapter;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IAdapterProvider {

    List<String> getRuleOperatorList(Context context, IEntityDataType operand, boolean includeNonSelectionValue);

    BaseAdapter getStaticUnitValueAdapter(Context context, String openHABItemName);

    BaseAdapter getStaticUnitValueAdapter(Context context, OpenHABWidget openHABWidget);

    BaseAdapter getStaticOperationValueAdapter(Context context, boolean includeNonSelectionValue);

}
