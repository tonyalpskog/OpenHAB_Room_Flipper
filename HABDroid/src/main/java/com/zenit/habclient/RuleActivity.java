package com.zenit.habclient;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.openhab.habdroid.R;

public class RuleActivity extends Activity implements ActionBar.TabListener, IRuleActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    RuleFragment mTabFragment;
    Rule mRule;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycle.RuleActivity", "onCreate()");

        if(mRule == null) {
            mRule = new Rule();
            mRule.setName("Initial rule name");
        }

        setContentView(R.layout.activity_rule);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rule, menu);
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
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public int getDisplayViewIndex() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public String getRuleName() {
        return mRule.getName();
    }

    @Override
    public void setRuleName(String name) {
        mRule.setName(name);
    }

    //      SectionsPagerAdapter

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mTabFragment = RuleFragment.newInstance(position);

            mTabFragment.setDisplayView(position);

            return mTabFragment;
        }

        @Override
        public int getCount() {
            // ViewFlipper has two pages
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

    private static class RuleFragment extends Fragment {

        private ViewFlipper mTabViewFlipper;
        private EditText mRuleNameView;
        private int mDisplayViewIndex = 0;
        private IRuleActivity mRuleActivity;
        private Context mContext;

        ExpandableMultiLevelGroupAdapter listAdapter;
        ExpandableListView mTreeView;
//        List<String> listDataHeader;
        HashMap<Integer, RuleTreeItem> mTreeData;

        public static RuleFragment newInstance(int displayViewIndex) {
            RuleFragment fragment = new RuleFragment(displayViewIndex);
            return fragment;
        }

        public RuleFragment(int displayViewIndex) {
            mDisplayViewIndex = displayViewIndex;
            // preparing list data
            prepareListData();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            Log.d("LifeCycle.RuleFragment", "setUserVisibleHint(" + isVisibleToUser + ")");
            if(isVisibleToUser && mRuleNameView != null && mRuleActivity != null) {
                Log.d("RuleFragment.setUserVisibleHint()", "Set rule name text = '" + mRuleActivity.getRuleName() + "'");
                mRuleNameView.setText(mRuleActivity.getRuleName());
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Log.d("LifeCycle.RuleFragment", "onAttach()");
            mRuleActivity = (IRuleActivity) activity;
            mContext = activity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            Log.d("LifeCycle.RuleFragment", "onCreateView()");
            View rootView = inflater.inflate(R.layout.fragment_rule, container, false);
            mRuleNameView = (EditText) rootView.findViewById(R.id.rule_name_textview);
            mTabViewFlipper = (ViewFlipper) rootView.findViewById(R.id.rule_viewflipper);
            mTreeView = (ExpandableListView) rootView.findViewById(R.id.rule_if_tree);

            mRuleNameView.setText(mRuleActivity.getRuleName());
            mRuleNameView.addTextChangedListener(ruleNameTextWatcher);
            mTabViewFlipper.setDisplayedChild(mDisplayViewIndex);

            listAdapter = new ExpandableMultiLevelGroupAdapter(mContext, mTreeData);

            // setting list adapter
            mTreeView.setAdapter(listAdapter);

            // Listview Group click listener
            mTreeView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    // Toast.makeText(getApplicationContext(),
                    // "Group Clicked " + listDataHeader.get(groupPosition),
                    // Toast.LENGTH_SHORT).show();
                    RuleOperationProvider rop = ((HABApplication)getActivity().getApplication()).getRuleOperationProvider();
                    HashMap<RuleOperatorType, RuleOperator<Number>> ruleOperators = (HashMap<RuleOperatorType, RuleOperator<Number>>) rop.mOperatorHash.get(Number.class);
                    RuleOperator<Number> ro =  ruleOperators.get(RuleOperatorType.Equal);

                    Toast.makeText(mContext,
                    "Integer 10 equals 10 = " + ro.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)),
                    Toast.LENGTH_LONG).show();

                    ro =  ruleOperators.get(RuleOperatorType.Between);
                    Toast.makeText(mContext,
                            "Float 10 between 10 and 13 = " + ro.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)),
                            Toast.LENGTH_LONG).show();

                    ro =  ruleOperators.get(RuleOperatorType.Within);
                    Toast.makeText(mContext,
                            "Float 10 within 10 and 13 = " + ro.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)),
                            Toast.LENGTH_LONG).show();

                    HashMap<RuleOperatorType, RuleOperator<Boolean>> ruleOperators2 = (HashMap<RuleOperatorType, RuleOperator<Boolean>>) rop.mOperatorHash.get(Boolean.class);
                    RuleOperator<Boolean> ro2 =  ruleOperators2.get(RuleOperatorType.And);

                    Toast.makeText(mContext,
                            "Boolean false AND false = " + ro2.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)),
                            Toast.LENGTH_LONG).show();

                    return false;
                }
            });

            // Listview Group expanded listener
            mTreeView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    Toast.makeText(mContext,
                            mTreeData.get(Integer.valueOf(groupPosition)).mName + " Expanded",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Listview Group collasped listener
            mTreeView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    Toast.makeText(mContext,
                            mTreeData.get(Integer.valueOf(groupPosition)).mName + " Collapsed",
                            Toast.LENGTH_SHORT).show();

                }
            });

            // Listview on child click listener
            mTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // TODO Auto-generated method stub
                    Toast.makeText(
                            mContext,
                            mTreeData.get(Integer.valueOf(groupPosition)).mName
                                    + " : "
                                    + mTreeData.get(Integer.valueOf(groupPosition)).mChildren.get(Integer.valueOf(childPosition)).mName, Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d("LifeCycle.RuleFragment", "onResume()");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d("LifeCycle.RuleFragment", "onPause()");
        }

        public void setDisplayView(int position) {
            mDisplayViewIndex = position;

            if(mTabViewFlipper != null)
                mTabViewFlipper.setDisplayedChild(position);
        }

        private TextWatcher ruleNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("RuleFragment.ruleNameTextWatcher", "onTextChanged = " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("RuleFragment.ruleNameTextWatcher", "afterTextChanged = " + s.toString());
                mRuleActivity.setRuleName(s.toString());
            }
        };

        /*
         * Preparing the list data
         */
        private void prepareListData() {
            // Adding child data
            HashMap<Integer, RuleTreeItem> top250 = new HashMap<Integer, RuleTreeItem>();
            top250.put(Integer.valueOf(0), new RuleTreeItem(0, "The Shawshank Redemption"));
            top250.put(Integer.valueOf(1), new RuleTreeItem(1, "The Godfather"));
            top250.put(Integer.valueOf(2), new RuleTreeItem(2, "The Godfather: Part II"));
            top250.put(Integer.valueOf(3), new RuleTreeItem(3, "Pulp Fiction"));
            top250.put(Integer.valueOf(4), new RuleTreeItem(4, "The Good, the Bad and the Ugly"));
            top250.put(Integer.valueOf(5), new RuleTreeItem(5, "The Dark Knight"));
            top250.put(Integer.valueOf(6), new RuleTreeItem(6, "12 Angry Men"));

            HashMap<Integer, RuleTreeItem> thirdLevel = new HashMap<Integer, RuleTreeItem>();
            thirdLevel.put(Integer.valueOf(0), new RuleTreeItem(0, "Närkontakt"));
            thirdLevel.put(Integer.valueOf(1), new RuleTreeItem(1, "Av tredje"));
            thirdLevel.put(Integer.valueOf(2), new RuleTreeItem(2, "Graden"));

            HashMap<Integer, RuleTreeItem> nowShowing = new HashMap<Integer, RuleTreeItem>();
            nowShowing.put(Integer.valueOf(0), new RuleTreeItem(0, "The Conjuring"));
            nowShowing.put(Integer.valueOf(1), new RuleTreeItem(1, "Despicable Me 2"));
            nowShowing.put(Integer.valueOf(2), new RuleTreeItem(2, "Turbo"));
            nowShowing.put(Integer.valueOf(3), new RuleTreeItem(3, "Grown Ups 2", thirdLevel));
            nowShowing.put(Integer.valueOf(4), new RuleTreeItem(4, "Red 2"));
            nowShowing.put(Integer.valueOf(5), new RuleTreeItem(5, "The Wolverine"));

            HashMap<Integer, RuleTreeItem> comingSoon = new HashMap<Integer, RuleTreeItem>();
            comingSoon.put(Integer.valueOf(0), new RuleTreeItem(0, "2 Guns"));
            comingSoon.put(Integer.valueOf(1), new RuleTreeItem(1, "The Smurfs 2"));
            comingSoon.put(Integer.valueOf(2), new RuleTreeItem(2, "The Spectacular Now"));
            comingSoon.put(Integer.valueOf(3), new RuleTreeItem(3, "The Canyons"));
            comingSoon.put(Integer.valueOf(4), new RuleTreeItem(4, "Europa Report"));

            if(mTreeData == null)
                mTreeData = new HashMap<Integer, RuleTreeItem>();

            mTreeData.put(Integer.valueOf(0), new RuleTreeItem(0, "Top 250", top250));
            mTreeData.put(Integer.valueOf(1), new RuleTreeItem(1, "Now Showing", nowShowing));
            mTreeData.put(Integer.valueOf(2), new RuleTreeItem(2, "Coming Soon...", comingSoon));
        }
    }

}
