package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.habclient.InjectUtils;

import java.util.ArrayList;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperandDialogFragment extends DialogFragment {
    private static final String ARG_RULE_OPERATOR_TYPE = "ruleOperatorType";
    private OnRuleOperationChangedListener mListener;
    private RuleOperatorType mRuleOperatorType;

    public static RuleOperandDialogFragment newInstance(RuleOperatorType currentType) {
        final RuleOperandDialogFragment fragment = new RuleOperandDialogFragment();

        final Bundle args = new Bundle();
        args.putString(ARG_RULE_OPERATOR_TYPE, currentType.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (OnRuleOperationChangedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);

        final Bundle args = getArguments();
        if(args == null)
            return;

        mRuleOperatorType = RuleOperatorType.valueOf(args.getString(ARG_RULE_OPERATOR_TYPE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

        final ArrayAdapter<RuleOperatorType> operandAdapter = new ArrayAdapter<RuleOperatorType>(activity,
                android.R.layout.simple_selectable_list_item,
                new ArrayList<RuleOperatorType>(RuleOperatorType.TYPES));

        return new AlertDialog.Builder(activity)
                .setTitle("Change operand")
                .setAdapter(operandAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener != null) {
                            mListener.onRuleOperationChanged(operandAdapter.getItem(which));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    public interface OnRuleOperationChangedListener {
        void onRuleOperationChanged(RuleOperatorType ruleOperatorType);
    }
}
