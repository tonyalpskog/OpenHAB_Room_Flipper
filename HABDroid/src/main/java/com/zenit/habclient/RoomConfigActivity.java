package com.zenit.habclient;

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

import org.openhab.habdroid.R;

import java.util.Locale;

public class RoomConfigActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private Room mConfigRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_config);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        .setOnTabChangedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);

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
    protected void onResume() {
        super.onResume();
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
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition() == 0)
            HABApplication.setAppMode(ApplicationMode.RoomEdit);
        else
            HABApplication.setAppMode(ApplicationMode.UnitPlacement);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public boolean isNewRoom() {
        return ((HABApplication)getApplication()).getConfigRoom() == null;
    }

    public Room getConfigRoom() {
        if(mConfigRoom != null)
            return mConfigRoom;

        Room roomToBeEdited = ((HABApplication)getApplication()).getConfigRoom();

        if(roomToBeEdited == null)
            mConfigRoom = ((HABApplication)getApplication()).getRoomProvider().createNewRoom();
        else if(roomToBeEdited != null)
            mConfigRoom = roomToBeEdited;

        return mConfigRoom;
    }

    public void setConfigRoom(Room configRoom) {
        mConfigRoom = configRoom;
    }

//    @Override
//    public void onTabChanged(String tabId) {
//        Log.d(TAG, "onTabChanged(): tabId=" + tabId);
//        if (TAB_WORDS.equals(tabId)) {
//            updateTab(tabId, R.id.tab_1);
//            mCurrentTab = 0;
//            return;
//        }
//        if (TAB_NUMBERS.equals(tabId)) {
//            updateTab(tabId, R.id.tab_2);
//            mCurrentTab = 1;
//            return;
//        }
//    }
//
//    private void updateTab(String tabId, int placeholder) {
//        FragmentManager fm = getFragmentManager();
//        if (fm.findFragmentByTag(tabId) == null) {
//            fm.beginTransaction()
//                    .replace(placeholder, new MyListFragment(tabId), tabId)
//                    .commit();
//        }
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private RoomConfigActivity mActivity;

        public SectionsPagerAdapter(FragmentManager fm, RoomConfigActivity activity) {
            super(fm);
            mActivity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RoomConfigFragment(((HABApplication)getApplication()).getRoomProvider(), mActivity);
                case 1:
                    return new UnitPlacementFragment(mActivity);
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_room_config).toUpperCase(l);
                case 1:
                    return getString(R.string.title_unit_placement).toUpperCase(l);
            }
            return null;
        }
    }

//    /**
//     * A placeholder fragment containing a simple view.
//    */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_room_config, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

}
