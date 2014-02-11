package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABMainActivity;

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
    private HABApplication mApplication;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_flipper, container, false);
        mRoomLabel = (TextView) rootView.findViewById(R.id.room_flipper_section_label);
        mRoomViewFlipper = (RoomFlipper) rootView.findViewById(R.id.flipper);

        mRoomViewFlipper.setDisplayedChild(0);//Show middle image as initial image
        mRoomViewFlipper.setGestureListener(new GestureListener(rootView, true));
        mRoomViewFlipper.setOnRoomShiftListener(this);
        mRoomViewFlipper.setRoomFlipperAdapter(new RoomFlipperAdapter(rootView.getContext(), mApplication.getFlipperRoom()), mApplication);

        mRoomLabel.setText(mApplication.getFlipperRoom().getName());

        setHasOptionsMenu(true);

        ((HABApplication) mApplication).getSpeechResultAnalyzer().setRoomFlipper(mRoomViewFlipper);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        mApplication = (HABApplication) activity.getApplication();
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
                Room roomToEdit = mApplication.getFlipperRoom();
                Log.d("Edit Room", "onOptionsItemSelected() - Edit room action on room<" + roomToEdit.getId() + ">");
                mApplication.setConfigRoom(roomToEdit);

                Intent intent = new Intent(getActivity(), RoomConfigActivity.class);
                startActivity(intent);
//                ((MainActivity) getActivity()).selectNavigationDrawerItem(2);//TODO - Use enum as fragment identifier.
                return true;
            case R.id.action_add_room_from_flipper:
                Log.d("Add Room", "onOptionsItemSelected() - Add room");
                mApplication.setConfigRoom(mApplication.getRoomProvider().createNewRoom());

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
                widgetListIntent.putExtra("pageUrl", "openhab://sitemaps/demo/" + mApplication.getFlipperRoom().getRoomWidget().getLinkedPage().getId());

                // Start launch activity
                mApplication.getApplicationContext().startActivity(widgetListIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HABApplication.setAppMode(ApplicationMode.RoomFlipper);
        mRoomViewFlipper.getCurrentUnitContainer().redrawAllUnits();
    }

    @Override
    public boolean onRoomShift(Gesture gesture, Room room) {
        Log.d("Flip Room", "onRoomShift() - Shifted to room<" + room.getId() + ">");
        mRoomLabel.setText(room.getName());
        mApplication.setFlipperRoom(room);
        return false;
    }
}

