package org.openhab.habclient.rule;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.Rule;
import org.openhab.domain.user.User;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.dagger.Dagger_RuleListComponent;
import org.openhab.habclient.dagger.RuleListComponent;
import org.openhab.habdroid.R;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleListActivity extends ListActivity {

//    public static final int RULE_REQUEST_CODE = 4321;
    private String mTemporaryHardCodedUserId = "Admin123";//TODO - TA: Replace this with data from user module/provider
    private ArrayAdapter<Rule> mListAdapter;

    @Inject IRuleProvider mRuleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RuleListComponent component = Dagger_RuleListComponent.builder()
                .appComponent(((HABApplication) getApplication()).appComponent())
                .build();
        component.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mListAdapter.notifyDataSetChanged();
        mListAdapter = new ArrayAdapter<Rule>(this, android.R.layout.simple_list_item_1, mRuleProvider.getUserRules(mTemporaryHardCodedUserId));
        setListAdapter(mListAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Rule itemValue = (Rule) l.getItemAtPosition(position);

        Intent intent = new Intent(this, RuleEditActivity.class);
        intent.putExtra(User.ARG_USER_ID, mTemporaryHardCodedUserId);
        intent.putExtra(Rule.ARG_RULE_ID, itemValue.getRuleId().toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rule_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_rule:
                Intent intent = new Intent(this, RuleEditActivity.class);
                intent.putExtra(User.ARG_USER_ID, mTemporaryHardCodedUserId);
                intent.putExtra(Rule.ARG_RULE_ID, "");
                startActivity(intent);
//                startActivityForResult(intent, RULE_REQUEST_CODE);
                break;
            case R.id.action_edit_rule:
                if(getSelectedItemId() == AdapterView.INVALID_ROW_ID)
                    Toast.makeText(this, "No selected rule", Toast.LENGTH_SHORT).show();
                else {
                    intent = new Intent(this, RuleEditActivity.class);
                    intent.putExtra(User.ARG_USER_ID, mTemporaryHardCodedUserId);
                    intent.putExtra(Rule.ARG_RULE_ID, getSelectedItemId());
                    startActivity(intent);
//                startActivityForResult(intent, RULE_REQUEST_CODE);
                }
                break;
            case R.id.action_delete_rule:
                //TODO - TA: Implement this.
                Toast.makeText(this, "Not implemented.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RULE_REQUEST_CODE && resultCode == RESULT_OK) {
//            //TODO - TA: Save Rule
//        }
//    }

}
