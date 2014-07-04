package org.openhab.habclient;

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
import android.widget.Toast;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IRestCommunication;
import org.openhab.domain.model.GraphicUnit;
import org.openhab.domain.model.Room;
import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class UnitPlacementFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "UnitPlacementFragment";

    private UnitContainerView roomView;
    private final EnumSet<OpenHABWidgetType> mUnitTypes = EnumSet.of(OpenHABWidgetType.RollerShutter, OpenHABWidgetType.Switch, OpenHABWidgetType.Slider, OpenHABWidgetType.ItemText, OpenHABWidgetType.SitemapText, OpenHABWidgetType.SelectionSwitch, OpenHABWidgetType.Selection, OpenHABWidgetType.Setpoint, OpenHABWidgetType.Color, OpenHABWidgetType.Group);
    //TA: TODO - Add a LinkedPageLink string member here for REST Get sitemap usage. Then Load HABApp with the resulting data source.

    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject
    IRestCommunication mRestCommunication;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UnitPlacementFragment newInstance(int sectionNumber) {
        UnitPlacementFragment fragment = new UnitPlacementFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
//        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));//TODO - remove?
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);

        Log.d("LifeCycle", "UnitPlacementFragment(" + (getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : "?") + ") <constructor>");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Room room = ((RoomConfigActivity)getActivity()).getConfigRoom();
        Log.d("LifeCycle", "UnitPlacementFragment.onCreateView() room<" + (room == null? "NULL": room.getId()) + ">");

        View fragmentView = inflater.inflate(R.layout.fragment_unit_placement, container, false);
        TextView textView = (TextView) fragmentView.findViewById(R.id.room_config_section_label);
        roomView = (UnitContainerView) fragmentView.findViewById(R.id.room_layout);

        roomView.setRoom(room);
        //TODO - Make DragListener internal in UnitContainerView and control the usage of it by layout parameters (DragNDrop on/off)
        roomView.setOnDragListener(dropListener);

//        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));//TODO - Fix this

        setHasOptionsMenu(true);

        mRestCommunication.requestOpenHABSitemap(roomView.getRoom().getRoomWidget());

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
                break;
            case R.id.action_remove:
                removeSelectedUnits();
                break;
            case R.id.action_selection:
                unitSelectionDialog(getActivity());
                break;
            case R.id.action_clone:
                //TODO: Create and draw a new copy of any current selection.
                Toast.makeText(getActivity(), "Not implemented.", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void removeSelectedUnits() {
        final List<GraphicUnit> unitsToBeRemovedList = new ArrayList<GraphicUnit>();

        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
            if(gu.isSelected())
                unitsToBeRemovedList.add(gu);
        }

        for(GraphicUnit gu : unitsToBeRemovedList) {
            roomView.getRoom().removeUnit(gu);
        }

        roomView.redrawAllUnits();//TA: TODO - Ugly way too remove a single View.
    }

    private void showAddUnitDialog(Context context) {
        List<OpenHABWidget> list = mWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null);
        for (OpenHABWidget aList : list)
            Log.d(HABApplication.getLogTag(), "WidgetProvider data ID = " + aList.getId());

//        Toast.makeText(context, "ALL widgetList = " + (mOpenHABWidgetRoomProvider == null? "NULL-Provider": mOpenHABWidgetRoomProvider.getWidgetList(OpenHABWidgetType.Switch).size()), Toast.LENGTH_SHORT).show();
        //TA: Just a test. TODO - Replace some List<> for a better sustainable solution.
        List<CharSequence> itemsList = new ArrayList<CharSequence>();
        List<String> itemNameList = new ArrayList<String>();
        final HashMap<Integer, OpenHABWidget> widgetMap = new HashMap<Integer, OpenHABWidget>();
        String strLogAll = "showAddUnitDialog() -> Full list: ";
        String strLogRemoved = "showAddUnitDialog() -> Removed list: ";

