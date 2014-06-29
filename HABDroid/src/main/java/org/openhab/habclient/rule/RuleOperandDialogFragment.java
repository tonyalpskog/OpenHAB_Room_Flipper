package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.habclient.HABApplication;
import org.openhab.habdroid.R;
import org.openhab.rule.IEntityDataType;
import org.openhab.rule.RuleOperation;
import org.openhab.rule.RuleOperator;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperandDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String ARG_ID = "operand";
    private Button mButtonUnit;
    private TextView mTextUnit;
    private Button mButtonOperation;
    private TextView mTextOperation;
    private EditText mEditNewOperation;
    private EditText mEditStaticValue;
    private boolean mShowNextButton;

    private RuleOperationBuildListener mListener;
    private IEntityDataType mOldOperand;
    private int mPosition;

    public RuleOperandDialogFragment(IEntityDataType currentOperand, int position, boolean showNextButton) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARG_ID, status.value());
//        this.setArguments(bundle);
        mOldOperand = currentOperand;
        mPosition = position;
        mShowNextButton = showNextButton;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (RuleOperationBuildListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if(activity == null) throw new IllegalArgumentException("activity is null");

//        Injector injector = (Injector) getActivity().getApplication();
//        injector.inject(this);

//        Bundle args = getArguments();
//        if(args != null) {
//            mStatus = CaseTaskStatus.fromId(args.getInt(ARG_ID));
//        }
    }

    private View createCustomView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_rule_select_operand, null);

        if(view != null) {
            mButtonUnit = (Button) view.findViewById(R.id.button_rule_operation_builder_unit);
            mTextUnit = (TextView) view.findViewById(R.id.text_rule_operation_builder_unit);
            mButtonOperation = (Button) view.findViewById(R.id.button_rule_operation_builder_operation);
            mTextOperation = (TextView) view.findViewById(R.id.text_rule_operation_builder_operation);
            mEditNewOperation = (EditText) view.findViewById(R.id.edit_rule_operation_builder_new_operation);
            mEditStaticValue = (EditText) view.findViewById(R.id.edit_rule_operation_builder_static_value);

            mButtonUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final UnitOperandSelectionDialogFragment dialogFragment
                            = UnitOperandSelectionDialogFragment.newInstance(HABApplication.getOpenHABWidgetProvider2().getItemNameListByWidgetType(OpenHABWidgetTypeSet.UnitItem)
                            , mButtonUnit.getText().toString(), mPosition, mShowNextButton, mListener);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                    dismiss();
                }
            });
        }

        if(mOldOperand != null) {
            switch (mOldOperand.getSourceType()) {
                case UNIT:
                    mTextUnit.setText(mOldOperand.toString());
                    break;
                case OPERATION:
                    mTextOperation.setText(mOldOperand.toString());
                    break;
                case STATIC:
                    mEditStaticValue.setText(mOldOperand.toString());
                    break;
            }
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

//        final int statusTextId = mStatusTextResourceProvider.getStatusText(mStatus);
//        final String statusText = getString(statusTextId);

        if(mShowNextButton)
            return new AlertDialog.Builder(activity).setTitle("Select " + (mPosition == 0 ? "left" : "right") + " side operand")
                    .setView(createCustomView(activity))
                    .setPositiveButton("Next", this)
                    .setNegativeButton("Cancel", this)
                    .setNeutralButton("Done", this)
                    .create();

        return new AlertDialog.Builder(activity).setTitle("Select " + (mPosition == 0 ? "left" : "right") + " side operand")
                .setView(createCustomView(activity))
                .setNegativeButton("Cancel", this)
                .setNeutralButton("Done", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE://Next
                if(mListener != null) {
//                    boolean machineUsable = mCheckMachinceUsable.isChecked();
//                    boolean liquidsRemoved = mCheckLiquidsRemoved.isChecked();
//                    String volumeOfLiquids = mEditVolumeOfLiquids.getText().toString();
//                    String abandonReason = mEditAbandonReason.getText().toString();

//                    mListener.onCheckListSave(mStatus, machineUsable, liquidsRemoved, volumeOfLiquids, abandonReason);
                    mListener.onOperationBuildResult(RuleOperationBuildListener.RuleOperationSelectionInterface.NEW_RULE
                            , RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT
                    , new RuleOperation(mEditNewOperation.getText().toString()), mPosition, null);
                }
                break;
        }
    }

    public interface RuleOperationBuildListener {
        public enum RuleOperationSelectionInterface {
            UNIT(0),
            NEW_RULE(1),
            OLD_RULE(2),
            STATIC(3),
            OPERATOR(4);

            public final int Value;

            private RuleOperationSelectionInterface(int value) {
                Value = value;
            }
        }

        public enum RuleOperationDialogButtonInterface {
            CANCEL(0),
            DONE(1),
            NEXT(3);

            public final int Value;

            private RuleOperationDialogButtonInterface(int value) {
                Value = value;
            }
        }
        public <T> void onOperationBuildResult(RuleOperationSelectionInterface ruleOperationSelectionInterface,
                                           RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface,
                                           IEntityDataType<T> operand,
                                           int operandPosition,
                                           RuleOperator<T> ruleOperator);
    }
}
