package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.RuleAction;
import org.openhab.domain.rule.RuleActionType;
import org.openhab.domain.rule.RuleActionValueType;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.util.StringHandler;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleActionDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, IRuleOperationBuildListener {
    private static final String ARG_ID = "operand";
    private Button mButtonTargetUnit;
    private TextView mTextTargetUnit;
    private Spinner mSpinnerValue;
    private SpinnerAdapter mSpinnerAdapter;
    private Button mButtonSourceUnit;
    private TextView mTextSourceUnit;
    private TextView mTextValueLabel;
    private EditText mEditTextValue;
    private RuleActionValueType mRuleActionValueType;
    private TextWatcher mTextChangedListener;

    private RuleActionBuildListener mActionListener;
    private IRuleOperationBuildListener mOperationListener;
    private RuleAction mAction;
    private int mPosition;

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IAdapterProvider mAdapterProvider;

    protected static final String ARG_POSITION = "mPosition";

    public static RuleActionDialogFragment newInstance() {
        final RuleActionDialogFragment fragment = new RuleActionDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION, 1);
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

        Activity activity = getActivity();
        if(activity == null) throw new IllegalArgumentException("activity is null");
        mOperationListener = ((RuleEditActivity)activity).getRuleOperationBuildListener();
        mAction = ((RuleEditActivity)activity).getActionUnderConstruction();
        mActionListener = ((RuleEditActivity)activity).getRuleActionBuildListener();
        mPosition = args.getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private View createCustomView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_rule_create_action, null);

        if(view != null) {
            mButtonTargetUnit = (Button) view.findViewById(R.id.button_rule_action_builder_target_unit);
            mTextTargetUnit = (TextView) view.findViewById(R.id.text_rule_action_builder_target_unit);
            mSpinnerValue = (Spinner) view.findViewById(R.id.spinner_rule_action_builder_static_value);
            mButtonSourceUnit = (Button) view.findViewById(R.id.button_rule_action_builder_source_unit);
            mTextSourceUnit = (TextView) view.findViewById(R.id.text_rule_action_builder_source_unit);
            mTextValueLabel = (TextView) view.findViewById(R.id.text_rule_action_builder_text_value_label);
            mEditTextValue = (EditText) view.findViewById(R.id.edit_rule_action_builder_text_value);

            final IRuleOperationBuildListener localListener = this;
            mButtonTargetUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(localListener);
                    final UnitOperandSelectionDialogFragment dialogFragment
                            = UnitOperandSelectionDialogFragment.newInstance(mWidgetProvider.getItemNameListByWidgetType(OpenHABWidgetTypeSet.UnitItem)
                            , mButtonTargetUnit.getText().toString(), 0, false);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                }
            });

            mButtonSourceUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRuleActionValueType = RuleActionValueType.SOURCE_UNIT;
                    OpenHABItemType type = mWidgetProvider.getWidgetByItemName(mAction.getTargetOpenHABItemName()).getItem().getType();
                    List<String> itemNameList = mWidgetProvider.getItemNamesByType(type);
                    itemNameList.remove(mAction.getTargetOpenHABItemName());
                    ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(localListener);
                    final UnitOperandSelectionDialogFragment dialogFragment
                            //TODO - TA: Get a list of items with compatible values (A text target may have any item as source)
                            = UnitOperandSelectionDialogFragment.newInstance(itemNameList
                            , mButtonTargetUnit.getText().toString(), 1, false);
                    dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
                }
            });

            mSpinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > 0) {
                        mRuleActionValueType = RuleActionValueType.STATIC;
                        clearSourceSelection();
                        clearTextSelection();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mRuleActionValueType = RuleActionValueType.NA;
                }
            });

            mTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mRuleActionValueType = RuleActionValueType.TEXT;
                    clearSourceSelection();
                    clearStaticSelection();
                }
            };
            mEditTextValue.addTextChangedListener(mTextChangedListener);
        }

        if(mAction != null) {
            mRuleActionValueType = mAction.getValueType();
            setViewContent();
        }

        return view;
    }

    private void setViewContent() {
        if(mAction.getActionType() == RuleActionType.COMMAND) {
            //Set visibility
            mButtonTargetUnit.setVisibility(View.VISIBLE);
            mTextTargetUnit.setVisibility(View.VISIBLE);
            mSpinnerValue.setVisibility(mAction.getValueType() == RuleActionValueType.NA? (StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName()) || mAdapterProvider.getStaticUnitValueAdapter(getActivity(), mAction.getTargetOpenHABItemName()) == null? View.GONE : View.VISIBLE) : View.VISIBLE);
            mButtonSourceUnit.setVisibility(mAction.getValueType() == RuleActionValueType.NA? (StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName())? View.GONE : View.VISIBLE) : View.VISIBLE);
            mTextSourceUnit.setVisibility(mAction.getValueType() == RuleActionValueType.NA? (StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName())? View.GONE : View.VISIBLE) : View.VISIBLE);
            mTextValueLabel.setVisibility(mAction.getValueType() == RuleActionValueType.TEXT? View.VISIBLE: View.GONE);
            mEditTextValue.setVisibility(mAction.getValueType() == RuleActionValueType.TEXT? View.VISIBLE : View.GONE);

            //Set content
            if(StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName()))
                mSpinnerAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, new String[]{getString(R.string.no_value)});
            else if(mAdapterProvider.getStaticUnitValueAdapter(getActivity(), mAction.getTargetOpenHABItemName()) == null) {
                mSpinnerAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, new String[]{getString(R.string.no_value)});
                mSpinnerValue.setVisibility(View.GONE);
            } else
                mSpinnerAdapter = mAdapterProvider.getStaticUnitValueAdapter(getActivity(), mAction.getTargetOpenHABItemName());
            
            mSpinnerValue.setAdapter(mSpinnerAdapter);

            mTextSourceUnit.setText((mAction.getSourceUnit() == null? getString(R.string.no_value) : mAction.getSourceUnit().getDataSourceId()).toString());

            mEditTextValue.setSingleLine();
            mEditTextValue.removeTextChangedListener(mTextChangedListener);
            if(mAction.getTextValue() != null)
                mEditTextValue.setText(mAction.getTextValue());
            else {
                mEditTextValue.setText("");
                mEditTextValue.setHint(getString(R.string.no_value));
            }
            mEditTextValue.addTextChangedListener(mTextChangedListener);

            boolean initAll = !StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName());
            if(mAction.getValueType() == RuleActionValueType.NA)
                mTextTargetUnit.setText(getString(R.string.no_value));
            if(initAll || mAction.getValueType() == RuleActionValueType.STATIC)
                for(int i = 1 ; i < mSpinnerAdapter.getCount(); i++)
                    if(mSpinnerAdapter.getItem(i).equals(mAction.getStaticValue())){
                        mSpinnerValue.setSelection(i);
                        continue;
                    }

            mTextTargetUnit.setText((StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName()) ? getString(R.string.no_value) : mAction.getTargetOpenHABItemName()).toString());

        } else if(mAction.getActionType() == RuleActionType.MESSAGE) {
            mButtonTargetUnit.setVisibility(View.GONE);
            mTextTargetUnit.setVisibility(View.GONE);
            mSpinnerValue.setVisibility(View.GONE);
            mButtonSourceUnit.setVisibility(View.GONE);
            mTextSourceUnit.setVisibility(View.GONE);
            mTextValueLabel.setVisibility(View.VISIBLE);
            mEditTextValue.setVisibility(View.VISIBLE);

            mEditTextValue.setSingleLine(false);
            if(mAction.getTextValue() != null)
                mEditTextValue.setText(mAction.getTextValue());
            else {
                mEditTextValue.setText("");
                mEditTextValue.setHint(getString(R.string.no_value));
            }
        }
    }

    private void clearSourceSelection() {
        mTextSourceUnit.setText(getString(R.string.no_value));
    }

    private void clearStaticSelection() {
        mSpinnerValue.setSelection(0);
    }

    private void clearTextSelection() {
        mEditTextValue.removeTextChangedListener(mTextChangedListener);
        mEditTextValue.setText("");
        mEditTextValue.setHint(R.string.no_value);
        mEditTextValue.addTextChangedListener(mTextChangedListener);
    }

    @Override
    public <T> void onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface ruleOperationSelectionInterface, IRuleOperationBuildListener.RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType<T> operand, int operandPosition, RuleOperator<T> ruleOperator) {
        if(ruleOperationDialogButtonInterface != IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL) {
            mOperationListener.onOperationBuildResult(ruleOperationSelectionInterface, ruleOperationDialogButtonInterface, operand, operandPosition, ruleOperator);
            dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

//        final int statusTextId = mStatusTextResourceProvider.getStatusText(mStatus);
//        final String statusText = getString(statusTextId);

        return new AlertDialog.Builder(activity).setTitle((StringHandler.isNullOrEmpty(mAction.getTargetOpenHABItemName())? "Create new" : "Edit") + " rule action")
                .setView(createCustomView(activity))
                .setPositiveButton("Done", this)
                .setNegativeButton("Cancel", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE://Done
                if(mActionListener != null) {
                    String value = null;
                    switch (mRuleActionValueType) {
                        case STATIC:
                            value = mSpinnerValue.getSelectedItem().toString();
                            break;
                        case SOURCE_UNIT:
                            value = mTextSourceUnit.getText().toString();
                            break;
                        case TEXT:
                            value = mEditTextValue.getText().toString();
                            break;
                        default:
                            mRuleActionValueType = mAction.getValueType();
                    }

                    mActionListener.onActionBuildResult(RuleActionBuildListener.RuleActionDialogButtonInterface.DONE
                            , mAction.getID(), mRuleActionValueType, value);
                }  else throw new IllegalArgumentException("listener is null");
                break;
        }
    }

    public interface RuleActionBuildListener {
        public enum RuleActionSelectionInterface {
            TARGET_UNIT(0),
            NEW_ACTION(1),
            OLD_ACTION(2),
            SOURCE_UNIT(3),
            STATIC(4),
            TEXT(5);

            public final int Value;

            private RuleActionSelectionInterface(int value) {
                Value = value;
            }
        }

        public enum RuleActionDialogButtonInterface {
            CANCEL(0),
            DONE(1);

            public final int Value;

            private RuleActionDialogButtonInterface(int value) {
                Value = value;
            }
        }
        public void onActionBuildResult(RuleActionDialogButtonInterface ruleActionDialogButtonInterface,
                                        String actionID,
                                        RuleActionValueType ruleActionValueType,
                                        String valueAsText);
    }
}
