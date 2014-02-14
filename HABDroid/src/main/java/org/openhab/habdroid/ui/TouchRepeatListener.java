package org.openhab.habdroid.ui;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.zenit.habclient.HABApplication;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically fires a OnRepeatClickListener.onRepeat event, used by Views to emulate keyboard-like behaviour.
 * First click is fired immediately, next after mInitialInterval, and subsequent after mNormalInterval.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks.
 */
public class TouchRepeatListener implements OnTouchListener {

    private Handler handler = new Handler();

    private int mInitialInterval;
    private final int mNormalInterval;
    private View mDownView;

    private Runnable mHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, mNormalInterval);
            fireRepeatEvent(mDownView, RepeatClickEvent.RepeatClick);
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
        mNormalInterval = normalInterval;
        mOnRepeatClickListener = repeatClickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(mHandlerRunnable);
                handler.postDelayed(mHandlerRunnable, mInitialInterval);
                mDownView = view;
                fireRepeatEvent(mDownView, RepeatClickEvent.InitialClick);
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(mHandlerRunnable);
                fireRepeatEvent(mDownView, RepeatClickEvent.Done);
                mDownView = null;
                break;
        }
        return false;
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
        Log.v(HABApplication.getLogTag(), String.format("RepeatClickEvent event = %s", event.name()));
        if(mOnRepeatClickListener != null) {
            mOnRepeatClickListener.onRepeat(view, event);
            return true;
        } else Log.w(HABApplication.getLogTag(), "Cannot post event. OnRepeatClickListener is NULL");
        return false;
    }
}