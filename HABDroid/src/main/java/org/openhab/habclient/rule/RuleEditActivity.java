package org.openhab.habclient.rule;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleEditActivity;
import org.openhab.domain.rule.Rule;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.RuleOperator;
import org.openhab.habdroid.R;

import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

public class RuleEditActivity extends Activity implements IRuleEditActivity, ActionBar.TabListener {
    private enum RuleActivityMode {
        OPERATION_EDITOR,
        ACTION_LIST
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Rule mRule;
    private RuleActivityMode mRuleActivityMode;
    private RuleOperandDialogFragment.RuleOperationBuildListener mRuleOperationBuildListener;
    @Inject IOpenHABWidgetControl mWidgetControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_edit);

        if(mRule == null) {
            mRule = new Rule(mWidgetControl);
            mRule.setName("Initial rule name");
            mRule.setEnabled(true);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.rule_edit_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle.RuleEditActivity", "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle.RuleEditActivity", "onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
            mRuleActivityMode = tab.getPosition() == 0? RuleActivityMode.OPERATION_EDITOR : RuleActivityMode.ACTION_LIST;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private IEntityDataType getEntityDataBySourceId(String dataSourceId) {
        if(dataSourceId == null) return null;
        //TODO - TA: implement
        return null;
    }

    public RuleOperator getOperatorBySourceId(String dataSourceId) {
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

    public RuleOperation getOperationByOperandSourceId(String dataSourceId) {
        HashMap<String, RuleOperation> entityMap = getRule().getRuleOperation().getRuleOperationHash();
        return entityMap.get(dataSourceId);
    }

    public RuleOperandDialogFragment.RuleOperationBuildListener getRuleOperationBuildListener() {
        return mRuleOperationBuildListener;
    }

    public void setRuleOperationBuildListener(RuleOperandDialogFragment.RuleOperationBuildListener mRuleOperationBuildListener) {
        this.mRuleOperationBuildListener = mRuleOperationBuildListener;
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private RuleEditActivity mActivity;

        public SectionsPagerAdapter(FragmentManager fm, RuleEditActivity activity) {
            super(fm);
            mActivity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RuleOperationFragment.newInstance();
                case 1:
                    return RuleActionFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Support for 2 pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.rule_tab_if).toUpperCase(l);
                case 1:
                    return getString(R.string.rule_tab_then).toUpperCase(l);
            }
            return null;
        }
    }
}
