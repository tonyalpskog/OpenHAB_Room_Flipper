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
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.Room;
import org.openhab.domain.wear.IWearCommandHost;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.rule.RuleEditActivity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABMainActivity;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class RoomFlipperFragment extends Fragment implements RoomFlipper.OnRoomShiftListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RoomFlipper mRoomViewFlipper;
    private TextView mRoomLabel;
    @Inject IWearCommandHost mWearCommandHost;

    @Inject ICommandAnalyzer mSpeechResultAnalyzer;
    @Inject
    IRoomProvider mRoomProvider;
    @Inject
    IApplicationModeProvider mApplicationModeProvider;
    @Inject IRoomDataContainer mRoomDataContainer;
    @Inject IRoomImageProvider mRoomImageProvider;

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

//        mWearCommandHost = new WearCommandHost(mApplication, mSpeechResultAnalyzer);
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

        mRoomLabel.setText(mRoomDataContainer.getFlipperRoom().getName());

        setHasOptionsMenu(true);

        mSpeechResultAnalyzer.setRoomFlipper(mRoomViewFlipper);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                widgetListIntent.putExtra("pageUrl", "openhab://sitemaps/demo/" + mRoomDataContainer.getFlipperRoom().getRoomWidget().getLinkedPage().getId());

                // Start launch activity
                getActivity().startActivity(widgetListIntent);

                return true;
            case R.id.action_start_wear_app:
                mWearCommandHost.startSession("Room navigation", "Please, response with the name of a room");
                return true;
            case R.id.action_open_rules:
                Intent i = new Intent(getActivity(), RuleEditActivity.class);
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
        mRoomLabel.setText(mRoomDataContainer.getFlipperRoom().getName());
        mRoomViewFlipper.getCurrentUnitContainer().redrawAllUnits();
        mWearCommandHost.registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWearCommandHost.unregisterReceiver();
    }

    @Override
    public boolean onRoomShift(Gesture gesture, Room room) {
        Log.d("Flip Room", "onRoomShift() - Shifted to room<" + room.getId() + ">");
        mRoomLabel.setText(room.getName());
        mRoomDataContainer.setFlipperRoom(room);
        return false;
    }
}

