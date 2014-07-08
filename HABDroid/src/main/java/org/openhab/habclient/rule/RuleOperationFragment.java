package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.UnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IRuleOperationBuildListener;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.RuleTreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class RuleOperationFragment extends Fragment implements IRuleOperationBuildListener {
    private final String TAG = "RuleOperationFragment";

    private ExpandableMultiLevelGroupAdapter mTreeListAdapter;
    private ExpandableListView mTreeView;
    private SparseArray<RuleTreeItem> mTreeData;
    private RuleTreeItem mSelectedTreeItem;
    private RuleOperation mOperationUnderConstruction;
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IRuleOperationProvider mRuleOperationProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;
    @Inject IAdapterProvider mAdapterProvider;

    public static RuleOperationFragment newInstance() {
        return new RuleOperationFragment();
    }

    public RuleOperationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);
    }

//    public class OpenHABWidgetSpinnerItem extends OpenHABWidget
//    {
//        @Override
//        public String toString() {
//            return String.format("(%s) %s", getType().name(), getLabel());
//
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(HABApplication.getLogTag(), "RuleOperationFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rule_operation, container, false);
        EditText mRuleNameView = (EditText) view.findViewById(R.id.rule_name_textview);
        mTreeView = (ExpandableListView) view.findViewById(R.id.rule_if_tree);
        mTreeData = new SparseArray<RuleTreeItem>();

        mTreeListAdapter = new ExpandableMultiLevelGroupAdapter(getActivity(), mTreeData);
        mTreeView.setAdapter(mTreeListAdapter);

        updateRuleTree(((RuleEditActivity) getActivity()).getRule().getRuleOperation());

        ((RuleEditActivity)getActivity()).setRuleOperationBuildListener(this);

        mTreeView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();

                Toast.makeText(getActivity(), "OnGroupClickListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        mTreeData.get(Integer.valueOf(groupPosition)).mName
//                                + " : "
//                                + mTreeData.get(Integer.valueOf(groupPosition)).mChildren.get(Integer.valueOf(childPosition)).mName, Toast.LENGTH_SHORT
//                )
//                        .show();
                mSelectedTreeItem = mTreeData.get(Integer.valueOf(groupPosition)).mChildren.get(Integer.valueOf(childPosition));
                openOperationBuilderDialog(-1, mSelectedTreeItem);
                return false;
            }
        });

        mTreeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // Listview Group expanded listener
        mTreeView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                mSelectedTreeItem = mTreeData.get(Integer.valueOf(groupPosition));
                Toast.makeText(getActivity(),
                        mTreeData.get(Integer.valueOf(groupPosition)).getItemId() + " OnGroupExpandListener",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        mTreeView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        mTreeData.get(Integer.valueOf(groupPosition)).getItemId() + " OnGroupCollapseListener",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mTreeView.setOnLongClickListener(new ExpandableListView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "ExpandableListView.OnLongClick", Toast.LENGTH_SHORT).show();
                openOperationBuilderDialog(-1, mSelectedTreeItem);
                return false;
            }
        });

        mTreeView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getActivity(),
                        position + " OnItemLongClickListener" + "  Parent => " + ((RuleTreeItem)parent.getSelectedItem()).mName, Toast.LENGTH_SHORT)
                        .show();
//                mSelectedTreeItem = XXX
                //TODO - TA: set parent as mSelectedTreeItem
