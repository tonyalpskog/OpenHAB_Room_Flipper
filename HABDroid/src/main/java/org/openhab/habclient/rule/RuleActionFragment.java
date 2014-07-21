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
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import java.util.List;

import javax.inject.Inject;

public class RuleActionFragment extends Fragment implements RuleActionDialogFragment.RuleActionBuildListener, IRuleOperationBuildListener {

    private final String TAG = "RuleActionFragment";
    private ListView mListView;
    private ArrayAdapter<RuleAction> mListAdapter;
    private int mSelectedActionPosition = -1;
    private RuleAction mActionUnderConstruction;

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;
    private RuleActionFragmentListener mListener;

    public static RuleActionFragment newInstance() {
        return new RuleActionFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (RuleActionFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(HABApplication.getLogTag(), "RuleActionFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rule_action, container, false);
        EditText mRuleNameView = (EditText) view.findViewById(R.id.rule_name_textview);
        mListView = (ListView) view.findViewById(R.id.rule_then_list);

        mListAdapter = new ArrayAdapter<RuleAction>(getActivity(), android.R.layout.simple_list_item_1, mListener.getRuleActions());
        mListView.setAdapter(mListAdapter);

        setHasOptionsMenu(true);
        mRuleNameView.setText(mListener.getRuleName());
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
                mListener.setRuleName(s.toString());
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
            }
        });

        return view;
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
            mListener.getRuleActions().add(mActionUnderConstruction);
        } else {
            int actionIndex = mListener.getRuleActions().indexOf(mListAdapter.getItem(mSelectedActionPosition));
            mListener.getRuleActions().remove(actionIndex);
            mListener.getRuleActions().add(actionIndex, mActionUnderConstruction);
        }

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface ruleOperationSelectionInterface
            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType operand
            , int operandPosition) {
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
            final RuleActionDialogFragment dialogFragment = RuleActionDialogFragment.newInstance();
            dialogFragment.show(getFragmentManager(), "Action_Builder_Tag");
        }
    }

    public interface RuleActionFragmentListener {
        List<RuleAction> getRuleActions();
        void setRuleName(String name);
        String getRuleName();
    }
}
