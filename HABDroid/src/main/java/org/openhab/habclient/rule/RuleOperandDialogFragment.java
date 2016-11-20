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
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.habclient.HABApplication;
import org.openhab.habdroid.R;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperandDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, IRuleOperationBuildListener {
    private static final String ARG_ID = "operand";
    //    protected static final String ARG_DIALOG_TITLE = "dialogTitle";
    protected static final String ARG_POSITION = "mPosition";
    protected static final String ARG_SHOW_NEXT_BUTTON = "showNextButton";

    private Button mButtonUnit;
    private TextView mTextUnit;
    private Button mButtonOperation;
    private TextView mTextOperation;
    private TextView mTextLabelStaticValue;
    private TextView mTextLabelOperation;
    private EditText mEditNewOperation;
    private Spinner mSpinnerValue;
    private SpinnerAdapter mSpinnerAdapter;
    private EditText mEditStaticValue;
    private boolean mShowNextButton;
    private OpenHABWidget mFirstOperandWidgetIfAny;
    private IEntityDataType mLeftSideOperand;

    private IRuleOperationBuildListener mListener;
    private IEntityDataType mOldOperand;
    private int mPosition;//The operand order number. The leftmost = first = 0

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;
    @Inject IAdapterProvider mAdapterProvider;
    @Inject IRuleProvider mRuleProvider;

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

        ((HABApplication)getActivity().getApplication()).appComponent()
                .ruleOperand()
                .inject(this);

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
            mTextLabelOperation = (TextView) view.findViewById(R.id.text_rule_operation_builder_operation_label);
            mTextOperation = (TextView) view.findViewById(R.id.text_rule_operation_builder_operation);
            mEditNewOperation = (EditText) view.findViewById(R.id.edit_rule_operation_builder_new_operation);
            mTextLabelStaticValue = (TextView) view.findViewById(R.id.text_rule_operation_builder_static_value_label);
            mSpinnerValue = (Spinner) view.findViewById(R.id.spinner_rule_operation_builder_static_value);
            mEditStaticValue = (EditText) view.findViewById(R.id.edit_rule_operation_builder_static_value);

            final IRuleOperationBuildListener localListener = this;
            mButtonUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(localListener);
                    final UnitOperandSelectionDialogFragment dialogFragment
                            = UnitOperandSelectionDialogFragment.newInstance(mWidgetProvider.getItemNameListByWidgetType(OpenHABWidgetTypeSet.UnitItem)
                            , mButtonUnit.getText().toString(), mPosition, mShowNextButton);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                }
            });
            mButtonOperation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(localListener);
                    final OperationOperandSelectionDialogFragment dialogFragment
                            = OperationOperandSelectionDialogFragment.newInstance(mRuleProvider.getUserRules(RuleListActivity.TemporaryHardCodedUserId)
                            , mButtonUnit.getText().toString(), mPosition, mShowNextButton);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                }
            });
        }

        Map<String, ?> staticValuesCompatibleWithFirstOperand = null;
        boolean enableStaticValueSpinner = false;
        if(mPosition > 0) {//Try to enable views for static value input if this isn´t the first operand.
            try {
                mLeftSideOperand = ((RuleEditActivity) getActivity()).getOperationToEdit().getOperand(0);
                if(mLeftSideOperand.getSourceType() == EntityDataTypeSource.OPERATION) {
                    staticValuesCompatibleWithFirstOperand = RuleOperation.getStaticEntityDataType(null).getStaticValues();
                    mSpinnerAdapter = mAdapterProvider.getStaticOperationValueAdapter(getActivity(), true);
                } else {
                    mFirstOperandWidgetIfAny = mWidgetProvider.getWidgetByItemName(mLeftSideOperand.getDataSourceId());
                    staticValuesCompatibleWithFirstOperand = mUnitEntityDataTypeProvider.getUnitEntityDataType(mFirstOperandWidgetIfAny).getStaticValues();
                    mSpinnerAdapter = mAdapterProvider.getStaticUnitValueAdapter(getActivity(), mFirstOperandWidgetIfAny);
                }
            } catch (Exception e) {};

            enableStaticValueSpinner = staticValuesCompatibleWithFirstOperand != null && staticValuesCompatibleWithFirstOperand.size() > 0;
            mSpinnerValue.setAdapter(mSpinnerAdapter);
            mEditStaticValue.setVisibility(enableStaticValueSpinner ? View.GONE : View.VISIBLE);
            mSpinnerValue.setVisibility(enableStaticValueSpinner ? View.VISIBLE : View.GONE);
            mTextLabelStaticValue.setVisibility(View.VISIBLE);

            //Set operation selection visibility
            int operationVisibility = mLeftSideOperand == null || mLeftSideOperand.getSourceType() == EntityDataTypeSource.OPERATION? View.VISIBLE : View.GONE;
            mTextLabelOperation.setVisibility(operationVisibility);
            mButtonOperation.setVisibility(operationVisibility);
            mTextOperation.setVisibility(operationVisibility);
            mEditNewOperation.setVisibility(operationVisibility);

            int unitVisibility = mLeftSideOperand == null || mLeftSideOperand.getSourceType() != EntityDataTypeSource.OPERATION? View.VISIBLE : View.GONE;
            mButtonUnit.setVisibility(unitVisibility);
            mTextUnit.setVisibility(unitVisibility);

        } else {
            mButtonUnit.setVisibility(View.VISIBLE);
            mTextUnit.setVisibility(View.VISIBLE);

            mTextLabelStaticValue.setVisibility(View.GONE);
            mEditStaticValue.setVisibility(View.GONE);
            mSpinnerValue.setVisibility(View.GONE);

            //Set operation selection visibility
            mTextLabelOperation.setVisibility(View.VISIBLE);
            mButtonOperation.setVisibility(View.VISIBLE);
            mTextOperation.setVisibility(View.VISIBLE);
            mEditNewOperation.setVisibility(View.VISIBLE);
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
                    .setPositiveButton(activity.getString(R.string.choice_next), this)
                    .setNegativeButton(activity.getString(R.string.choice_cancel), this)
                    .setNeutralButton(activity.getString(R.string.choice_done), this)
                    .create();

        return new AlertDialog.Builder(activity).setTitle("Select " + (mPosition == 0 ? "left" : "right") + " side operand")
                .setView(createCustomView(activity))
                .setNegativeButton(activity.getString(R.string.choice_cancel), this)
                .setNeutralButton(activity.getString(R.string.choice_done), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(mListener != null) {
            switch (which) {
                case DialogInterface.BUTTON_NEUTRAL: //Done
                case DialogInterface.BUTTON_POSITIVE://Next
                    //This code shall handle new/edited/unchanged values for EntityDataTypeSource.STATIC and EntityDataTypeSource.OPERATION selection
                    //AND unchanged EntityDataTypeSource.UNIT values.
                    IRuleOperationBuildListener.RuleOperationSelectionInterface resultingSelectionType;
                    IEntityDataType operand = mFirstOperandWidgetIfAny != null ? mUnitEntityDataTypeProvider.getStaticEntityDataType(mFirstOperandWidgetIfAny, null) : null;
                    if (mEditNewOperation.getText().length() > 0) {
                        //User selected a new RuleOperation as an operand.
                        operand = new RuleOperation(mEditNewOperation.getText().toString());
                        resultingSelectionType = IRuleOperationBuildListener.RuleOperationSelectionInterface.NEW_OPERATION;
                    } else if (mEditStaticValue.getVisibility() == View.VISIBLE && mEditStaticValue.getText().length() > 0) {
                        //The operand is a static text vale entered by the user.
                        ((UnitEntityDataType) operand).setValue(operand.valueOf(mEditStaticValue.getText().toString()), false);
                        resultingSelectionType = IRuleOperationBuildListener.RuleOperationSelectionInterface.STATIC;
                    } else if (mSpinnerValue.getSelectedItemPosition() > 0) {
                        //Spinner selection
                        if (mLeftSideOperand != null && mLeftSideOperand.getSourceType() == EntityDataTypeSource.OPERATION) {//TODO - TA: Fix this shit.
                            //The user has selected a static operation result (true or false).
                            Map<String, ?> staticValuesCompatibleWithFirstOperand = RuleOperation.getStaticEntityDataType(null).getStaticValues();
                            operand = RuleOperation.getStaticEntityDataType(mSpinnerValue.getSelectedItemPosition() > 0 ? mSpinnerValue.getSelectedItem().toString() : null);
                            resultingSelectionType = IRuleOperationBuildListener.RuleOperationSelectionInterface.STATIC;
                        } else {
                            //The user has selected a static operand value.
                            Map<String, ?> staticValuesCompatibleWithFirstOperand = mUnitEntityDataTypeProvider.getUnitEntityDataType(mFirstOperandWidgetIfAny).getStaticValues();
                            ((UnitEntityDataType) operand).setValue(mSpinnerValue.getSelectedItemPosition() > 0 ? staticValuesCompatibleWithFirstOperand.get(mSpinnerValue.getSelectedItem()) : null, false);
                            resultingSelectionType = IRuleOperationBuildListener.RuleOperationSelectionInterface.STATIC;
                        }
                    } else {
                        //Unchanged value of any type
                        operand = mOldOperand;
                        resultingSelectionType =  which == DialogInterface.BUTTON_POSITIVE? RuleOperationSelectionInterface.UNIT : RuleOperationSelectionInterface.NA;
                    }

                    mListener.onOperationBuildResult(resultingSelectionType
                            , which == DialogInterface.BUTTON_POSITIVE?  IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT : IRuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE
                            , operand, mPosition, null);
                    break;
                default:
                    mListener.onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface.NEW_OPERATION//Just a default non-used value since we are aborting.
                            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL
                            , null, mPosition, null);

            }
        } else throw new IllegalArgumentException("listener is null");
    }

    @Override
    public <T> void onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface ruleOperationSelectionInterface, IRuleOperationBuildListener.RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType<T> operand, int operandPosition, RuleOperator<T> ruleOperator) {
        if(ruleOperationDialogButtonInterface != IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL) {
            mListener.onOperationBuildResult(ruleOperationSelectionInterface, ruleOperationDialogButtonInterface, operand, operandPosition, ruleOperator);
            dismiss();
        }
    }
}
