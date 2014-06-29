package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;

import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitOperandSelectionDialogFragment extends StringSelectionDialogFragment {
    int mOperandIndex;
    RuleOperandDialogFragment.RuleOperationBuildListener mListener;

    public UnitOperandSelectionDialogFragment(List<String> source, String dialogTitle, int position, RuleOperandDialogFragment.RuleOperationBuildListener listener) {
        this(source, dialogTitle, position, false, listener);
    }

    public UnitOperandSelectionDialogFragment(List<String> source, String dialogTitle, int position, boolean showNextButton, RuleOperandDialogFragment.RuleOperationBuildListener listener) {
        super(source, dialogTitle, showNextButton);
        mOperandIndex = position;
        mListener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private RuleOperandDialogFragment.RuleOperationBuildListener getListener() {
        return mListener;
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
