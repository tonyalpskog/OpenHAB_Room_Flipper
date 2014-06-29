package org.openhab.habdroid.ui;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.openhab.habclient.HABApplication;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically fires a OnRepeatClickListener.onRepeat event, used by Views to emulate keyboard-like behaviour.
 * First click is fired immediately, next after mInitialInterval, and subsequent after mRepeatInterval.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks.
 */
public class TouchRepeatListener implements OnTouchListener {

    private Handler handler = new Handler();

    private int mInitialInterval;
    private final int mRepeatInterval;
    private View mDownView;
    private boolean mIsRepeating = false;

    private Runnable mRunnableLooper = new Runnable() {
        @Override
        public void run() {
            if(mIsRepeating) {
                fireRepeatEvent(mDownView, RepeatClickEvent.RepeatClick);
                handler.postDelayed(mRunnableLooper, mRepeatInterval);
            }
        }
    };

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval The interval after second and subsequent click events
     * @param repeatClickListener The OnRepeatClickListener, that will be called periodically
     */
    public TouchRepeatListener(int initialInterval, int normalInterval, OnRepeatClickListener repeatClickListener) {
        if (repeatClickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (initialInterval <= 0 || normalInterval <= 0)
            throw new IllegalArgumentException("negative or zero interval");

        mInitialInterval = initialInterval;
        mRepeatInterval = normalInterval;
        mOnRepeatClickListener = repeatClickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(HABApplication.getLogTag(), "[TouchRepeat] ACTION_DOWN => handler.removeCallbacks(mRunnableLooper)");
                handler.removeCallbacks(mRunnableLooper);
                mDownView = view;
                handler.postDelayed(mRunnableLooper, mInitialInterval);
                fireRepeatEvent(mDownView, RepeatClickEvent.InitialClick);
                mIsRepeating = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(HABApplication.getLogTag(), String.format("[TouchRepeat] %s => handler.removeCallbacks(mRunnableLooper)"
                        , motionEvent.getAction() == MotionEvent.ACTION_CANCEL? "ACTION_CANCEL" : "ACTION_UP"));
                mIsRepeating = false;
                handler.removeCallbacks(mRunnableLooper);
                mRunnableLooper = null;//TODO - Better? but still not bug free.
                fireRepeatEvent(mDownView, RepeatClickEvent.Done);
                mDownView = null;
                break;
//            case MotionEvent.AXIS_PRESSURE:
//                Log.v(HABApplication.getLogTag(), "[TouchRepeat]  AXIS_PRESSURE = " +  motionEvent.getPressure());
//                break;
        }
        return true;
    }

    public enum RepeatClickEvent {
        InitialClick,
        RepeatClick,
        Done,
    }

    public interface OnRepeatClickListener {
        /**
         * Called when a OnRepeatClick occur.
         * First touch will immediately fire a RepeatClickEvent.InitialClick
         * Subsequent repeat events will fire a RepeatClickEvent.RepeatClick
         * Immediately when the touch cease a RepeatClickEvent.Done will be fired
         *
         * @param v The view the touch event has been dispatched to.
         * @param event The type of event that occurred.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onRepeat(View v, RepeatClickEvent event);
    }

    private OnRepeatClickListener mOnRepeatClickListener;

    public void setOnRepeatClickListener(OnRepeatClickListener eventListener) {
        mOnRepeatClickListener = eventListener;
    }

    private boolean fireRepeatEvent(View view, RepeatClickEvent event) {
        Log.d(HABApplication.getLogTag(), "[TouchRepeat] Fire new RepeatClickEvent: " + event.name());
        Log.v(HABApplication.getLogTag(), String.format("RepeatClickEvent event = %s", event.name()));
        if(mOnRepeatClickListener != null) {
            mOnRepeatClickListener.onRepeat(view, event);
            return true;
        } else Log.w(HABApplication.getLogTag(), "Cannot post event. OnRepeatClickListener is NULL");
        return false;
    }
}