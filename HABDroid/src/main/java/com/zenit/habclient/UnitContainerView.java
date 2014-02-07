package com.zenit.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2013.
 */
public class UnitContainerView extends FrameLayout implements RoomImageView.OnBackgroundDrawn {

    OnContainerBackgroundDrawn mOnContainerBackgroundDrawn;
    private final String TAG = "UnitContainerView";

    //TA: TODO - Will disposal of unused RoomImageView save some memory?
    private RoomImageView roomImage;
    private Room mRoom;
    private List<View> addedUnitViews;
    private boolean mBlockUnitRedraw = false;

    public UnitContainerView(Context context) {
        this(context, null);
    }

    public UnitContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        addedUnitViews = new ArrayList<View>();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );

        setLayoutParams(params);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.room_layout, this, true);

        roomImage = (RoomImageView) findViewById(R.id.dropImage);
        roomImage.setOnBackgroundDrawnListener(this);
    }

    public int getScaledBitmapHeight() {
        return roomImage.getScaledBitmapHeight();
    }

    public int getScaledBitmapWidth() {
        return roomImage.getScaledBitmapWidth();
    }

    public int getScaledBitmapX() {
        return roomImage.getScaledBitmapX();
    }

    public int getScaledBitmapY() {
        return roomImage.getScaledBitmapY();
    }

    @Override
    public boolean onBackgroundDrawn(View v) {
        postOnContainerBackgroundDrawn();
        redrawAllUnits();
        return false;
    }

    public interface OnContainerBackgroundDrawn {
        boolean onContainerBackgroundDrawn(View v);
    }

    public void setOnContainerBackgroundDrawnListener(OnContainerBackgroundDrawn eventListener) {
        mOnContainerBackgroundDrawn = eventListener;
    }

    private boolean postOnContainerBackgroundDrawn() {
        if(mOnContainerBackgroundDrawn != null) {
            mOnContainerBackgroundDrawn.onContainerBackgroundDrawn(this);
            return true;
        }
        return false;
    }

    public void redrawAllUnits() {
        Log.d(TAG, "redrawAllUnits() - method called");
        if(mRoom == null || mBlockUnitRedraw)
            return;

        removeAllUnitViews();

        Iterator unitIterator = mRoom.getUnitIterator();
        GraphicUnit graphicUnit;
        while (unitIterator.hasNext()) {
            graphicUnit = (GraphicUnit) unitIterator.next();
            graphicUnit.resetView();
            drawUnitInRoom(graphicUnit);
        }
    }

    private void drawUnitInRoom(GraphicUnit gUnit) {
        int x = Math.round(getScaledBitmapX() + (getScaledBitmapWidth() / gUnit.getRoomRelativeX()));
        int y = Math.round(getScaledBitmapY() + (getScaledBitmapHeight() / gUnit.getRoomRelativeY()));
        drawUnitInRoom(gUnit, x, y);
    }

    public void addNewUnitToRoom(GraphicUnit gUnit, int percentOfX, int percentOfY) {
        mRoom.addUnit(gUnit);
        Log.d("Add unit", "addNewUnitToRoom() add unit<" + gUnit.getId() + "> to mRoom<" + mRoom.getId() + ">");

        int x = ((getScaledBitmapWidth() / 100) * percentOfX) + getScaledBitmapX();
        int y = ((getScaledBitmapHeight() / 100) * percentOfY) + getScaledBitmapY();

        //TODO - Fix initial unit placement mismatch
        gUnit.setRoomRelativeX(getScaledBitmapWidth() / (x - getScaledBitmapX()));
        gUnit.setRoomRelativeY(getScaledBitmapHeight() / (y - getScaledBitmapY()));

        View addedView = drawUnitInRoom(gUnit, x, y);
    }

    private View drawUnitInRoom(GraphicUnit gUnit, int x, int y) {
        //TODO - This is a horrible work-around for a FrameLayout problem.
        RelativeLayout layout = new RelativeLayout(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.room_layout_relative, layout, true);

        //RelativeLayout layout = (RelativeLayout) findViewById(R.layout.room_layout_relative);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(x, y, 0, 0);
        Log.d("UnitPos", params.leftMargin + "/" + params.topMargin);
        ImageView gView = gUnit.getView(getContext());
        layout.addView(gView, params);
        addedUnitViews.add(layout);
        addView(layout);
        return gView;
    }

    public void setRoom(Room nextRoom) {
        mRoom = nextRoom;
        mBlockUnitRedraw = true;
        setImageBitmap(mRoom.getRoomImage());
        mBlockUnitRedraw = false;
        redrawAllUnits();
    }

    public Room getRoom() {
        return mRoom;
    }

    public void setImageBitmap(Bitmap bitmap) {
        roomImage.setImageBitmap(bitmap);
    }

    private void removeAllUnitViews() {
        Log.d("Remove unit views", "Child count = " + getChildCount());
        Iterator iterator = addedUnitViews.iterator();
        while(iterator.hasNext()) {
            ViewGroup vg = (ViewGroup) iterator.next();
            vg.removeAllViewsInLayout();
            removeView(vg);
        }
        addedUnitViews.clear();
        Log.d("Remove unit views", "Room<" + getRoom().getId() + ">  Child count = " + getChildCount());
    }
}
