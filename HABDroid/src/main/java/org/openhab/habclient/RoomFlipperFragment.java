package org.openhab.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.INotificationHost;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IRestCommunication;
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.Room;
import org.openhab.domain.model.RoomConfigEvent;
import org.openhab.domain.model.SitemapUpdateEvent;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.domain.user.User;
import org.openhab.habclient.rule.RuleListActivity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABMainActivity;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class RoomFlipperFragment extends Fragment implements RoomFlipper.OnRoomShiftListener, ICommandAnalyzer.OnShowRoomListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
    */
    private static final String ARG_SECTION_NUMBER = "section_number";
    final String TAG = "RoomFlipperFragment";
    private RoomFlipper mRoomViewFlipper;
    private TextView mRoomLabel;

    @Inject INotificationHost mNotificationHost;
    @Inject ICommandAnalyzer mSpeechResultAnalyzer;
    @Inject IRoomProvider mRoomProvider;
    @Inject IApplicationModeProvider mApplicationModeProvider;
    @Inject IRoomDataContainer mRoomDataContainer;
    @Inject IRoomImageProvider mRoomImageProvider;
    @Inject IRestCommunication mRestCommunication;
    @Inject IOpenHABWidgetProvider mWidgetProvider;//TODO - temporary

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RoomFlipperFragment newInstance(int sectionNumber) {
        RoomFlipperFragment fragment = new RoomFlipperFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public RoomFlipperFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);

//        mNotificationHost = new WearCommandHost(mApplication, mSpeechResultAnalyzer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_flipper, container, false);
        mRoomLabel = (TextView) rootView.findViewById(R.id.room_flipper_section_label);
        mRoomViewFlipper = (RoomFlipper) rootView.findViewById(R.id.flipper);
        mRoomViewFlipper.setDisplayedChild(0);//Show middle image as initial image
        mRoomViewFlipper.setGestureListener(new GestureListener(rootView, true));
        mRoomViewFlipper.setOnRoomShiftListener(this);
        mRoomViewFlipper.setRoomFlipperAdapter(new RoomFlipperAdapter(mRoomDataContainer.getFlipperRoom(), mRoomImageProvider));

        mRoomLabel.setText(getRoomLabel());

        setHasOptionsMenu(true);

        mSpeechResultAnalyzer.setOnShowRoomListener(this);
        EventBus.getDefault().register(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //TODO - Create dynamic menu
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.room_flipper_default, menu);

        //menu.findItem(R.id.action_remove).setVisible()
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_room_from_flipper:
                Room roomToEdit = mRoomDataContainer.getFlipperRoom();
                Log.d("Edit Room", "onOptionsItemSelected() - Edit room action on room<" + roomToEdit.getId() + ">");
                mRoomDataContainer.setConfigRoom(roomToEdit);

                Intent intent = new Intent(getActivity(), RoomConfigActivity.class);
                startActivity(intent);
//                ((MainActivity) getActivity()).selectNavigationDrawerItem(2);//TODO - Use enum as fragment identifier.
                return true;
            case R.id.action_add_room_from_flipper:
                Log.d("Add Room", "onOptionsItemSelected() - Add room");
                mRoomDataContainer.setConfigRoom(mRoomProvider.createNewRoom());
                
                intent = new Intent(getActivity(), RoomConfigActivity.class);
                startActivity(intent);
//                ((MainActivity) getActivity()).selectNavigationDrawerItem(2);//TODO - Use enum as fragment identifier.
                return true;
            case R.id.action_speak_room_from_flipper:
                ((MainActivity) getActivity()).startVoiceRecognition(mRoomViewFlipper);
                return true;
            case R.id.action_go_to_list_view:
                // Get launch intent for application
                Intent widgetListIntent = new Intent(getActivity().getApplicationContext(), OpenHABMainActivity.class);
                widgetListIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                widgetListIntent.setAction("SHOW_PAGE_AS_LIST");//TODO - Centralize this parameter
                try {
                    widgetListIntent.putExtra("pageUrl", "openhab://sitemaps/demo/" + mRoomDataContainer.getFlipperRoom().getRoomWidget().getLinkedPage().getId());
                } catch (NullPointerException e) {
                    return true;//Probably because no OpenHAB group was attached to the room.
                }
                // Start launch activity
                getActivity().startActivity(widgetListIntent);

                return true;
            case R.id.action_start_wear_app:
//                mNotificationHost.startSession("OpenHab", mWidgetProvider.getWidgetByID("Light_GF_Kitchen_Ceiling"), "Kitchen Dishwasher leakage detected");
                mNotificationHost.startSession("Person", new User(), "Hi, this is a test message");
                return true;
            case R.id.action_open_rules:
                Intent i = new Intent(getActivity(), RuleListActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mApplicationModeProvider.setAppMode(ApplicationMode.RoomFlipper);
        mRoomLabel.setText(getRoomLabel());
        mRoomViewFlipper.getCurrentUnitContainer().redrawAllUnits();
        mNotificationHost.registerReceivers();
        requestRemoteRoomUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mNotificationHost.unregisterReceivers();
        cancelRemoteRoomUpdate();
    }

    @Override
    public boolean onRoomShift(Gesture gesture, Room room) {
        Log.d("Flip Room", "onRoomShift() - Shifted to room<" + room.getId() + ">");
        mRoomDataContainer.setFlipperRoom(room);
        mRoomLabel.setText(getRoomLabel());
        requestRemoteRoomUpdate();
        return false;
    }

    private String getRoomLabel() {
        if(mRoomDataContainer.getFlipperRoom().getRoomWidget() == null)
            return this.getString(R.string.missing_openhab_group_for_room);
        return mRoomDataContainer.getFlipperRoom().getName();
    }

    private void requestRemoteRoomUpdate() {
        cancelRemoteRoomUpdate();
        if(mRoomDataContainer.getFlipperRoom() != null) {
            mRestCommunication.requestOpenHABSitemap(mRoomDataContainer.getFlipperRoom().getRoomWidget(), false, TAG);
            mRestCommunication.requestOpenHABSitemap(mRoomDataContainer.getFlipperRoom().getRoomWidget(), true, TAG);//used
        }
    }

    private void cancelRemoteRoomUpdate() {
        mRestCommunication.cancelRequests(TAG);
    }

    @Override
    public void onShowRoom(Room room) {
        mRoomViewFlipper.showRoom(room);
    }

    public void onEvent(SitemapUpdateEvent updateEvent){
        if(updateEvent.isUpdateFinished())
            mRoomViewFlipper.getCurrentUnitContainer().redrawAllUnits();
    }

    public void onEvent(RoomConfigEvent roomConfigEvent){
        if(mRoomViewFlipper.getCurrentUnitContainer().getRoom().equals(roomConfigEvent.getRoom()) && roomConfigEvent.getEventType() == RoomConfigEvent.EventType.ConfigurationChanged)
            mRoomViewFlipper.getCurrentUnitContainer().setRoom(roomConfigEvent.getRoom());//Force an update.
    }
}

