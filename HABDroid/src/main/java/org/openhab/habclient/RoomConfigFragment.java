package org.openhab.habclient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RoomConfigFragment extends Fragment {
    private static final String TAG = "RoomConfigFragment";
    private RoomProvider mRoomProvider;
    private Room mCurrentRoom;
    private Button mSaveButton;
    private EditText mRoomNameText;
    private Spinner mHABGroupSpinner;
    private OpenHABWidget mNullGroupWidget;
    private Room mNullRoom;
    private HashMap<Direction, Spinner> mSpinnerHashMap;
    private IOpenHABWidgetProvider mOpenHABWidgetProvider;
    private IRestCommunication mRestCommunication;

    public static RoomConfigFragment newInstance() {
        return new RoomConfigFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final HABApplication application = (HABApplication) getActivity().getApplication();
        mNullGroupWidget = new OpenHABWidget(application.getLogger(), application.getColorParser());
        mNullRoom = new Room(null, "<Undefined room>", null, application.getLogger(),
                application.getColorParser(), application.getOpenHABWidgetProvider());//TA: TODO - Fix name problem. (now sitemapID)
        mOpenHABWidgetProvider = application.getOpenHABWidgetProvider();
        mRestCommunication = application.getRestCommunication();
        mRoomProvider = application.getRoomProvider();
        mCurrentRoom = ((RoomConfigActivity)getActivity()).getConfigRoom();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("LifeCycle", "RoomConfigFragment.onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_config, container, false);
        mRoomNameText = (EditText) view.findViewById(R.id.edittext_room_alias);
        mHABGroupSpinner = (Spinner) view.findViewById(R.id.spinner_hab_group);
        Spinner upRoomSpinner = (Spinner) view.findViewById(R.id.spinner_up_room);
        Spinner upRightRoomSpinner = (Spinner) view.findViewById(R.id.spinner_up_right_room);
        Spinner rightRoomSpinner = (Spinner) view.findViewById(R.id.spinner_right_room);
        Spinner downRightRoomSpinner = (Spinner) view.findViewById(R.id.spinner_down_right_room);
        Spinner downRoomSpinner = (Spinner) view.findViewById(R.id.spinner_down_room);
        Spinner downLeftRoomSpinner = (Spinner) view.findViewById(R.id.spinner_down_left_room);
        Spinner leftRoomSpinner = (Spinner) view.findViewById(R.id.spinner_left_room);
        Spinner upLeftRoomSpinner = (Spinner) view.findViewById(R.id.spinner_up_left_room);
        Spinner aboveRoomSpinner = (Spinner) view.findViewById(R.id.spinner_above_room);
        Spinner belowRoomSpinner = (Spinner) view.findViewById(R.id.spinner_below_room);
        mSaveButton = (Button) view.findViewById(R.id.room_edit_save_button);

        mRoomNameText.setText(mCurrentRoom.getName() != null? mCurrentRoom.getName(): "");

        List<OpenHABWidget> habGroupArrayList = mOpenHABWidgetProvider.getWidgetList(EnumSet.of(OpenHABWidgetType.Group, OpenHABWidgetType.SitemapText));
        if(habGroupArrayList.size() == 0)
            Log.e(HABApplication.getLogTag(), "No OpenHABWidget groups found in OpenHABWidgetProvider.");

        mNullGroupWidget.setLabel("<Undefined HAB group>");
        habGroupArrayList.add(mNullGroupWidget);

        //Create adapter for HAB Group spinner
        ArrayAdapter<OpenHABWidget> habGroupSpinnerAdapter = new ArrayAdapter<OpenHABWidget>(this.getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, habGroupArrayList);
        habGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Create adapter for Room spinners
        List<Room> roomArrayList = new ArrayList<Room>(mRoomProvider.roomHash.size());
        Iterator iterator = mRoomProvider.roomHash.values().iterator();
        while(iterator.hasNext()) {
            Room room = (Room) iterator.next();

            //TA: TODO - Load the whole sitemap to ensure that all groups are loaded.
            if(room.getGroupWidgetId() == null) {
                OpenHABWidget groupWidget = mOpenHABWidgetProvider.getWidgetByID(room.getRoomWidget().getId());
                mRestCommunication.requestOpenHABSitemap(groupWidget);
            }

            roomArrayList.add(room);
        }
        roomArrayList.add(mNullRoom);

        //Inline sort the list of rooms.
        Collections.sort(roomArrayList, new Comparator<Room>() {
            public int compare(Room lhs, Room rhs) {
                return (lhs.getName().compareTo(rhs.getName()));
            }
        });

        ArrayAdapter<Room> roomSpinnerAdapter = new ArrayAdapter<Room>(this.getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, roomArrayList);

        roomSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set adapters for spinners
        mHABGroupSpinner.setAdapter(habGroupSpinnerAdapter);

        upRoomSpinner.setAdapter(roomSpinnerAdapter);
        upRightRoomSpinner.setAdapter(roomSpinnerAdapter);
        rightRoomSpinner.setAdapter(roomSpinnerAdapter);
        downRightRoomSpinner.setAdapter(roomSpinnerAdapter);
        downRoomSpinner.setAdapter(roomSpinnerAdapter);
        downLeftRoomSpinner.setAdapter(roomSpinnerAdapter);
        leftRoomSpinner.setAdapter(roomSpinnerAdapter);
        upLeftRoomSpinner.setAdapter(roomSpinnerAdapter);
        aboveRoomSpinner.setAdapter(roomSpinnerAdapter);
        belowRoomSpinner.setAdapter(roomSpinnerAdapter);

        //Set selection for spinners
        if(mCurrentRoom.getGroupWidgetId() == null || mCurrentRoom.getGroupWidgetId().isEmpty())
            mHABGroupSpinner.setSelection(habGroupSpinnerAdapter.getPosition(mNullGroupWidget));
        else
            mHABGroupSpinner.setSelection(habGroupSpinnerAdapter.getPosition(mOpenHABWidgetProvider.getWidgetByID(mCurrentRoom.getGroupWidgetId())));

        if(mSpinnerHashMap == null)
            mSpinnerHashMap = new HashMap<Direction, Spinner>();
        mSpinnerHashMap.put(Direction.UP, upRoomSpinner);
        mSpinnerHashMap.put(Direction.UP_RIGHT, upRightRoomSpinner);
        mSpinnerHashMap.put(Direction.RIGHT, rightRoomSpinner);
        mSpinnerHashMap.put(Direction.DOWN_RIGHT, downRightRoomSpinner);
        mSpinnerHashMap.put(Direction.DOWN, downRoomSpinner);
        mSpinnerHashMap.put(Direction.DOWN_LEFT, downLeftRoomSpinner);
        mSpinnerHashMap.put(Direction.LEFT, leftRoomSpinner);
        mSpinnerHashMap.put(Direction.UP_LEFT, upLeftRoomSpinner);
        mSpinnerHashMap.put(Direction.ABOVE, aboveRoomSpinner);
        mSpinnerHashMap.put(Direction.BELOW, belowRoomSpinner);

        iterator = mSpinnerHashMap.keySet().iterator();
        while(iterator.hasNext()) {
            Direction direction = (Direction) iterator.next();
            Spinner spinner = mSpinnerHashMap.get(direction);
            if(mCurrentRoom.getRoomByAlignment(direction) != null) {
                spinner.setSelection(roomSpinnerAdapter.getPosition(mCurrentRoom.getRoomByAlignment(direction)));
            } else { spinner.setSelection(roomSpinnerAdapter.getPosition(mNullRoom)); }
        }

        mHABGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRestCommunication.requestOpenHABSitemap((OpenHABWidget) mHABGroupSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSaveButton.setOnClickListener(buttonClickListener);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LifeCycle", "RoomConfigFragment.onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle", "RoomConfigFragment.onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle", "RoomConfigFragment.onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LifeCycle", "RoomConfigFragment.onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("LifeCycle", "RoomConfigFragment.onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "RoomConfigFragment.onDestroy()");
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("LifeCycle", "RoomConfigFragment.onAttach()");
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("LifeCycle", "RoomConfigFragment.onDetach()");
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == mSaveButton.getId()) {
                saveRoomConfig();
            }
        }
    };

    private void saveRoomConfig() {
        Log.d(TAG, "saveRoomConfig()");

        if(mHABGroupSpinner.getSelectedItem() == mNullGroupWidget) {
            Toast.makeText(getActivity(),  "Unsuccessful! HAB Group item must be selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasAlignment = false;
        Iterator iterator = mSpinnerHashMap.keySet().iterator();
        while(iterator.hasNext()) {
            Direction direction = (Direction) iterator.next();
            Spinner spinner = mSpinnerHashMap.get(direction);
            if(spinner.getSelectedItem() == mNullRoom)
                mCurrentRoom.setAlignment(null, direction);
            else {
                mCurrentRoom.setAlignment((Room) spinner.getSelectedItem(), direction);
                hasAlignment = true;
            }
        }

        if(!hasAlignment) {
            Toast.makeText(getActivity(),  "Unsuccessful! At least one room alignment must be selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        mCurrentRoom.setGroupWidgetId(((OpenHABWidget) mHABGroupSpinner.getSelectedItem()).getId());

        if(mRoomNameText.getText().toString().length() > 0)
            mCurrentRoom.setName(mRoomNameText.getText().toString());

        mRoomProvider.saveRoom(mCurrentRoom);
    }
}
