package com.zenit.habclient.rule;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RuleActivity extends Activity implements ActionBar.TabListener, IRuleActivity
        , RuleOperandDialogFragment.RuleOperationBuildListener, RuleActionDialogFragment.RuleActionBuildListener {

    Rule mRule;
    RuleOperation mOperationUnderConstruction;
    RuleAction mActionUnderConstruction;

    private ViewFlipper mTabViewFlipper;
    private RuleTreeItem mSelectedTreeItem;

    ExpandableMultiLevelGroupAdapter mTreeListAdapter;
    ExpandableListView mTreeView;
    HashMap<Integer, RuleTreeItem> mTreeData;

    ListView mListView;
    ArrayAdapter<RuleAction> mListAdapter;
    private int mSelectedActionPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycle.RuleActivity", "onCreate()");

        setContentView(R.layout.activity_rule);
        EditText mRuleNameView = (EditText) findViewById(R.id.rule_name_textview);
        mTabViewFlipper = (ViewFlipper) findViewById(R.id.rule_viewFlipper);
        mTreeView = (ExpandableListView) findViewById(R.id.rule_if_tree);
        mListView = (ListView) findViewById(R.id.rule_then_list);
        mTreeData = new HashMap<Integer, RuleTreeItem>();

        if(mRule == null) {
            mRule = new Rule(getApplication().getApplicationContext());
            mRule.setName("Initial rule name");
        }

        Locale l = Locale.getDefault();
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.rule_tab_if).toUpperCase(l)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.rule_tab_then).toUpperCase(l)).setTabListener(this));

        String[] list = new String[] {"hello", "how", "are", "you"};//TODO - TA: implement the listView and its adapter
        mListAdapter = new ArrayAdapter<RuleAction>(this, android.R.layout.simple_list_item_1, mRule.mActions);
        mListView.setAdapter(mListAdapter);

        mTreeListAdapter = new ExpandableMultiLevelGroupAdapter(this, mTreeData);
        mTreeView.setAdapter(mTreeListAdapter);

        RuleOperation initialRootOperation = getInitialTreeOperation();
        getRule().setRuleOperation(initialRootOperation);
//            mRuleNameView.setText(getRuleName());
        updateRuleTree(initialRootOperation);

        mRuleNameView.setText(getRuleName());
        TextWatcher ruleNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(this,"Open RuleOperandDialogFragment", Toast.LENGTH_SHORT).show();