//                openOperationBuilderDialog(-1, mSelectedTreeItem);
                return false;
            }
        });

        mRuleNameView.setText(((RuleEditActivity)getActivity()).getRuleName());
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
                ((RuleEditActivity)getActivity()).setRuleName(s.toString());
            }
        };
        mRuleNameView.addTextChangedListener(ruleNameTextWatcher);

        setHasOptionsMenu(true);

        RuleOperation initialRootOperation = null/*getInitialTreeOperation()*/;//TODO - TA: getInitialTreeOperation is just a time saver for UI test
        ((RuleEditActivity)getActivity()).getRule().setRuleOperation(initialRootOperation);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LifeCycle", "RuleOperationFragment.onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle", "RuleOperationFragment.onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle", "RuleOperationFragment.onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LifeCycle", "RuleOperationFragment.onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("LifeCycle", "RuleOperationFragment.onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "RuleOperationFragment.onDestroy()");
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
        Log.d("LifeCycle", "RuleOperationFragment.onAttach()");
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
        Log.d("LifeCycle", "RuleOperationFragment.onDetach()");
//        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rule_operation_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_rule_operation_member:
                openOperationBuilderDialog(-1, mSelectedTreeItem);
                break;
            case R.id.action_delete_rule_operation_member:
                //TODO - TA: Implement this.
                Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void updateRuleTree(RuleOperation ruleOperationRoot) {
        if(mTreeData == null)
            mTreeData = new SparseArray<RuleTreeItem>();

        mTreeData.clear();
        if(ruleOperationRoot != null)
            mTreeData.put(0, ruleOperationRoot.getRuleTreeItem(0));
        mTreeListAdapter.notifyDataSetInvalidated();
    }

    public RuleTreeItem getSelectedTreeItem() {
        return mSelectedTreeItem;
    }

    public void addTreeItem(IEntityDataType operand) {
        getSelectedTreeItem().mChildren.put(getSelectedTreeItem().mChildren.size(), operand.getRuleTreeItem(getSelectedTreeItem().mChildren.size()));
        mTreeListAdapter.notifyDataSetChanged();
        mTreeListAdapter.notifyDataSetInvalidated();
    }

    public void addTreeItem(Integer treeIndex, RuleTreeItem ruleTreeItem) {
        mTreeData.put(treeIndex, ruleTreeItem);
        mTreeListAdapter.notifyDataSetChanged();
        mTreeListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onOperationBuildResult(IRuleOperationBuildListener.RuleOperationSelectionInterface ruleOperationSelectionInterface
            , IRuleOperationBuildListener.RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType operand
            , int operandPosition, RuleOperator ruleOperator) {
        if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.CANCEL) {
            if(mOperationUnderConstruction != null)
                mOperationUnderConstruction.setActive(true);
            return;
        }

        switch(ruleOperationSelectionInterface) {
            case NA:
                break;
            case UNIT:
                if (getSelectedTreeItem() != null) {
                    //Edit selected operation
                    mOperationUnderConstruction = ((RuleEditActivity)getActivity()).getOperationByOperandSourceId(operand.getDataSourceId());
                } else {
                    //Create new operation
                    if(mOperationUnderConstruction == null)
                        mOperationUnderConstruction = new RuleOperation(/*"My operation"*/);
                }

                IEntityDataType oldOperand = mOperationUnderConstruction.getOperand(operandPosition);
                if(oldOperand != null && oldOperand.getSourceType() == EntityDataTypeSource.UNIT)
                    mWidgetProvider.removeItemListener((UnitEntityDataType)oldOperand);
                mOperationUnderConstruction.setOperand(operandPosition, operand);
                mWidgetProvider.addItemListener((UnitEntityDataType) operand);

                if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT)
                    openNewDialogAfterOperandSelection(operandPosition, operand);

                break;
            case NEW_OPERATION:
            case OLD_OPERATION:
            case STATIC:
                if(mOperationUnderConstruction == null)
                    mOperationUnderConstruction = new RuleOperation(/*"My operation"*/);
                mOperationUnderConstruction.setOperand(operandPosition, operand);
                if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT)
                    openNewDialogAfterOperandSelection(operandPosition, operand);
                break;
            case OPERATOR:
                RuleOperation currentOperation;
                if (getSelectedTreeItem() != null) {
                    currentOperation = ((RuleEditActivity)getActivity()).getOperationByOperandSourceId(getSelectedTreeItem().getItemId());
                } else {
                    currentOperation = mOperationUnderConstruction;
                }
                currentOperation.setRuleOperator(ruleOperator);
                if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.NEXT) {
                    openOperationBuilderDialog(operandPosition + 1, mSelectedTreeItem);
                    return;
                }
                break;
        }

        if(ruleOperationDialogButtonInterface == IRuleOperationBuildListener.RuleOperationDialogButtonInterface.DONE) {
            ((RuleEditActivity)getActivity()).getRule().setRuleOperation(mOperationUnderConstruction);
            mOperationUnderConstruction.setActive(true);
            mOperationUnderConstruction.runCalculation();
            updateRuleTree(mOperationUnderConstruction);
        }
    }

    private void openNewDialogAfterOperandSelection(int previousOperandPosition, IEntityDataType operand) {
        if(previousOperandPosition == 0) {
            final List<String> operatorList = mAdapterProvider.getRuleOperatorList(getActivity(),
                    operand,
                    false);
            final OperatorSelectionDialogFragment dialogFragment = OperatorSelectionDialogFragment.newInstance(operand.getDataSourceId(),
                    operand.getSourceType(),
                    "Select an operator",
                    true,
                    operatorList);
            dialogFragment.show(getFragmentManager(), "String_Selection_Dialog_Tag");
        } else if(mOperationUnderConstruction.getRuleOperator() != null && mOperationUnderConstruction.getRuleOperator().supportsMultipleOperations()) {
            openRuleOperandDialogFragment(mOperationUnderConstruction.getOperand(previousOperandPosition + 1), previousOperandPosition + 1, true);
        }
    }

    public void openRuleOperandDialogFragment(IEntityDataType currentOperand, int position, boolean showNextButton) {
        ((RuleEditActivity)getActivity()).setOperandToEdit(currentOperand);
        final RuleOperandDialogFragment dialogFragment = RuleOperandDialogFragment.newInstance(
                position != -1 ? position : 0, showNextButton);
        dialogFragment.show(getFragmentManager(), "Operation_Builder_Tag");
    }

    public void openOperationBuilderDialog(int aOperandposition, RuleTreeItem selectedTreeItem) {
        RuleTreeItem rti = selectedTreeItem;
        if (rti == null && ((RuleEditActivity)getActivity()).getRule().getRuleOperation() != null) {
            Toast.makeText(getActivity(), "Select a target item first.", Toast.LENGTH_SHORT).show();
        } else if (rti == null || rti.getItemType() == RuleTreeItem.ItemType.OPERAND) {
            if(rti == null && mOperationUnderConstruction == null && ((RuleEditActivity)getActivity()).getRule().getRuleOperation() == null) {
                ((RuleEditActivity)getActivity()).setOperandToEdit(null);
                final RuleOperandDialogFragment dialogFragment = RuleOperandDialogFragment.newInstance(0, true);
                dialogFragment.show(getFragmentManager(), "Operation_Builder_Tag");
                return;
            }/* else
                rti = mRule.getRuleOperation().getRuleTreeItem();*///TODO - TA: get current operation and open it up
            mOperationUnderConstruction = rti != null? ((RuleEditActivity)getActivity()).getOperationByOperandSourceId(rti.getItemId()) : mOperationUnderConstruction;
            int operandPosition = aOperandposition;
            if(operandPosition == -1 && mOperationUnderConstruction.getSourceType() != EntityDataTypeSource.OPERATION) {
                operandPosition = rti.getPosition();
            } else if(operandPosition == -1) {
                operandPosition = 0;
            }
            ((RuleEditActivity)getActivity()).setOperationToEdit(mOperationUnderConstruction);
            mOperationUnderConstruction.setActive(false);
            ((RuleEditActivity)getActivity()).setOperandToEdit(mOperationUnderConstruction.getOperand(operandPosition));
            final RuleOperandDialogFragment dialogFragment = RuleOperandDialogFragment.newInstance(
                    operandPosition != -1 ? operandPosition : 0
                    , operandPosition < 1
                            || (operandPosition > 1 && (mOperationUnderConstruction.getRuleOperator() != null
                                && mOperationUnderConstruction.getRuleOperator().supportsMultipleOperations())
                    )
            );
            dialogFragment.show(getFragmentManager(), "Operation_Builder_Tag");
        } else if (rti.getItemType() == RuleTreeItem.ItemType.OPERATOR) {
            //TODO - TA: open an operator selection dialog.
            ((RuleEditActivity)getActivity()).getOperatorBySourceId(rti.getItemId());
        }
    }

    //==============================================================
    //                      T   E   S   T
    //==============================================================

    /*
 * Preparing the list data
 */
    private RuleOperation getInitialTreeOperation() {
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  mRuleOperationProvider.getRuleOperator(Boolean.class, RuleOperatorType.And);
        RuleOperation ron = new RuleOperation(ror, operandList);
        ron.setName("The is the name of the root operation.");

        return ron;
    }

    private void prepareListData(RuleOperation ruleOperationRoot) {
        // Adding tree data
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  mRuleOperationProvider.getRuleOperator(Boolean.class, RuleOperatorType.And);
        RuleOperation ron2 = new RuleOperation(ror, operandList);
        RuleOperation ron3 = new RuleOperation(ror, operandList);
        ron3.setName("The is the name of the ron3 rule.");

        if(mTreeData == null)
            mTreeData = new SparseArray<RuleTreeItem>();

        mTreeData.put(0, ron2.getRuleTreeItem(0));
        mTreeData.put(1, ron3.getRuleTreeItem(1));

        ((RuleEditActivity)getActivity()).getRule().setRuleOperation(ron3);
    }

    /*
     * Preparing the list data
     */
    private void prepareMockedListData() {
        // Adding child data
        HashMap<Integer, RuleTreeItem> top250 = new HashMap<Integer, RuleTreeItem>();
        top250.put(0, new RuleTreeItem(0, "The Shawshank Redemption", RuleTreeItem.ItemType.OPERAND));
        top250.put(1, new RuleTreeItem(1, "The Godfather", RuleTreeItem.ItemType.OPERAND));
        top250.put(2, new RuleTreeItem(2, "The Godfather: Part II", RuleTreeItem.ItemType.OPERAND));
        top250.put(3, new RuleTreeItem(3, "Pulp Fiction", RuleTreeItem.ItemType.OPERAND));
        top250.put(4, new RuleTreeItem(4, "The Good, the Bad and the Ugly", RuleTreeItem.ItemType.OPERAND));
        top250.put(5, new RuleTreeItem(5, "The Dark Knight", RuleTreeItem.ItemType.OPERAND));
        top250.put(6, new RuleTreeItem(6, "12 Angry Men", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> thirdLevel = new HashMap<Integer, RuleTreeItem>();
        thirdLevel.put(0, new RuleTreeItem(0, "Närkontakt", RuleTreeItem.ItemType.OPERAND));
        thirdLevel.put(1, new RuleTreeItem(1, "Av tredje", RuleTreeItem.ItemType.OPERAND));
        thirdLevel.put(2, new RuleTreeItem(2, "Graden", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> nowShowing = new HashMap<Integer, RuleTreeItem>();
        nowShowing.put(0, new RuleTreeItem(0, "The Conjuring", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(1, new RuleTreeItem(1, "Despicable Me 2", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(2, new RuleTreeItem(2, "Turbo", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(3, new RuleTreeItem(3, "Grown Ups 2", RuleTreeItem.ItemType.OPERAND, thirdLevel));
        nowShowing.put(4, new RuleTreeItem(4, "Red 2", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(5, new RuleTreeItem(5, "The Wolverine", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> comingSoon = new HashMap<Integer, RuleTreeItem>();

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  mRuleOperationProvider.getRuleOperator(Boolean.class, RuleOperatorType.And);
        RuleOperation ron2 = new RuleOperation(ror, operandList);
        RuleOperation ron3 = new RuleOperation(ror, operandList);
        ron3.setName("The is the name of the ron3 rule.");

        comingSoon.put(0, new RuleTreeItem(0, "Men In Black", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(1, ron2.getRuleTreeItem(1));
        comingSoon.put(2, new RuleTreeItem(2, "The Spectacular Now", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(3, ron3.getRuleTreeItem(3));
        comingSoon.put(4, new RuleTreeItem(4, "Europa Report", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(5, new RuleTreeItem(5, "Apocalypse Now", RuleTreeItem.ItemType.OPERAND));

        if(mTreeData == null)
            mTreeData = new SparseArray<RuleTreeItem>();

        mTreeData.put(0, new RuleTreeItem(0, "Top 250", RuleTreeItem.ItemType.OPERAND, top250));
        mTreeData.put(1, new RuleTreeItem(1, "Now Showing", RuleTreeItem.ItemType.OPERAND, nowShowing));
        mTreeData.put(2, new RuleTreeItem(2, "Coming Soon...", RuleTreeItem.ItemType.OPERAND, comingSoon));
    }

    private RuleOperation getRuleOperation() {
        final HABApplication application = (HABApplication) getActivity().getApplication();
        OpenHABWidget widget = mWidgetProvider.getWidgetByID("demo_1_0");
        return new RuleOperation(mRuleOperationProvider.getRuleOperator(widget, RuleOperatorType.Equal), getOperandsAsList3(2));
    }

    private List<IEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(mUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Toilet_1")));
                operands.add(mUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Corridor_1")));
                break;

            case 2:
                //Number
                operands.add(mUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("demo_1_0")));//"demo_1_0"
                operands.add(mUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("demo_1_0")));//ekafallettest_1
                break;
        }

        return operands;
    }

}
