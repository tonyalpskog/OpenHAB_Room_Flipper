package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.UnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.util.StringHandler;
import org.openhab.habclient.InjectUtils;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitOperandSelectionDialogFragment extends StringSelectionDialogFragment {
    private static final String ARG_POSITION = "position";

    private int mOperandIndex;
    private UnitEntityDataTypeProvider.RuleOperationBuildListener mListener;

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject
    IUnitEntityDataTypeProvider mIUnitEntityDataTypeProvider;

    public static UnitOperandSelectionDialogFragment newInstance(List<String> source,
                                                              String dialogTitle,
                                                              int position,
                                                              boolean showNextButton) {
        final UnitOperandSelectionDialogFragment fragment = new UnitOperandSelectionDialogFragment();

        final Bundle args = new Bundle();
        args.putStringArrayList(ARG_SOURCE, new ArrayList<String>(source));
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        args.putInt(ARG_POSITION, position);
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
                    if(StringHandler.isNullOrEmpty(mSelectedString)) {
                        Toast.makeText(getActivity(), "No selection", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    final OpenHABWidget widget = mWidgetProvider.getWidgetByItemName(mSelectedString);
                    final IEntityDataType entityDataType = mIUnitEntityDataTypeProvider.getUnitEntityDataType(widget);
                    final UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationDialogButtonInterface buttonInterface = which == DialogInterface.BUTTON_POSITIVE ? UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE;
                    mListener.onOperationBuildResult(UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT, buttonInterface, entityDataType, mOperandIndex, null);
                    break;
                default:
                    mListener.onOperationBuildResult(UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT
                            , UnitEntityDataTypeProvider.RuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, 0, null);
                    break;
            }
        } else throw new IllegalArgumentException("listener is null");
    }
}
