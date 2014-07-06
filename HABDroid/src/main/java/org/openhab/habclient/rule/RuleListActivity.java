package org.openhab.habclient.rule;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.Rule;
import org.openhab.domain.rule.RuleActionType;
import org.openhab.domain.user.User;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.duenndns.ssl.MemorizingTrustManager;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleListActivity extends ListActivity {

//    public static final int RULE_REQUEST_CODE = 4321;
    @Inject IRuleProvider mRuleProvider;
    private String mTemporaryHardCodedUserId = "Admin123";//TODO - TA: Replace this with data from user module/provider

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.inject(this);
//        setContentView(R.layout.activity_rule_list);
//        if(mRuleProvider.getUserRules(mTemporaryHardCodedUserId).size() < 1)
//            mRuleProvider.saveRule(new Rule("<No data>", null),mTemporaryHardCodedUserId );
        List<Rule> ruleList = mRuleProvider.getUserRules(mTemporaryHardCodedUserId);
        ArrayAdapter<Rule> adapter = new ArrayAdapter<Rule>(this, android.R.layout.simple_list_item_1, ruleList != null? ruleList : new ArrayList<Rule>());
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Rule itemValue = (Rule) l.getItemAtPosition(position);

        Intent intent = new Intent(this, RuleEditActivity.class);
        intent.putExtra(User.ARG_USER_ID, mTemporaryHardCodedUserId);
        intent.putExtra(Rule.ARG_RULE_ID, itemValue.getRuleId());
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
                intent = new Intent(this, RuleEditActivity.class);
                intent.putExtra(User.ARG_USER_ID, mTemporaryHardCodedUserId);
                intent.putExtra(Rule.ARG_RULE_ID, getSelectedItemId());
                startActivity(intent);
//                startActivityForResult(intent, RULE_REQUEST_CODE);
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
