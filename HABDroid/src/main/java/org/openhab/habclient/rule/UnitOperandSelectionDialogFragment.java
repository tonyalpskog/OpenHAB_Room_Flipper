package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;


import org.openhab.domain.business.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.util.StringSelectionDialogFragment;
import org.openhab.rule.IEntityDataType;
import org.openhab.rule.UnitEntityDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitOperandSelectionDialogFragment extends StringSelectionDialogFragment {
    private static final String ARG_POSITION = "position";

    private int mOperandIndex;
    RuleOperandDialogFragment.RuleOperationBuildListener mListener;

    public static UnitOperandSelectionDialogFragment newInstance(List<String> source,
                                                              String dialogTitle,
                                                              int position,
                                                              boolean showNextButton,
                                                              RuleOperandDialogFragment.RuleOperationBuildListener listener) {
        final UnitOperandSelectionDialogFragment fragment = new UnitOperandSelectionDialogFragment();
        fragment.setListener(listener);

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

        Bundle args = getArguments();
        if(args == null)
            return;

        mOperandIndex = args.getInt(ARG_POSITION);
    }

    public void setListener(RuleOperandDialogFragment.RuleOperationBuildListener listener) {
        mListener = listener;
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
                    final HABApplication application = (HABApplication) getActivity().getApplication();
                    final IOpenHABWidgetProvider provider = application.getOpenHABWidgetProvider();
                    final OpenHABWidget widget = provider.getWidgetByItemName(mSelectedString);
                    final IEntityDataType entityDataType = UnitEntityDataType.getUnitEntityDataType(widget);
                    final RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface buttonInterface = which == DialogInterface.BUTTON_POSITIVE ? RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE;
                    getListener().onOperationBuildResult(RuleOperandDialogFragment.RuleOperationBuildListener.RuleOperationSelectionInterface.UNIT, buttonInterface, entityDataType, mOperandIndex, null);
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
