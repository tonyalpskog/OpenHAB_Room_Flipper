package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.habclient.InjectUtils;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OperatorSelectionDialogFragment extends StringSelectionDialogFragment {
    private static final String ARG_OPEN_HAB_ITEM_NAME = "openHABItemName";

    private Map<String, RuleOperator<?>> mOperatorMap;
    private String mOpenHABItemName;
    @Inject RuleOperationProvider mRuleOperationProvider;
    @Inject IOpenHABWidgetProvider mWidgetProvider;

    public static OperatorSelectionDialogFragment newInstance(String openHABItemName,
                                                              String dialogTitle,
                                                              boolean showNextButton,
                                                              List<String> ruleOperatorList) {
        final OperatorSelectionDialogFragment fragment = new OperatorSelectionDialogFragment();

        final Bundle args = new Bundle();
        args.putStringArrayList(ARG_SOURCE, new ArrayList<String>(ruleOperatorList));
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        args.putString(ARG_OPEN_HAB_ITEM_NAME, openHABItemName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);

        final Bundle args = getArguments();
        if(args == null)
            return;

        mOpenHABItemName = args.getString(ARG_OPEN_HAB_ITEM_NAME);
        mOperatorMap = getRuleOperatorMap(mOpenHABItemName);
    }

    public Map<String, RuleOperator<?>> getRuleOperatorMap(String openHABItemName) {
        HashMap<RuleOperatorType, RuleOperator<?>> operatorTypeHash = mRuleOperationProvider.getUnitRuleOperator(mWidgetProvider.getWidgetByItemName(openHABItemName));
        HashMap<String, RuleOperator<?>> operatorNameHash = new HashMap<String, RuleOperator<?>>();
        for(RuleOperatorType operatorType : operatorTypeHash.keySet())
            operatorNameHash.put(operatorType.getName(), operatorTypeHash.get(operatorType));
        return operatorNameHash;
    }

    private RuleOperandDialogFragment.RuleOperationBuildListener getListener() {
        return ((RuleEditActivity)getActivity()).getRuleOperationBuildListener();
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
