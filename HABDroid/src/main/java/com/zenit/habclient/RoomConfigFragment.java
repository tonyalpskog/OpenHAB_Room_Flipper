package com.zenit.habclient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RoomConfigFragment extends Fragment {

    private final String TAG = "RoomConfigFragment";
    private RoomProvider mRoomProvider;
    private Room mCurrentRoom;
    private Button mSaveButton;
    private EditText mRoomNameText;
    private final Room NULL_ROOM = new Room("<Undefined room>", null);
    HashMap<Direction, Spinner> mSpinnerHashMap;

//    private OnFragmentInteractionListener mListener;

    public static RoomConfigFragment newInstance(RoomProvider roomProvider, Room roomToBeEdited) {
        RoomConfigFragment fragment = new RoomConfigFragment(roomProvider, roomToBeEdited);
        return fragment;
    }

    public RoomConfigFragment(RoomProvider roomProvider, Room roomToBeEdited) {
        mRoomProvider = roomProvider;
        mCurrentRoom = roomToBeEdited;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("LifeCycle", "RoomConfigFragment.onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config_room, container, false);
        mRoomNameText = (EditText) view.findViewById(R.id.edittext_room_name);
        Spinner leftRoomSpinner = (Spinner) view.findViewById(R.id.spinner_left_room);
        Spinner rightRoomSpinner = (Spinner) view.findViewById(R.id.spinner_right_room);
        Spinner upRoomSpinner = (Spinner) view.findViewById(R.id.spinner_up_room);
        Spinner downRoomSpinner = (Spinner) view.findViewById(R.id.spinner_down_room);
        Spinner aboveRoomSpinner = (Spinner) view.findViewById(R.id.spinner_above_room);
        Spinner belowRoomSpinner = (Spinner) view.findViewById(R.id.spinner_below_room);
        mSaveButton = (Button) view.findViewById(R.id.room_edit_save_button);

        mRoomNameText.setText(mCurrentRoom.getName() != null? mCurrentRoom.getName(): "");

        List<Room> roomArrayList = new ArrayList<Room>(mRoomProvider.roomHash.size());
        Iterator iterator = mRoomProvider.roomHash.values().iterator();
        while(iterator.hasNext()) {
            roomArrayList.add((Room) iterator.next());
        }
        roomArrayList.add(NULL_ROOM);

        //Inline sort the list of rooms.
        Collections.sort(roomArrayList, new Comparator<Room>() {
            public int compare(Room lhs, Room rhs) {
                return (lhs.getName().compareTo(rhs.getName()));
            }
        });

        ArrayAdapter<Room> roomSpinnerAdapter = new ArrayAdapter<Room>(this.getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, roomArrayList);
        roomSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        leftRoomSpinner.setAdapter(roomSpinnerAdapter);
        rightRoomSpinner.setAdapter(roomSpinnerAdapter);
        upRoomSpinner.setAdapter(roomSpinnerAdapter);
        downRoomSpinner.setAdapter(roomSpinnerAdapter);
        aboveRoomSpinner.setAdapter(roomSpinnerAdapter);
        belowRoomSpinner.setAdapter(roomSpinnerAdapter);

        if(mSpinnerHashMap == null)
            mSpinnerHashMap = new HashMap<Direction, Spinner>();
        mSpinnerHashMap.put(Direction.LEFT, leftRoomSpinner);
        mSpinnerHashMap.put(Direction.RIGHT, rightRoomSpinner);
        mSpinnerHashMap.put(Direction.UP, upRoomSpinner);
        mSpinnerHashMap.put(Direction.DOWN, downRoomSpinner);
        mSpinnerHashMap.put(Direction.ABOVE, aboveRoomSpinner);
        mSpinnerHashMap.put(Direction.BELOW, belowRoomSpinner);

        iterator = mSpinnerHashMap.keySet().iterator();
        while(iterator.hasNext()) {
            Direction direction = (Direction) iterator.next();
            Spinner spinner = mSpinnerHashMap.get(direction);
            if(mCurrentRoom.getRoomByAlignment(direction) != null) {
                spinner.setSelection(roomSpinnerAdapter.getPosition(mCurrentRoom.getRoomByAlignment(direction)));
            } else { spinner.setSelection(roomSpinnerAdapter.getPosition(NULL_ROOM)); }
        }

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
        Iterator iterator = mSpinnerHashMap.keySet().iterator();
        while(iterator.hasNext()) {
            Direction direction = (Direction) iterator.next();
            Spinner spinner = mSpinnerHashMap.get(direction);
            if((Room) spinner.getSelectedItem() == NULL_ROOM)
                mCurrentRoom.setAlignment(null, direction);
            else
                mCurrentRoom.setAlignment((Room) spinner.getSelectedItem(), direction);
        }

        if(mRoomNameText.getText().toString().length() > 0)
            mCurrentRoom.setName(mRoomNameText.getText().toString());
    }
}
