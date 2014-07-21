package org.openhab.habclient.rule;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.Rule;
import org.openhab.domain.rule.RuleAction;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.operations.date.AfterDateTimeRuleOperation;
import org.openhab.domain.rule.operations.date.AfterOrEqualDateTimeRuleOperation;
import org.openhab.domain.rule.operations.date.BeforeDateTimeRuleOperation;
import org.openhab.domain.rule.operations.date.BeforeOrEqualDateTimeRuleOperation;
import org.openhab.domain.rule.operations.number.LessOrEqualNumberRuleOperation;
import org.openhab.domain.rule.operations.number.LessThanNumberRuleOperation;
import org.openhab.domain.rule.operations.number.MoreOrEqualNumberRuleOperation;
import org.openhab.domain.rule.operations.number.MoreThanNumberRuleOperation;
import org.openhab.domain.rule.operations.NotEqualRuleOperation;
import org.openhab.domain.rule.operations.RuleOperation;
import org.openhab.domain.rule.operations.bool.BooleanAndRuleOperation;
import org.openhab.domain.rule.operations.EqualRuleOperation;
import org.openhab.domain.rule.operations.bool.BooleanOrRuleOperation;
import org.openhab.domain.user.AccessModifier;
import org.openhab.habclient.InjectUtils;
import org.openhab.habdroid.R;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

public class RuleEditActivity extends Activity implements ActionBar.TabListener,
        RuleOperationFragment.RuleOperationFragmentListener,
        RuleActionFragment.RuleActionFragmentListener,
        RuleOperandDialogFragment.OnRuleOperationChangedListener {
    private final Set<OnRuleOperationUpdatedListener> mRuleOperationUpdatedListeners = new HashSet<OnRuleOperationUpdatedListener>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Rule mRule;
    private RuleAction mActionUnderConstruction;

    @Inject IRuleProvider mRuleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_edit);

        mRuleOperationUpdatedListeners.clear();

        InjectUtils.inject(this);

        String ruleId = getIntent().getExtras().getString(Rule.ARG_RULE_ID);

        mRule = mRuleProvider.getUserRule(ruleId);
        if(mRule == null) {
            mRule = mRuleProvider.createNewRule(AccessModifier.ReadOnly ,"Initial rule name");
            mRule.setEnabled(true);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

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
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void setActionUnderConstruction(RuleAction action) {
        mActionUnderConstruction = action;
    }

    public RuleAction getActionUnderConstruction() {
        return mActionUnderConstruction;
    }

    @Override
    public void onShowChangeOperationDialog() {
        final RuleOperandDialogFragment fragment = RuleOperandDialogFragment.newInstance(RuleOperatorType.And);
        fragment.show(getFragmentManager(), "changeOperationDialog");
    }

    @Override
    public void onChangeLeftEntity() {
        //TODO: show dialog and store result in provider
    }

    @Override
    public void onChangeRightEntity() {
        //TODO: show dialog and store result in provider
    }

    @Override
    public void onRuleOperationChanged(RuleOperatorType ruleOperatorType) {
        IEntityDataType left = null;
        IEntityDataType right = null;
        RuleOperation<?> op = mRule.getRuleOperation();
        if(op != null) {
            left = op.getLeft();
            right = op.getRight();
        }

        op = createOperation(ruleOperatorType);
        op.setLeft(left);
        op.setRight(right);
        mRule.setRuleOperation(op);
        notifyRuleOperationUpdated();
    }

    private RuleOperation<?> createOperation(RuleOperatorType type) {
        switch (type) {
            case Or:
                return new BooleanOrRuleOperation();
            case And:
                return new BooleanAndRuleOperation();
            case After:
                return new AfterDateTimeRuleOperation();
            case AfterOrEqual:
                return new AfterOrEqualDateTimeRuleOperation();
            case Before:
                return new BeforeDateTimeRuleOperation();
            case BeforeOrEqual:
                return new BeforeOrEqualDateTimeRuleOperation();
            case Equal:
                return new EqualRuleOperation();
            case LessOrEqual:
                return new LessOrEqualNumberRuleOperation();
            case LessThan:
                return new LessThanNumberRuleOperation();
            case MoreThan:
                return new MoreThanNumberRuleOperation();
            case MoreOrEqual:
                return new MoreOrEqualNumberRuleOperation();
            case NotEqual:
                return new NotEqualRuleOperation<Object>();
            default:
                throw new IllegalStateException(type + " not supported");
        }
    }

    @Override
    public RuleOperation<?> getRuleOperation() {
        return mRule.getRuleOperation();
    }

    @Override
    public List<RuleAction> getRuleActions() {
        return mRule.getActions();
    }

    @Override
    public void setRuleName(String name) {
        mRule.setName(name);
    }

    @Override
    public String getRuleName() {
        return mRule.getName();
    }

    private void notifyRuleOperationUpdated() {
        synchronized (mRuleOperationUpdatedListeners) {
            for(OnRuleOperationUpdatedListener listener : mRuleOperationUpdatedListeners) {
                listener.onRuleOperationUpdated(mRule.getRuleOperation());
            }
        }
    }

    @Override
    public void addOnRuleOperationUpdatedListener(OnRuleOperationUpdatedListener listener) {
        synchronized (mRuleOperationUpdatedListeners) {
            mRuleOperationUpdatedListeners.add(listener);
        }
    }

    @Override
    public void removeOnRuleOperationUpdatedListener(OnRuleOperationUpdatedListener listener) {
        synchronized (mRuleOperationUpdatedListeners) {
            mRuleOperationUpdatedListeners.remove(listener);
        }
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private RuleOperationFragment mRuleOperationFragment;
        private RuleActionFragment mRuleActionFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(mRuleOperationFragment == null)
                            mRuleOperationFragment = RuleOperationFragment.newInstance();
                    return mRuleOperationFragment;
                case 1:
                    if(mRuleActionFragment == null)
                        mRuleActionFragment = RuleActionFragment.newInstance();
                    return mRuleActionFragment;
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
