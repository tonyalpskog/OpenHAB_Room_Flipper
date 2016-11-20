package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.util.StringHandler;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitOperandSelectionDialogFragment extends StringSelectionDialogFragment<String> {
    private static final String ARG_POSITION = "position";

    private int mOperandIndex;
    private IRuleOperationBuildListener mListener;

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    public static UnitOperandSelectionDialogFragment newInstance(List<String> source,
                                                              String dialogTitle,
                                                              int position,
                                                              boolean showNextButton) {
        final UnitOperandSelectionDialogFragment fragment = new UnitOperandSelectionDialogFragment();

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

        ((HABApplication) getActivity().getApplication()).appComponent()
                .unitOperandSelection()
                .inject(this);

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
                    if(StringHandler.isNullOrEmpty(mSelectedItem.toString())) {
                        Toast.makeText(getActivity(), "No selection", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    final OpenHABWidget widget = mWidgetProvider.getWidgetByItemName(mSelectedItem.toString());
                    final IEntityDataType entityDataType = mUnitEntityDataTypeProvider.getUnitEntityDataType(widget);
                    final IRuleOperationBuildListener.RuleOperationDialogButtonInterface buttonInterface = which == DialogInterface.BUTTON_POSITIVE ? IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : IRuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE;
                    mListener.onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.UNIT, buttonInterface, entityDataType, mOperandIndex, null);
                    break;
                default:
                    mListener.onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.UNIT
                            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                    break;
            }
        } else throw new IllegalArgumentException("listener is null");
    }
}
