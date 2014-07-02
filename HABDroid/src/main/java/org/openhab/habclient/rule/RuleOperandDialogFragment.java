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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperandDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String ARG_ID = "operand";
    //    protected static final String ARG_DIALOG_TITLE = "dialogTitle";
    protected static final String ARG_POSITION = "mPosition";
    protected static final String ARG_SHOW_NEXT_BUTTON = "showNextButton";

    private Button mButtonUnit;
    private TextView mTextUnit;
    private Button mButtonOperation;
    private TextView mTextOperation;
    private TextView mTextLabelStaticValue;
    private EditText mEditNewOperation;
    private Spinner mSpinnerValue;
    private SpinnerAdapter mSpinnerAdapter;
    private EditText mEditStaticValue;
    private boolean mShowNextButton;
    private OpenHABWidget mFirstOperandWidgetIfAny;

    private RuleOperationBuildListener mListener;
    private IEntityDataType mOldOperand;
    private int mPosition;
    @Inject IOpenHABWidgetProvider mWidgetProvider;

    public static RuleOperandDialogFragment newInstance(int position, boolean showNextButton) {
        final RuleOperandDialogFragment fragment = new RuleOperandDialogFragment();

        final Bundle args = new Bundle();
//        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
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

        final Bundle args = getArguments();
        if(args == null)
            return;

        mPosition = args.getInt(ARG_POSITION);
        mShowNextButton = args.getBoolean(ARG_SHOW_NEXT_BUTTON);

        Activity activity = getActivity();
        if(activity == null) throw new IllegalArgumentException("activity is null");
        mListener = ((RuleEditActivity)activity).getRuleOperationBuildListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
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
            mTextLabelStaticValue = (TextView) view.findViewById(R.id.text_rule_operation_builder_static_value_label);
            mSpinnerValue = (Spinner) view.findViewById(R.id.spinner_rule_operation_builder_static_value);
            mEditStaticValue = (EditText) view.findViewById(R.id.edit_rule_operation_builder_static_value);

            mButtonUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(mListener);
                    final UnitOperandSelectionDialogFragment dialogFragment
                            = UnitOperandSelectionDialogFragment.newInstance(mWidgetProvider.getItemNameListByWidgetType(OpenHABWidgetTypeSet.UnitItem)
                            , mButtonUnit.getText().toString(), mPosition, mShowNextButton);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                    dismiss();
                }
            });
        }

        Map<String, ?> staticValuesCompatibleWithFirstOperand = null;
        boolean enableStaticValueSpinner = false;
        if(mPosition > 0) {//Try to enable views for static value input if this isn´t the first operand.
            try {
                mFirstOperandWidgetIfAny = mWidgetProvider.getWidgetByItemName(((RuleEditActivity) getActivity()).getOperationToEdit().getOperand(0).getDataSourceId());
                staticValuesCompatibleWithFirstOperand = UnitEntityDataType.getUnitEntityDataType(mFirstOperandWidgetIfAny).getStaticValues();
                mSpinnerAdapter = AdapterProvider.getStaticUnitValueAdapter(getActivity(), mFirstOperandWidgetIfAny);
            } catch (Exception e) {};

            enableStaticValueSpinner = staticValuesCompatibleWithFirstOperand != null && staticValuesCompatibleWithFirstOperand.size() > 0;
            mSpinnerValue.setAdapter(mSpinnerAdapter);
            mEditStaticValue.setVisibility(enableStaticValueSpinner ? View.GONE : View.VISIBLE);
            mSpinnerValue.setVisibility(enableStaticValueSpinner ? View.VISIBLE : View.GONE);
            mTextLabelStaticValue.setVisibility(View.VISIBLE);
        } else {
            mTextLabelStaticValue.setVisibility(View.GONE);
            mEditStaticValue.setVisibility(View.GONE);
            mSpinnerValue.setVisibility(View.GONE);
        }

        mOldOperand = ((RuleEditActivity)getActivity()).getOperandToEdit();
        if(mOldOperand != null) {
            switch (mOldOperand.getSourceType()) {
                case UNIT:
                    mTextUnit.setText(mOldOperand.toString());
                    break;
                case OPERATION:
                    mTextOperation.setText(mOldOperand.toString());
                    break;
                case STATIC:
                    if(mSpinnerValue.getVisibility() == View.VISIBLE) {
                        //Set current value (if any) in spinner
                        if(mOldOperand.getValue() != null)
                            for(int i = 1 ; i < mSpinnerAdapter.getCount(); i++)
                                if(mSpinnerAdapter.getItem(i).equals(mOldOperand.getValue())){
                                    mSpinnerValue.setSelection(i);
                                    continue;
                                }
                    } else
                        mEditStaticValue.setText(mOldOperand.toString());
                    break;
            }
        }

        mSpinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

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
        if(mListener != null) {
            switch (which) {
                case DialogInterface.BUTTON_NEUTRAL: //Done
                case DialogInterface.BUTTON_POSITIVE://Next
                    RuleOperationBuildListener.RuleOperationSelectionInterface resultingSelectionType;
                    IEntityDataType operand =  mFirstOperandWidgetIfAny != null? UnitEntityDataType.getUnitEntityDataType(mFirstOperandWidgetIfAny) : null;
                    if(mEditNewOperation.getText().length() > 0) {
                        operand = new RuleOperation(mEditNewOperation.getText().toString());
                        resultingSelectionType = RuleOperationBuildListener.RuleOperationSelectionInterface.NEW_OPERATION;
                    } else if(mEditStaticValue.getVisibility() == View.VISIBLE) {
                        ((UnitEntityDataType) operand).setValue(operand.valueOf(mEditStaticValue.getText().toString()));
                        resultingSelectionType = RuleOperationBuildListener.RuleOperationSelectionInterface.STATIC;
                    } else {
                        Map<String, ?> staticValuesCompatibleWithFirstOperand = UnitEntityDataType.getUnitEntityDataType(mFirstOperandWidgetIfAny).getStaticValues();
                        ((UnitEntityDataType) operand).setValue(mSpinnerValue.getSelectedItemPosition() > 0 ? staticValuesCompatibleWithFirstOperand.get(mSpinnerValue.getSelectedItem()) : null);
                        resultingSelectionType = RuleOperationBuildListener.RuleOperationSelectionInterface.STATIC;
                    }

                    mListener.onOperationBuildResult(resultingSelectionType
                            , which == DialogInterface.BUTTON_POSITIVE?  RuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT :RuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
                            , operand, mPosition, null);
                    break;
                default:
                    mListener.onOperationBuildResult(RuleOperationBuildListener.RuleOperationSelectionInterface.NEW_OPERATION//Just a default non-used value since we are aborting.
                            , RuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, mPosition, null);

            }
        } else throw new IllegalArgumentException("listener is null");
    }

    public interface RuleOperationBuildListener {
        public enum RuleOperationSelectionInterface {
            UNIT(0),
            NEW_OPERATION(1),
            OLD_OPERATION(2),
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
