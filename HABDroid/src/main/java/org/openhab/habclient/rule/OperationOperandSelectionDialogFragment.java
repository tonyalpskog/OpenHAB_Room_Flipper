package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.Rule;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.List;

/**
 * Created by Tony Alpskog in 2015.
 */
public class OperationOperandSelectionDialogFragment<T> extends StringSelectionDialogFragment<T> {
    private static final String ARG_POSITION = "position";

    private int mOperandIndex;
    private IRuleOperationBuildListener mListener;

    public static <T>OperationOperandSelectionDialogFragment newInstance(List<T> source,
                                                              String dialogTitle,
                                                              int position,
                                                              boolean showNextButton) {
        final OperationOperandSelectionDialogFragment fragment = new OperationOperandSelectionDialogFragment<T>();

        final Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        fragment.setSourceList(source);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args == null)
            return;

        mOperandIndex = args.getInt(ARG_POSITION);

        mListener = ((RuleEditActivity)getActivity()).getRuleOperationBuildListener();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    if(mSelectedItem == null) {
                        Toast.makeText(getActivity(), "No selection", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    final Rule rule = (Rule) mSelectedItem;
                    final IEntityDataType entityDataType = rule.getRuleOperation();
                    final IRuleOperationBuildListener.RuleOperationDialogButtonInterface buttonInterface = which == DialogInterface.BUTTON_POSITIVE ? IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : IRuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE;
                    mListener.onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.EXISTING_OPERATION, buttonInterface, entityDataType, mOperandIndex, null);
                    break;
                default:
                    mListener.onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.EXISTING_OPERATION
                            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                    break;
            }
        } else throw new IllegalArgumentException("listener is null");
    }
}
