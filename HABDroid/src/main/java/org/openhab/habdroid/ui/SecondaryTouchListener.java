package org.openhab.habdroid.ui;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.openhab.habclient.HABApplication;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SecondaryTouchListener implements View.OnTouchListener {

    private View mDownView;
    private boolean mSecondaryDown;

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval The interval after second and subsequent click events
     * @param secondaryClickListener The OnSecondaryClickListener, that will be called at Down and then Up.
     */
    public SecondaryTouchListener(OnSecondaryClickListener secondaryClickListener) {
        if (secondaryClickListener == null)
            throw new IllegalArgumentException("null runnable");

        mOnSecondaryClickListener = secondaryClickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean result = true;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSecondaryDown = false;
                Log.d(HABApplication.getLogTag(), "[SecondaryTouch] ACTION_DOWN (Primary)");
                mDownView = view;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mSecondaryDown = true;
                Log.d(HABApplication.getLogTag(), "[SecondaryTouch] ACTION_POINTER_DOWN (Secondary)");
                fireSecondaryEvent(mDownView, SecondaryClickEvent.Down);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                Log.d(HABApplication.getLogTag(), String.format("[SecondaryTouch] %s (Touch ended)"
                        , motionEvent.getAction() == MotionEvent.ACTION_CANCEL ? "ACTION_CANCEL" : motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP ? "ACTION_POINTER_UP" : "ACTION_UP"));
                fireSecondaryEvent(mDownView, mSecondaryDown ? SecondaryClickEvent.Up : SecondaryClickEvent.Cancel);
                result = !mSecondaryDown;
                mDownView = null;
                mSecondaryDown = false;
                break;
        }
        return result;
    }

    public enum SecondaryClickEvent {
        Down,
        Up,
        Cancel,
    }

    public interface OnSecondaryClickListener {
        /**
         * Called when a OnRepeatClick occur.
         * Will fire a SecondaryClickEvent.Down at secondary touch.
         * Immediately when the touch cease a SecondaryClickEvent.Up will be fired
         *
         * @param v The view the touch event has been dispatched to.
         * @param event The type of event that occurred.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onSecondary(View v, SecondaryClickEvent event);
    }

    private OnSecondaryClickListener mOnSecondaryClickListener;

    public void setOnRepeatClickListener(OnSecondaryClickListener eventListener) {
        mOnSecondaryClickListener = eventListener;
    }

    private boolean fireSecondaryEvent(View view, SecondaryClickEvent event) {
        Log.d(HABApplication.getLogTag(), "[SecondaryTouch] Fire new SecondaryClickEvent: " + event.name());
        Log.v(HABApplication.getLogTag(), String.format("SecondaryClickEvent event = %s", event.name()));
        if(mOnSecondaryClickListener != null) {
            mOnSecondaryClickListener.onSecondary(view, event);
            return true;
        } else Log.w(HABApplication.getLogTag(), "Cannot post event. OnSecondaryClickListener is NULL");
        return false;
    }
}
