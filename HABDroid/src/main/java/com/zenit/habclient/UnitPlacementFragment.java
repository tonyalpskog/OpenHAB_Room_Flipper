package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2013.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A placeholder fragment containing a simple view.
 */
public class UnitPlacementFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private View fragmentView;
    private UnitContainerView roomView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UnitPlacementFragment newInstance(int sectionNumber, Room room) {
        UnitPlacementFragment fragment = new UnitPlacementFragment(room);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UnitPlacementFragment(Room room) {
        Log.d("LifeCycle", "UnitPlacementFragment(" + (getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : "?") + ") <constructor>");
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.d("LifeCycle", "UnitPlacementFragment.onInflate(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("LifeCycle", "UnitPlacementFragment.onAttach(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("LifeCycle", "UnitPlacementFragment.onCreateView() room<" + ((HABApplication) getActivity().getApplication()).getConfigRoom().getId() + ">");

        fragmentView = inflater.inflate(R.layout.fragment_unit_placement, container, false);
        TextView textView = (TextView) fragmentView.findViewById(R.id.room_config_section_label);
        roomView = (UnitContainerView) fragmentView.findViewById(R.id.room_layout);

        roomView.setRoom(((HABApplication) getActivity().getApplication()).getConfigRoom());
        //TODO - Make DragListener internal in UnitContainerView and control the usage of it by layout parameters (DragNDrop on/off)
        roomView.setOnDragListener(dropListener);

        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LifeCycle", "UnitPlacementFragment.onStart(" + (getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : "?") + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle", "UnitPlacementFragment.onResume(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle", "UnitPlacementFragment.onPause(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LifeCycle", "UnitPlacementFragment.onStop(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("LifeCycle", "UnitPlacementFragment.onDestroyView(" + (getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : "?") + ")");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "UnitPlacementFragment.onDestroy(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("LifeCycle", "UnitPlacementFragment.onDetach(" + (getArguments()!=null? getArguments().getInt(ARG_SECTION_NUMBER): "?") + ")");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //TODO - Create dynamic menu
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.room_config_default, menu);

        //menu.findItem(R.id.action_remove).setVisible()
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add:
                showAddUnitDialog(getActivity());
                return true;
            case R.id.action_selection:
                unitSelectionDialog(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddUnitDialog(Context context) {
        final CharSequence[] items = {"Switch", "Dimmer", "Heating", "Vent", "Socket"};

        AlertDialog addUnitDialog;
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setOnDismissListener(new MyOnDismissListener());
        builder.setTitle("Select unit type");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        roomView.addNewUnitToRoom(new GraphicUnit(UnitType.SWITCH), 150, 150);
                        break;
                    case 1:
                        roomView.addNewUnitToRoom(new GraphicUnit(UnitType.DIMMER), 150, 150);
                        break;
                    case 2:
                        roomView.addNewUnitToRoom(new GraphicUnit(UnitType.ROOM_HEATER), 150, 150);
                        break;
                    case 3:
                        roomView.addNewUnitToRoom(new GraphicUnit(UnitType.VENT), 150, 150);
                        break;
                    case 4:
                        roomView.addNewUnitToRoom(new GraphicUnit(UnitType.SOCKET), 150, 150);
                        break;

                }
                dialog.dismiss();
            }
        });
        addUnitDialog = builder.create();
        addUnitDialog.show();

        class MyOnDismissListener implements DialogInterface.OnDismissListener {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }
    }

    private void unitSelectionDialog(Context context) {
        //TODO - Create dialog choices dynamically.
        final CharSequence[] items = {"Select all", "Deselect all", "Select all of current type(s)"};

        AlertDialog selectUnitDialog;
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose selection type");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        multiUnitSelection(true);
                        break;
                    case 1:
                        multiUnitSelection(false);
                        break;
                    case 2:
                        ArrayList<UnitType> selectedTypes = new ArrayList<UnitType>();

                        Iterator iterator = roomView.getRoom().getUnitIterator();
                        while(iterator.hasNext()) {
                            GraphicUnit gu = (GraphicUnit) iterator.next();
                            if(gu.isSelected() && !selectedTypes.contains(gu.getType()))
                                selectedTypes.add(gu.getType());
                        }

                        iterator = roomView.getRoom().getUnitIterator();
                        while(iterator.hasNext()) {
                            GraphicUnit gu = (GraphicUnit) iterator.next();
                            if(!gu.isSelected() && selectedTypes.contains(gu.getType()))
                                gu.setSelected(true);
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
        selectUnitDialog = builder.create();
        selectUnitDialog.show();
    }

    View.OnDragListener dropListener = new View.OnDragListener() {
        float dragXDiff = 0;
        float dragYDiff = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            switch (dragEvent) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i("DragEvent", "Entered");
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.i("DragEvent", "Ended");
                    break;
                case DragEvent.ACTION_DRAG_STARTED:
                    GraphicUnitWidget draggedView = (GraphicUnitWidget) event.getLocalState();
                    Log.i("DragEvent", "Started at LAMP = " + draggedView.getX() + "/" + draggedView.getY() + "   EVENT = " + event.getX() + "/" + event.getY());
                    dragXDiff = event.getX() - draggedView.getX();
                    dragYDiff = event.getY() - draggedView.getY();
                    //stop displaying the view where it was before it was dragged
                    draggedView.setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DROP:
                    GraphicUnitWidget droppedView = (GraphicUnitWidget) event.getLocalState();
                    Log.i("DragEvent", "Dropped at LAMP = " + Math.round(event.getX() + dragXDiff) + "/" + Math.round(event.getY() + dragYDiff) + "   EVENT = " + event.getX() + "/" + event.getY());
                    Log.i("DragEvent", "Drop target at TOP = " + v.getTop() + "   LEFT = " + v.getLeft());
//                    droppedView.setX(Math.round(event.getX() + dragXDiff + v.getLeft() - 70));
//                    droppedView.setY(Math.round(event.getY() + dragYDiff/* + v.getTop()*/ - 50));
                    droppedView.setX(Math.round(event.getX())-30);
                    droppedView.setY(Math.round(event.getY())+20);
                    droppedView.setVisibility(View.VISIBLE);

                    droppedView.gUnit.setRoomRelativeX(roomView.getScaledBitmapWidth() / (droppedView.getX() - roomView.getScaledBitmapX()));
                    droppedView.gUnit.setRoomRelativeY(roomView.getScaledBitmapHeight() / (droppedView.getY() - roomView.getScaledBitmapY()));

                    Log.d("Unit", "Dropped view pos X/Y = " + droppedView.getX() + "/" + droppedView.getY());
                    Log.d("UnitPos", "dropped REL: " + droppedView.gUnit.getRoomRelativeX() + "/" + droppedView.gUnit.getRoomRelativeY() + "   Calc: X=(" + roomView.getScaledBitmapWidth() + "/(" + droppedView.getX() + "-" + roomView.getScaledBitmapX() + ")  Y=(" + roomView.getScaledBitmapHeight() + "/(" + droppedView.getY() + "-" + roomView.getScaledBitmapY() + ")");
                    break;
            }
            return true;
        }
    };

    private void multiUnitSelection(boolean doSelect) {
        Iterator iterator = roomView.getRoom().getUnitIterator();
        while(iterator.hasNext()) {
            GraphicUnit gu = (GraphicUnit) iterator.next();
            setSelected(gu, doSelect);
        }
    }

    private void setSelected(GraphicUnit gu, boolean selected) {
        if(gu != null && gu.isSelected() != selected)
            gu.setSelected(selected);
    }
}

