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
        this(source, dialogTitle, position, false);
    }

    public UnitOperandSelectionDialogFragment(List<String> source, String dialogTitle, int position, boolean showNextButton) {
        super(source, dialogTitle, showNextButton);
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
            case DialogInterface.BUTTON_NEUTRAL:
                if(getListener() != null) {
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT
                            , which == DialogInterface.BUTTON_POSITIVE? RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
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
