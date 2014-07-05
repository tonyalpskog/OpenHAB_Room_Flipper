package org.openhab.habclient.rule;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.Rule;
import org.openhab.habclient.InjectUtils;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleListActivity extends ListActivity {

    @Inject IRuleProvider mRuleProvider;
    private String mTemporaryHardCodedUserId = "Admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.inject(this);
//        setContentView(R.layout.activity_rule_list);
        ArrayAdapter<Rule> adapter = new ArrayAdapter<Rule>(this, android.R.layout.simple_list_item_1, mRuleProvider.getUserRules(mTemporaryHardCodedUserId));
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Rule itemValue = (Rule) l.getItemAtPosition(position);

        //Open RuleEditActivity...
    }
}