//                final RuleOperandDialogFragment dialogFragment = new RuleOperandDialogFragment(null);
//                dialogFragment.show(getFragmentManager(), "Operation_Builder_Tag");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("RuleActivity.ruleNameTextWatcher", "onTextChanged = " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("RuleActivity.ruleNameTextWatcher", "afterTextChanged = " + s.toString());
                setRuleName(s.toString());
            }
        };
        mRuleNameView.addTextChangedListener(ruleNameTextWatcher);

        mTabViewFlipper.setDisplayedChild(0);

        // Listview Group click listener
        mTreeView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();


                Toast.makeText(getApplicationContext(), "OnGroupClickListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        mTreeData.get(Integer.valueOf(groupPosition)).mName
                                + " : "
                                + mTreeData.get(Integer.valueOf(groupPosition)).mChildren.get(Integer.valueOf(childPosition)).mName, Toast.LENGTH_SHORT
                )
                        .show();
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
                Toast.makeText(getApplicationContext(),
                        mTreeData.get(Integer.valueOf(groupPosition)).getItemId() + " OnGroupExpandListener",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        mTreeView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        mTreeData.get(Integer.valueOf(groupPosition)).getItemId() + " OnGroupCollapseListener",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mTreeView.setOnLongClickListener(new ExpandableListView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "ExpandableListView.OnLongClick", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mTreeView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        position + " OnItemLongClickListener", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedActionPosition = position;
                mListView.setItemChecked(position, true);
//                    mListView.getFocusables(position);
//                    mListView.setSelection(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle.RuleActivity", "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle.RuleActivity", "onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mTabViewFlipper.getDisplayedChild() == 0) {
            RuleTreeItem rti = getSelectedTreeItem();
            if (rti == null && getRule().getRuleOperation() != null) {
                Toast.makeText(this, "Select a target item first.", Toast.LENGTH_SHORT).show();
            } else if (rti == null || rti.getItemType() == RuleTreeItem.ItemType.OPERAND) {
                RuleOperation selectedOperation = getOperationByOperandSourceId(getSelectedTreeItem().getItemId());
                int operandPosition = selectedOperation.getOperandIndexBySourceId(getSelectedTreeItem().getItemId());
                final RuleOperandDialogFragment dialogFragment = new RuleOperandDialogFragment(
                        selectedOperation.getOperandBySourceId(getSelectedTreeItem().getItemId())
                        , operandPosition != -1 ? operandPosition : 0);
                dialogFragment.show(getFragmentManager(), "Operation_Builder_Tag");
            } else if (rti.getItemType() == RuleTreeItem.ItemType.OPERATOR) {
                //TODO - TA: open an operator selection dialog.
                getOperatorBySourceId(rti.getItemId());
            }
        } else {
            if(mSelectedActionPosition > -1)
                mActionUnderConstruction = mListAdapter.getItem(mSelectedActionPosition);
            else
                mActionUnderConstruction = new RuleAction(RuleActionType.COMMAND);//TODO - TA: Let the user decide if COMMAND or MESSAGE

            if (mActionUnderConstruction == null ) {
                Toast.makeText(this, "Select a target item first.", Toast.LENGTH_SHORT).show();
            } else {
                final RuleActionDialogFragment dialogFragment = new RuleActionDialogFragment(mActionUnderConstruction);
                dialogFragment.show(getFragmentManager(), "Action_Builder_Tag");
            }
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private IEntityDataType getEntityDataBySourceId(String dataSourceId) {
        if(dataSourceId == null) return null;
        //TODO - TA: implement
        return null;
    }

    private RuleOperator getOperatorBySourceId(String dataSourceId) {
        //TODO - TA: implement
        return null;
    }

    @Override
    public String getRuleName() {
        return mRule.getName();
    }

    @Override
    public void setRuleName(String name) {
        mRule.setName(name);
    }

    @Override
    public Rule getRule() {
        return mRule;
    }

    @Override
    public void setRule(Rule rule) {
        mRule = rule;
        //TODO - TA: Update Table, List and UI
    }

    @Override
    public void onOperationBuildResult(RuleOperationSelectionInterface ruleOperationSelectionInterface
            , RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface, IEntityDataType operand
            , int operandPosition, RuleOperator ruleOperator) {
        if(ruleOperationDialogButtonInterface == RuleOperationDialogButtonInterface.CANCEL)
            return;

        if (mTabViewFlipper.getDisplayedChild() == 0){
            //IF tab
            switch(ruleOperationSelectionInterface) {
                case UNIT:
                    if (getSelectedTreeItem() != null) {
                        RuleOperation selectedOperation = getOperationByOperandSourceId(operand.getDataSourceId());
                        selectedOperation.setOperand(operandPosition, operand);
                    } else {
                        mOperationUnderConstruction.setOperand(operandPosition, operand);
//                    addTreeItem(Integer.valueOf(mTreeData.size()), operand.getRuleTreeItem(mTreeData.size()));
                        //TODO - TA: update item tree if "DONE"
                    }
                    //TODO - TA: decide if a new dialog shall be shown or item tree shall be updated.
                    break;
                case NEW_RULE:
                case OLD_RULE:
                case STATIC:
                    if(getSelectedTreeItem() != null)
                        addTreeItem(operand);
                    else
                        addTreeItem(Integer.valueOf(mTreeData.size()), operand.getRuleTreeItem(mTreeData.size()));
                    break;
                case OPERATOR: mOperationUnderConstruction.setRuleOperator(ruleOperator);
            }
        } else {
            //THEN tab
            if(ruleOperationSelectionInterface == RuleOperationSelectionInterface.UNIT && mActionUnderConstruction != null) {
                if (operandPosition == 0) {
                    mActionUnderConstruction.setTargetOpenHABItemName(operand.getName());
                } else
                    mActionUnderConstruction.setSourceOpenHABItemName(operand.getName());

                final RuleActionDialogFragment dialogFragment = new RuleActionDialogFragment(mActionUnderConstruction);
                dialogFragment.show(getFragmentManager(), "Action_Builder_Tag");
            }
        }
    }

    public RuleOperation getOperationByOperandSourceId(String dataSourceId) {
        HashMap<String, RuleOperation> entityMap = getRule().getRuleOperation().getRuleOperationHash();
        return entityMap.get(dataSourceId);
    }

    @Override
    public void onActionBuildResult(RuleActionDialogButtonInterface ruleActionDialogButtonInterface,
                                    String actionID,
                                    RuleActionValueType ruleActionValueType,
                                    String valueAsText) {
        if (ruleActionDialogButtonInterface == RuleActionDialogButtonInterface.CANCEL)
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
            mRule.getActions().add(mActionUnderConstruction);
        } else {
            int actionIndex = mRule.getActions().indexOf(/*mListView.getSelectedItem()*/mListAdapter.getItem(mSelectedActionPosition));
            mRule.getActions().remove(actionIndex);
            mRule.getActions().add(actionIndex, mActionUnderConstruction);
        }
        mListAdapter.notifyDataSetChanged();
    }

    public RuleTreeItem getSelectedTreeItem() {
        return mSelectedTreeItem;
    }

    public void updateRuleTree(RuleOperation ruleOperationRoot) {
        if(mTreeData == null)
            mTreeData = new HashMap<Integer, RuleTreeItem>();

        mTreeData.clear();
        mTreeData.put(Integer.valueOf(0), ruleOperationRoot.getRuleTreeItem(0));
        mTreeListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mTabViewFlipper.setDisplayedChild(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }







    /*
     * Preparing the list data
     */
    private RuleOperation getInitialTreeOperation() {
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  HABApplication.getRuleOperationProvider(getApplicationContext()).getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        RuleOperation ron = new RuleOperation(ror, operandList);
        ron.setName("The is the name of the root operation.");

        return ron;
    }

    private void prepareListData(RuleOperation ruleOperationRoot) {
        // Adding tree data
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  HABApplication.getRuleOperationProvider(getApplicationContext()).getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        RuleOperation ron2 = new RuleOperation(ror, operandList);
        RuleOperation ron3 = new RuleOperation(ror, operandList);
        ron3.setName("The is the name of the ron3 rule.");

        if(mTreeData == null)
            mTreeData = new HashMap<Integer, RuleTreeItem>();

        mTreeData.put(Integer.valueOf(0), ron2.getRuleTreeItem(0));
        mTreeData.put(Integer.valueOf(1), ron3.getRuleTreeItem(1));

        getRule().setRuleOperation(ron3);
    }

    /*
     * Preparing the list data
     */
    private void prepareMockedListData() {
        // Adding child data
        HashMap<Integer, RuleTreeItem> top250 = new HashMap<Integer, RuleTreeItem>();
        top250.put(Integer.valueOf(0), new RuleTreeItem(0, "The Shawshank Redemption", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(1), new RuleTreeItem(1, "The Godfather", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(2), new RuleTreeItem(2, "The Godfather: Part II", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(3), new RuleTreeItem(3, "Pulp Fiction", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(4), new RuleTreeItem(4, "The Good, the Bad and the Ugly", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(5), new RuleTreeItem(5, "The Dark Knight", RuleTreeItem.ItemType.OPERAND));
        top250.put(Integer.valueOf(6), new RuleTreeItem(6, "12 Angry Men", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> thirdLevel = new HashMap<Integer, RuleTreeItem>();
        thirdLevel.put(Integer.valueOf(0), new RuleTreeItem(0, "Närkontakt", RuleTreeItem.ItemType.OPERAND));
        thirdLevel.put(Integer.valueOf(1), new RuleTreeItem(1, "Av tredje", RuleTreeItem.ItemType.OPERAND));
        thirdLevel.put(Integer.valueOf(2), new RuleTreeItem(2, "Graden", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> nowShowing = new HashMap<Integer, RuleTreeItem>();
        nowShowing.put(Integer.valueOf(0), new RuleTreeItem(0, "The Conjuring", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(Integer.valueOf(1), new RuleTreeItem(1, "Despicable Me 2", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(Integer.valueOf(2), new RuleTreeItem(2, "Turbo", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(Integer.valueOf(3), new RuleTreeItem(3, "Grown Ups 2", RuleTreeItem.ItemType.OPERAND, thirdLevel));
        nowShowing.put(Integer.valueOf(4), new RuleTreeItem(4, "Red 2", RuleTreeItem.ItemType.OPERAND));
        nowShowing.put(Integer.valueOf(5), new RuleTreeItem(5, "The Wolverine", RuleTreeItem.ItemType.OPERAND));

        HashMap<Integer, RuleTreeItem> comingSoon = new HashMap<Integer, RuleTreeItem>();

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(getRuleOperation());
        operandList.add(getRuleOperation());
        RuleOperator ror =  HABApplication.getRuleOperationProvider(getApplicationContext()).getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        RuleOperation ron2 = new RuleOperation(ror, operandList);
        RuleOperation ron3 = new RuleOperation(ror, operandList);
        ron3.setName("The is the name of the ron3 rule.");

        comingSoon.put(Integer.valueOf(0), new RuleTreeItem(0, "Men In Black", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(Integer.valueOf(1), ron2.getRuleTreeItem(1));
        comingSoon.put(Integer.valueOf(2), new RuleTreeItem(2, "The Spectacular Now", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(Integer.valueOf(3), ron3.getRuleTreeItem(3));
        comingSoon.put(Integer.valueOf(4), new RuleTreeItem(4, "Europa Report", RuleTreeItem.ItemType.OPERAND));
        comingSoon.put(Integer.valueOf(5), new RuleTreeItem(5, "Apocalypse Now", RuleTreeItem.ItemType.OPERAND));

        if(mTreeData == null)
            mTreeData = new HashMap<Integer, RuleTreeItem>();

        mTreeData.put(Integer.valueOf(0), new RuleTreeItem(0, "Top 250", RuleTreeItem.ItemType.OPERAND, top250));
        mTreeData.put(Integer.valueOf(1), new RuleTreeItem(1, "Now Showing", RuleTreeItem.ItemType.OPERAND, nowShowing));
        mTreeData.put(Integer.valueOf(2), new RuleTreeItem(2, "Coming Soon...", RuleTreeItem.ItemType.OPERAND, comingSoon));
    }

    private RuleOperation getRuleOperation() {
        RuleOperationProvider rop = HABApplication.getRuleOperationProvider(getApplicationContext());
        OpenHABWidget widget = HABApplication.getOpenHABWidgetProvider2().getWidgetByID("demo_1_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(2));
        return roA;
    }

    private List<IEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(getUnitEntityDataType(HABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Toilet_1")));
                operands.add(getUnitEntityDataType(HABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Corridor_1")));
                break;

            case 2:
                //Number
                operands.add(getUnitEntityDataType(HABApplication.getOpenHABWidgetProvider2().getWidgetByID("demo_1_0")));//"demo_1_0"
                operands.add(getUnitEntityDataType(HABApplication.getOpenHABWidgetProvider2().getWidgetByID("demo_1_0")));//ekafallettest_1
                break;
        }

        return operands;
    }

    private UnitEntityDataType getUnitEntityDataType(OpenHABWidget openHABWidget) {
        UnitEntityDataType rue = null;

        switch(openHABWidget.getItem().getType()) {
            case Contact:
            case Switch:
                Boolean aBoolean;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aBoolean = null;
                else
                    aBoolean = openHABWidget.getItem().getState().equalsIgnoreCase("On");

                rue = new UnitEntityDataType<Boolean>(openHABWidget.getItem().getName(), aBoolean, null)//TODO - Change <null> to a listener provider
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "On": "Off";//TODO - Language independent
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        return Boolean.valueOf(input);
                    }

                    @Override
                    public Map<String, Boolean> getStaticValues() {
                        return null;
                    }
                };
                rue.setDataSourceId(openHABWidget.getId());
                break;
            case Number:
            case Dimmer:
                Double aNumber;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aNumber = null;
                else
                    aNumber = Double.valueOf(openHABWidget.getItem().getState());

                rue = new UnitEntityDataType<Double>(openHABWidget.getItem().getName(), aNumber, null)//TODO - Change <null> to a listener provider
                {
                    public String getFormattedString(){
                        return mValue.toString();
                    }

                    @Override
                    public Double valueOf(String input) {
                        return Double.valueOf(input);
                    }

                    @Override
                    public Map<String, Double> getStaticValues() {
                        return null;
                    }
                };
                rue.setDataSourceId(openHABWidget.getId());
                break;
        }

        return rue;
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


//    public static class RuleFragment extends Fragment implements IRuleFragment {
//
//        public RuleFragment(int displayViewIndex) {
//            mDisplayViewIndex = displayViewIndex;
//        }
//

//
//    }

}
