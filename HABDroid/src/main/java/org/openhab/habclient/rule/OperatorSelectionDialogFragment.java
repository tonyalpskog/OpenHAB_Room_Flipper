package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.IRuleOperatorProvider;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.dagger.DaggerOperatorSelectionComponent;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OperatorSelectionDialogFragment extends StringSelectionDialogFragment<String> {
    private static final String ARG_OPEN_HAB_ITEM_NAME = "openHABItemName";
    private static final String ARG_SOURCE_TYPE = "EntityDataTypeSource";

    private Map<String, RuleOperator<?>> mOperatorMap;
    private String mOpenHABItemName;
    @Inject IRuleOperatorProvider mRuleOperatorProvider;
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    public static OperatorSelectionDialogFragment newInstance(String openHABItemName,
                                                              EntityDataTypeSource sourceType,
                                                              String dialogTitle,
                                                              boolean showNextButton,
                                                              List<String> ruleOperatorList) {
        final OperatorSelectionDialogFragment fragment = new OperatorSelectionDialogFragment();

        final Bundle args = new Bundle();
        args.putString(ARG_SOURCE_TYPE, sourceType.name());
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        args.putString(ARG_OPEN_HAB_ITEM_NAME, openHABItemName);
        fragment.setArguments(args);
        fragment.setSourceList(ruleOperatorList);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerOperatorSelectionComponent.builder()
                .appComponent(((HABApplication) getActivity().getApplication()).appComponent())
                .build()
                .inject(this);

        final Bundle args = getArguments();
        if(args == null)
            return;

        mOpenHABItemName = args.getString(ARG_OPEN_HAB_ITEM_NAME);
        EntityDataTypeSource sourceType = EntityDataTypeSource.valueOf(args.getString(ARG_SOURCE_TYPE));
        mOperatorMap = getRuleOperatorMap(sourceType == EntityDataTypeSource.OPERATION? new RuleOperation().getDataType() : mUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByItemName(mOpenHABItemName)).getDataType());
    }

    public Map<String, RuleOperator<?>> getRuleOperatorMap(Class<?> operandClassType) {
        Set<RuleOperatorType> ruleOperatorTypes = mRuleOperatorProvider.getRuleOperatorTypes(operandClassType);
        HashMap<String, RuleOperator<?>> operatorNameHash = new HashMap<String, RuleOperator<?>>();
        for(RuleOperatorType operatorType : ruleOperatorTypes)
            operatorNameHash.put(operatorType.getName(), mRuleOperatorProvider.getRuleOperator(operandClassType, operatorType));
        return operatorNameHash;
    }

    private IRuleOperationBuildListener getListener() {
        return ((RuleEditActivity)getActivity()).getRuleOperationBuildListener();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEUTRAL:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.OPERATOR
                            , which == DialogInterface.BUTTON_POSITIVE? IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : IRuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
                            , null, 0, mOperatorMap.get(mSelectedItem));
                }
                break;
            default:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.OPERATOR
                            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                }
                break;
        }
    }
}
