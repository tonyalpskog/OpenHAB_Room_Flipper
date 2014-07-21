package org.openhab.habclient.rule;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.operations.bool.BooleanAndRuleOperation;
import org.openhab.domain.rule.operations.RuleOperation;

import java.util.Arrays;

public class RuleOperationFragment extends ListFragment implements OnRuleOperationUpdatedListener {
    private RuleOperationFragmentListener mListener;
    
    public static RuleOperationFragment newInstance() {
        return new RuleOperationFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (RuleOperationFragmentListener) activity;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadOperationTask().execute();

        mListener.addOnRuleOperationUpdatedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mListener.removeOnRuleOperationUpdatedListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        switch (position) {
            case 0:
                mListener.onChangeLeftEntity();
                break;
            case 1:
                mListener.onShowChangeOperationDialog();
                break;
            case 2:
                mListener.onChangeRightEntity();
                break;
        }
    }

    private String getUndefinedString() {
        return "<Undefined>";
    }

    private String getStringFromEntityDataType(IEntityDataType<?> dataType) {
        if(dataType == null)
            return getUndefinedString();

        return dataType.toString();
    }

    private void displayRuleOperation(RuleOperation<?> ruleOperation) {
        if(ruleOperation == null)
            ruleOperation = new BooleanAndRuleOperation();

        String firstRow = getStringFromEntityDataType(ruleOperation.getLeft());
        String secondRow = ruleOperation.getName();
        String thirdRow = getStringFromEntityDataType(ruleOperation.getRight());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                Arrays.asList(firstRow, secondRow, thirdRow));
        setListAdapter(adapter);
    }

    @Override
    public void onRuleOperationUpdated(RuleOperation<?> ruleOperation) {
        displayRuleOperation(ruleOperation);
    }

    private class LoadOperationTask extends AsyncTask<Void,Void,RuleOperation<?>> {
        @Override
        protected RuleOperation<?> doInBackground(Void... params) {
            return mListener.getRuleOperation();
        }

        @Override
        protected void onPostExecute(RuleOperation<?> ruleOperation) {
            super.onPostExecute(ruleOperation);

            displayRuleOperation(ruleOperation);
        }
    }

    public interface RuleOperationFragmentListener {
        void onShowChangeOperationDialog();
        void onChangeLeftEntity();
        void onChangeRightEntity();

        RuleOperation<?> getRuleOperation();

        void addOnRuleOperationUpdatedListener(OnRuleOperationUpdatedListener listener);

        void removeOnRuleOperationUpdatedListener(OnRuleOperationUpdatedListener listener);
    }
}
