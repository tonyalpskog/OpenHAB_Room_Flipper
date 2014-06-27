package com.zenit.habclient.rule;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.util.StringSelectionDialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OperatorSelectionDialogFragment extends StringSelectionDialogFragment {
    Map<String, RuleOperator<?>> mOperatorMap;

    public OperatorSelectionDialogFragment(Context context, String openHABItemName, String dialogTitle) {
        this(context, openHABItemName, dialogTitle, false);
    }

    public OperatorSelectionDialogFragment(Context context, String openHABItemName, String dialogTitle, boolean showNextButton) {
        super(AdapterProvider.getRuleOperatorList(context, openHABItemName), dialogTitle,showNextButton);
        mOperatorMap = getRuleOperatorMap(context, openHABItemName);
    }

    public Map<String, RuleOperator<?>> getRuleOperatorMap(Context context, String openHABItemName) {
        HashMap<RuleOperatorType, RuleOperator<?>> operatorTypeHash = HABApplication.getRuleOperationProvider(context).getUnitRuleOperator(HABApplication.getOpenHABWidgetProvider2().getWidgetByItemName(openHABItemName));
        HashMap<String, RuleOperator<?>> operatorNameHash = new HashMap<String, RuleOperator<?>>();
        for(RuleOperatorType operatorType : operatorTypeHash.keySet())
            operatorNameHash.put(operatorType.getName(), operatorTypeHash.get(operatorType));
        return operatorNameHash;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private RuleOperandDialogFragment.RuleOperationBuildListener getListener() {
        return (RuleOperandDialogFragment.RuleOperationBuildListener) getActivity();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEUTRAL:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.OPERATOR
                            , which == DialogInterface.BUTTON_POSITIVE? RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
                            , null, 0, mOperatorMap.get(mSelectedString));
                }
                break;
            default:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.OPERATOR
                            , RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                }
                break;
        }
    }
}
