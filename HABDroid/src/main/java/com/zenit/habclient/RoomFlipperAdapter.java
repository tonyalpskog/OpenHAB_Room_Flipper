package com.zenit.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Tony Alpskog in 2013.
 */
public class RoomFlipperAdapter {

    private final String TAG = "RoomFlipperAdapter";
    private Context mContext = null;
    private Room currentRoom;
    private RoomProvider roomProvider;

    public RoomFlipperAdapter(Context context, Room initialRoom) {
        mContext = context;
        currentRoom = initialRoom;
    }

    public Bitmap getCurrentBitmap() {
        return currentRoom.getRoomImage();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Room getRoom(Gesture gesture) {
        Room nextRoom = null;

        switch(gesture) {
            case PINCH_OUT:
                Log.d(TAG, "Pinch to LOWER view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.BELOW);
                break;

            case PINCH_IN:
                Log.d(TAG, "Pinch to UPPER view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.ABOVE);
                break;

            case SWIPE_LEFT:
                //Swipe to RIGHT view
                Log.d(TAG, "Swipe to RIGHT view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.RIGHT);
                break;

            case SWIPE_RIGHT:
                //Swipe to LEFT view
                Log.d(TAG, "Swipe to LEFT view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.LEFT);
                break;

            case SWIPE_UP:
                //Swipe to LOWER view
                Log.d(TAG, "Swipe to LOWER view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.DOWN);
                break;

            case SWIPE_DOWN:
                //Swipe to UPPER view
                Log.d(TAG, "Swipe to UPPER view");
                nextRoom = currentRoom.getRoomByAlignment(Direction.UP);
                break;
        }

        if(nextRoom != null) {
            currentRoom = nextRoom;
        }

        return nextRoom;
    }
}
