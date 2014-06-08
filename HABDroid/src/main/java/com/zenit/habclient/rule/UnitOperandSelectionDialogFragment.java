package com.zenit.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;

import com.zenit.habclient.util.StringSelectionDialogFragment;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitOperandSelectionDialogFragment extends StringSelectionDialogFragment {
    int mOperandIndex;

    public UnitOperandSelectionDialogFragment(List<String> source, String dialogTitle, int position) {
        super(source, dialogTitle);
        mOperandIndex = position;
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
                if(getListener() != null) {
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT
                            , RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
                            , UnitEntityDataType.getUnitEntityDataTypeByItemName(mSelectedString), mOperandIndex, null);
                }
                break;
            default:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT
                            , RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                }
                break;
        }
    }
}
