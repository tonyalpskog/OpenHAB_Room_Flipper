package org.openhab.habclient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IRestCommunication;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.rule.RuleEditActivity;
import org.openhab.habdroid.R;

import java.util.EnumSet;

import javax.inject.Inject;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private UnitPlacementFragment configFragment = null;
    private RoomFlipperFragment flipperFragment = null;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Inject ICommandAnalyzer mSpeechResultAnalyzer;
    @Inject
    IRestCommunication mRestCommunication;
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject
    IApplicationModeProvider mApplicationModeProvider;
    @Inject ICommandAnalyzer mCommandAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        InjectUtils.inject(this);

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

        if(mRestCommunication != null) {
            mRestCommunication.requestOpenHABSitemap((String) null);
            for (OpenHABWidget widget : mWidgetProvider.getWidgetList(EnumSet.of(OpenHABWidgetType.Group, OpenHABWidgetType.SitemapText))) {
                if (widget == null)
                    Log.e(HABApplication.getLogTag(), "Got OpenHABWidget = NULL from OpenHABWidgetProvider in " + HABApplication.getLogTag(2));
                else if (widget.hasChildren())
                    mRestCommunication.requestOpenHABSitemap(widget);
            }
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
                        .replace(R.id.container, RoomConfigFragment.newInstance())
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES/*LANGUAGE_MODEL_FREE_FORM*/);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_navigate_rooms));
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            final HABApplication application = ((HABApplication) getApplication());

//            mSpeechResultAnalyzer.setRoomFlipper(mRoomFlipper);
//            mSpeechResultAnalyzer.analyze(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS), mApplicationModeProvider.getAppMode());

            mCommandAnalyzer.analyzeCommand(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS), mApplicationModeProvider.getAppMode());
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
