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
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleListActivity extends ListActivity {

    @Inject IRuleProvider mRuleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<Rule> listAdapter = new ArrayAdapter<Rule>(this, android.R.layout.simple_list_item_1, mRuleProvider.getUserRules());
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Rule itemValue = (Rule) l.getItemAtPosition(position);

        Intent intent = new Intent(this, RuleEditActivity.class);
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
                addRule();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRule() {
        final Intent intent = new Intent(this, RuleEditActivity.class);
        intent.putExtra(Rule.ARG_RULE_ID, "");
        startActivity(intent);
    }

    private void editRule() {
        if(getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
            Toast.makeText(this, "No selected rule", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = new Intent(this, RuleEditActivity.class);
        intent.putExtra(Rule.ARG_RULE_ID, getSelectedItemId());
        startActivity(intent);
    }
}
