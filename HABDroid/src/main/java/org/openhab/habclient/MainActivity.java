package org.openhab.habclient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.rule.RuleEditActivity;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;

import java.util.EnumSet;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private UnitPlacementFragment configFragment = null;
    private RoomFlipperFragment flipperFragment = null;

    private ICommandAnalyzer mSpeechResultAnalyzer;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment newFragment = null;

        HABApplication.getRestCommunication().requestOpenHABSitemap(this,  (String) null);
        for(OpenHABWidget widget : HABApplication.getOpenHABWidgetProvider2().getWidgetList(EnumSet.of(OpenHABWidgetType.Group, OpenHABWidgetType.SitemapText))) {
            if(widget == null)
                Log.e(HABApplication.getLogTag(), "Got OpenHABWidget = NULL from OpenHABWidgetProvider in " + HABApplication.getLogTag(2));
            else if(widget.hasChildren())
                HABApplication.getRestCommunication().requestOpenHABSitemap(this,  widget);
        }

        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RoomFlipperFragment.newInstance(position + 1))
                        .commit();
                    break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, UnitPlacementFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RoomConfigFragment.newInstance(((HABApplication) getApplication()).getRoomProvider()))
                        .commit();
                break;
            default:
                Intent i = new Intent(this, RuleEditActivity.class);
                startActivity(i);
                break;

        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_room_flipper);
                break;
            case 2:
                mTitle = getString(R.string.title_unit_placement);
                break;
            case 3:
                mTitle = getString(R.string.title_room_config);
                break;
            case 4:
                mTitle = getString(R.string.title_rule_config);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    public void selectNavigationDrawerItem(int itemIndex) {
        mNavigationDrawerFragment.selectItem(itemIndex);
    }

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;


    private RoomFlipper mRoomFlipper;

    public void startVoiceRecognition(RoomFlipper roomFlipper) {
        mRoomFlipper = roomFlipper;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            if(mSpeechResultAnalyzer == null)
                mSpeechResultAnalyzer = new CommandAnalyzer(((HABApplication)getApplication()).getRoomProvider(), HABApplication.getOpenHABWidgetProvider2(), getApplicationContext());

            mSpeechResultAnalyzer.setRoomFlipper(mRoomFlipper);
            mSpeechResultAnalyzer.analyze(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS), HABApplication.getAppMode());

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
