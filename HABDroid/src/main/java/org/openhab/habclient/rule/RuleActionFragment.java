package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.RuleAction;
import org.openhab.domain.rule.RuleActionType;
import org.openhab.domain.rule.RuleActionValueType;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.dagger.DaggerRuleActionComponent;
import org.openhab.habdroid.R;

import javax.inject.Inject;

public class RuleActionFragment extends Fragment implements RuleActionDialogFragment.RuleActionBuildListener, IRuleOperationBuildListener {

    private final String TAG = "RuleActionFragment";
    private ListView mListView;
    private ArrayAdapter<RuleAction> mListAdapter;
    private int mSelectedActionPosition = -1;
    private RuleAction mActionUnderConstruction;
    private EditText mRuleNameView;
    
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    public static RuleActionFragment newInstance() {
        return new RuleActionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerRuleActionComponent.builder()
                .appComponent(((HABApplication) getActivity().getApplication()).appComponent())
                .build()
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(HABApplication.getLogTag(), "RuleActionFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rule_action, container, false);
        mRuleNameView = (EditText) view.findViewById(R.id.rule_name_textview);
        mListView = (ListView) view.findViewById(R.id.rule_then_list);

        mListAdapter = new ArrayAdapter<RuleAction>(getActivity(), android.R.layout.simple_list_item_1, ((RuleEditActivity)getActivity()).getRule().getActions());
        mListView.setAdapter(mListAdapter);

        setHasOptionsMenu(true);
        mRuleNameView.setText(((RuleEditActivity) getActivity()).getRuleName());
        TextWatcher ruleNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(HABApplication.getLogTag(), "ruleNameTextWatcher.afterTextChanged = " + s.toString());
                ((RuleEditActivity) getActivity()).setRuleName(s.toString());
            }
        };
        mRuleNameView.addTextChangedListener(ruleNameTextWatcher);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedActionPosition = position;
//                mListView.setItemChecked(position, true);
//                mListView.getFocusables(position);
//                mListView.setSelection(position);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LifeCycle", "RuleActionFragment.onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle", "RuleActionFragment.onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle", "RuleActionFragment.onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LifeCycle", "RuleActionFragment.onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("LifeCycle", "RuleActionFragment.onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "RuleActionFragment.onDestroy()");
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("LifeCycle", "RuleActionFragment.onAttach()");
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("LifeCycle", "RuleActionFragment.onDetach()");
//        mListener = null;
    }
    
    public void onUpdateView() {
        if(mRuleNameView != null)
            mRuleNameView.setText(((RuleEditActivity)getActivity()).getRuleName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rule_action_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_rule_action:
                openActionBuilderDialog(mSelectedActionPosition, RuleActionType.COMMAND);//COMMAND is used as default if something goes wrong.
                break;
            case R.id.action_delete_rule_action:
                //TODO - TA: Implement this.
                Toast.makeText(getActivity(), "Not implemented.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_add_rule_command_action:
                openActionBuilderDialog(-1, RuleActionType.COMMAND);
                break;
            case R.id.action_add_rule_message_action:
                openActionBuilderDialog(-1, RuleActionType.MESSAGE);
                break;
        }
        return true;
    }

    private void openActionBuilderDialog(int position, RuleActionType actionType) {
        mSelectedActionPosition = position;

        if(mSelectedActionPosition > -1)
            mActionUnderConstruction = mListAdapter.getItem(mSelectedActionPosition);
        else
            mActionUnderConstruction = new RuleAction(actionType, mWidgetProvider, mUnitEntityDataTypeProvider);

        if (mActionUnderConstruction == null ) {
            Toast.makeText(getActivity(), "Select a target item first.", Toast.LENGTH_SHORT).show();
        } else {
            RuleEditActivity activity = ((RuleEditActivity)getActivity());
            activity.setActionUnderConstruction(mActionUnderConstruction);
            activity.setRuleActionBuildListener(this);
            final RuleActionDialogFragment dialogFragment = RuleActionDialogFragment.newInstance();
            dialogFragment.show(getFragmentManager(), "Action_Builder_Tag");
        }
    }

    @Override
    public void onActionBuildResult(RuleActionDialogFragment.RuleActionBuildListener.RuleActionDialogButtonInterface ruleActionDialogButtonInterface,
                                    String actionID,
                                    RuleActionValueType ruleActionValueType,
                                    String valueAsText) {
        if (ruleActionDialogButtonInterface == RuleActionDialogFragment.RuleActionBuildListener.RuleActionDialogButtonInterface.CANCEL)
            return;

        RuleAction action;
        if (mActionUnderConstruction.getID().equalsIgnoreCase(actionID))
            action = mActionUnderConstruction;
        else if (/*((RuleAction) mListView.getSelectedItem())*/mListAdapter.getItem(mSelectedActionPosition).getID().equalsIgnoreCase(actionID)) {
            action = (RuleAction) mListView.getSelectedItem();
        } else {
            Log.w("onActionBuildResult occurred but no selected action was found with ID = " + actionID, HABApplication.getLogTag());
            return;
        }

        switch (ruleActionValueType) {
            case STATIC:
                action.setStaticValue(valueAsText);
                break;
            case SOURCE_UNIT:
                action.setSourceOpenHABItemName(valueAsText);
                break;
            case TEXT:
                action.setTextValue(valueAsText);
                break;
            case NA:
                action.setStaticValue(null);
                action.setSourceOpenHABItemName(null);
                action.setTextValue(null);
                break;
        }

        if (mSelectedActionPosition < 0/*mListView.getSelectedItem() == null*/) {
            ((RuleEditActivity)getActivity()).getRule().getActions().add(mActionUnderConstruction);
        } else {
            int actionIndex = ((RuleEditActivity)getActivity()).getRule().getActions().indexOf(/*mListView.getSelectedItem()*/mListAdapter.getItem(mSelectedActionPosition));
            ((RuleEditActivity)getActivity()).getRule().getActions().remove(actionIndex);
            ((RuleEditActivity)getActivity()).getRule().getActions().add(actionIndex, mActionUnderConstruction);
        }

//        IEntityDataType oldOperand = mOperationUnderConstruction.getOperand(operandPosition);
//        if(oldOperand != null && oldOperand.getSourceType() == EntityDataTypeSource.UNIT)
//            mWidgetProvider.removeItemListener((UnitEntityDataType)oldOperand);
//        mOperationUnderConstruction.setOperand(operandPosition, operand);
//        mWidgetProvider.addItemListener((UnitEntityDataType) operand);

        if(((RuleEditActivity)getActivity()).getRule().getRuleOperation() != null)
            ((RuleEditActivity)getActivity()).getRule().getRuleOperation().runCalculation();//TODO - TA: Temporary test code
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface ruleOperationSelectionInterface
            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType operand
            , int operandPosition, RuleOperator ruleOperator) {
        if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL)
            return;

        //THEN tab
        if(ruleOperationSelectionInterface == IRuleOperationBuildListener.RuleOperationSelectionInterface.UNIT && mActionUnderConstruction != null) {
            if (operandPosition == 0) {
                mActionUnderConstruction.setTargetOpenHABItemName(operand.getName());
                mActionUnderConstruction.validate();
            } else {
                mActionUnderConstruction.setSourceOpenHABItemName(operand.getName());
            }

            RuleEditActivity activity = ((RuleEditActivity)getActivity());
            activity.setActionUnderConstruction(mActionUnderConstruction);
            activity.setRuleActionBuildListener(this);
            final RuleActionDialogFragment dialogFragment = RuleActionDialogFragment.newInstance();
            dialogFragment.show(getFragmentManager(), "Action_Builder_Tag");
        }
    }
}