//TA: TODO - Not sure if this is needed.
//        if(!HABApplication.getOpenHABWidgetProvider2().hasWidgetID(roomView.getRoom().getGroupWidgetId()))
//            HABApplication.getRestCommunication().requestOpenHABSitemap(context, roomView.getRoom().getSitemapId());

        if(roomView.getRoom().getRoomWidget() == null) {
            mRestCommunication.requestOpenHABSitemap((String) null);
            if(roomView.getRoom().getRoomWidget() == null)
            {
                Log.e(HABApplication.getLogTag(), String.format("Cannot get room items for Room '%s' with widget ID = '%s'", roomView.getRoom().getName(), roomView.getRoom().getGroupWidgetId()));
                Toast.makeText(context, "Cannot get items for this room.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        List<OpenHABWidget> widgetList = roomView.getRoom().getRoomWidget().getChildren();
        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            OpenHABWidget next = iterator.next();
            strLogAll += next.getId() + ", ";
            if(mUnitTypes.contains(next.getType()) && !roomView.getRoom().contains(next)){
                itemNameList.add(next.getItem().getName());
                widgetMap.put(i, next);
                itemsList.add(i++, String.format("(%s) %s", next.getType().Name, next.hasLinkedPage() ? next.getLinkedPage().getTitle() : next.getItem().getName()));
            }
            else
                strLogRemoved += next.getId() + ", ";
        }
        Log.d(HABApplication.getLogTag(), strLogAll);
        Log.d(HABApplication.getLogTag(), strLogRemoved);

        if(itemsList.size() < 1) {
            Toast.makeText(context, "There are no more items for this room.", Toast.LENGTH_SHORT).show();
            return;
        }

        final List<String> finalItemNameList = itemNameList;

        CharSequence[] items = (CharSequence[]) itemsList.toArray(new CharSequence[itemsList.size()]);
//        Toast.makeText(context, "widgetList = " + widgetList.size() + "   itemsList = " + itemsList.size(), Toast.LENGTH_SHORT).show();

        AlertDialog addUnitDialog;
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setOnDismissListener(new MyOnDismissListener());
        builder.setTitle("Select unit type");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                roomView.addNewUnitToRoom(new GraphicUnit(widgetMap.get(item)), 50, 50);
                Log.d(TAG, "showAddUnitDialog() -> (list:)Added widget = " + finalItemNameList.get(item));
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
        final CharSequence[] items = {"Select all", "Deselect all", "Select all of current type(s)", "Select all clones"};

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
                        final ArrayList<OpenHABWidgetType> selectedTypes = getSelectedWidgetsType();

                        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
                            if(!gu.isSelected() && selectedTypes.contains(gu.getType())) {
                                gu.setSelected(true);
                                roomView.setSelected(gu);
                            }
                        }
                        break;
                    case 3:
                        ArrayList<String> selectedId = getSelectedWidgetsId();

                        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
                            if(!gu.isSelected() && selectedId.contains(gu.getOpenHABWidget().getId())) {
                                gu.setSelected(true);
                                roomView.setSelected(gu);
                            }
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
        selectUnitDialog = builder.create();
        selectUnitDialog.show();
    }

    private ArrayList<OpenHABWidgetType> getSelectedWidgetsType() {
        ArrayList<OpenHABWidgetType> selectedTypeList = new ArrayList<OpenHABWidgetType>();

        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
            if(gu.isSelected() && !selectedTypeList.contains(gu.getType()))
                selectedTypeList.add(gu.getType());
        }

        return selectedTypeList;
    }

    private ArrayList<String> getSelectedWidgetsId() {
        ArrayList<String> selectedIdList = new ArrayList<String>();

        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
            if(gu.isSelected() && !selectedIdList.contains(gu.getOpenHABWidget().getId()))
                selectedIdList.add(gu.getOpenHABWidget().getId());
        }

        return selectedIdList;
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
                    //TODO - Fix this temporary hard coded adjustment
                    droppedView.setX(Math.round(event.getX())-30);
                    droppedView.setY(Math.round(event.getY())-30);

                    droppedView.setVisibility(View.VISIBLE);

                    setRoomRelativePositions(droppedView);

                    Log.d("Unit", "Dropped view pos X/Y = " + droppedView.getX() + "/" + droppedView.getY());
                    Log.d("UnitPos", "dropped REL: " + droppedView.getgUnit().getRoomRelativeX() + "/" + droppedView.getgUnit().getRoomRelativeY() + "   Calc: X=(" + roomView.getScaledBitmapWidth() + "/(" + droppedView.getX() + "-" + roomView.getScaledBitmapX() + ")  Y=(" + roomView.getScaledBitmapHeight() + "/(" + droppedView.getY() + "-" + roomView.getScaledBitmapY() + ")");
                    break;
            }
            return true;
        }
    };

//    private int getRoomRelativeX(int percentOfX) {
//        return (((roomView.getScaledBitmapWidth() / 100) * percentOfX) + roomView.getScaledBitmapX());
//    }
//
//    private int getRoomRelativeY(int percentOfY) {
//        return (((roomView.getScaledBitmapHeight() / 100) * percentOfY) + roomView.getScaledBitmapY());
//    }

    private void setRoomRelativePositions(GraphicUnitWidget graphicUnitView) {
        setRoomRelativePositions(graphicUnitView.getgUnit(), graphicUnitView);
    }

    private void setRoomRelativePositions(GraphicUnit gUnit, View unitView) {
        gUnit.setRoomRelativeX(roomView.getScaledBitmapWidth() / (unitView.getX() - roomView.getScaledBitmapX()));
        gUnit.setRoomRelativeY(roomView.getScaledBitmapHeight() / (unitView.getY() - roomView.getScaledBitmapY()));
    }

    private void multiUnitSelection(boolean doSelect) {
        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
            setSelected(gu, doSelect);
        }
    }

    private void setSelected(GraphicUnit gu, boolean selected) {
        if(gu != null && gu.isSelected() != selected) {
            gu.setSelected(selected);
            roomView.setSelected(gu);
        }
    }

    private boolean cloneSelectedWidgets() {
        ArrayList<String> selectedWidgetsIdList = new ArrayList<String>();

        for(GraphicUnit gu : roomView.getRoom().getUnits()) {
            if(gu.isSelected() && !selectedWidgetsIdList.contains(gu.getOpenHABWidget().getId())) {
                roomView.addNewUnitToRoom(new GraphicUnit(gu.getOpenHABWidget()), 50, 50);
                selectedWidgetsIdList.add(gu.getOpenHABWidget().getId());
            }
        }

        return selectedWidgetsIdList.size() > 0;
    }
}

