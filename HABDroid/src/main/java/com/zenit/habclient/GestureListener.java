package com.zenit.habclient;

import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Tony Alpskog in 2013.
 */
//TODO - Have a closer look at GestureDetector.OnGestureListener and ScaleGestureDetector.OnScaleGestureListener
//TODO - Will those interfaces make this code better?
public class GestureListener implements View.OnTouchListener {

    final String TAG = "GestureListener";

    OnGestureListener mOnGestureListener;

    final float MIN_PINCH_DISTANCE = 100;
    final float MIN_SWIPE_DISTANCE = 100;
    final float DIAGONAL_FACTOR = 2;
    //Touch event related variables
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;
    int touchState = IDLE;
    float dist0 = 1, distCurrent = 1;

    float pinchBeginDist = 0;
    float pinchEndDist = 0;
    boolean isPinchOut = false;
    boolean ongoingPinch = false;
    private float initialX;
    private float initialY;

    private View mView;

    public GestureListener(View view) {
        mView = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, MotionEvent.axisToString(event.getAction()));

        float distx, disty;

        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                //A pressed gesture has started, the motion contains the initial starting location.
//                Log.d(TAG, "ACTION_DOWN");
                touchState = TOUCH;
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //A non-primary pointer has gone down.
//                Log.d(TAG, "ACTION_POINTER_DOWN");
                touchState = PINCH;

                //Get the distance when the second pointer touch
                distx = event.getX(0) - event.getX(1);
                disty = event.getY(0) - event.getY(1);
                dist0 = FloatMath.sqrt(distx * distx + disty * disty);

                pinchBeginDist = dist0;
                ongoingPinch = true;

                break;

            case MotionEvent.ACTION_MOVE:
                //A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
//                Log.d(TAG, "ACTION_MOVE");

                if(touchState == PINCH){
                    //Get the current distance
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    distCurrent = FloatMath.sqrt(distx * distx + disty * disty);

                    if(pinchEndDist == 0)//2:nd measure will detect the type of pinch.
                        isPinchOut = pinchBeginDist < distCurrent;
                    else {
                        if(isPinchOut && pinchEndDist > distCurrent) {
                            //Pinch is being reversed from OUT to IN
                            pinchBeginDist = pinchEndDist;
                            isPinchOut = false;
                        } else if(!isPinchOut && pinchEndDist < distCurrent) {
                            //Pinch is being reversed from IN to OUT
                            pinchBeginDist = pinchEndDist;
                            isPinchOut = true;
                        }

                    }
                    pinchEndDist = distCurrent;

//                    Log.d(TAG, "Pinching = " + distCurrent);
                }

                break;

            case MotionEvent.ACTION_UP:
                //A pressed gesture has finished.
//                Log.d(TAG, "ACTION_UP");
                touchState = IDLE;
                if(ongoingPinch) {
                    ongoingPinch = false;
                    float finalDist = isPinchOut? pinchEndDist - pinchBeginDist: pinchBeginDist - pinchEndDist;
                    if(finalDist >= MIN_PINCH_DISTANCE) {
                        //Log.d(TAG, "We got a pinch " + (isPinchOut? "OUT": "IN") + "(" + finalDist +")");

                        if(isPinchOut) {
                            fireGestureEvent(Gesture.PINCH_OUT);
                        } else {
                            fireGestureEvent(Gesture.PINCH_IN);
                        }
                    }
                    pinchBeginDist = pinchEndDist = 0;
                } else {
                    //Log.d(TAG, "Inside touchViewFlipper MotionEvent.ACTION_UP code");

                    float finalX = event.getX();
                    float finalY = event.getY();

                    float xDistance = Math.abs(initialX - finalX);
                    float yDistance = Math.abs(initialY - finalY);

                    if(xDistance > yDistance? xDistance / yDistance > DIAGONAL_FACTOR : yDistance / xDistance > DIAGONAL_FACTOR) {
                        if(xDistance > yDistance && xDistance > MIN_SWIPE_DISTANCE) {
                            //Horizontal swipe
                            if (initialX > finalX) {
                                fireGestureEvent(Gesture.SWIPE_LEFT);
                            } else {
                                fireGestureEvent(Gesture.SWIPE_RIGHT);
                            }
                        } else if(yDistance > xDistance && yDistance > MIN_SWIPE_DISTANCE) {
                            //Vertical swipe
                            if (initialY > finalY) {
                                fireGestureEvent(Gesture.SWIPE_UP);
                            } else {
                                fireGestureEvent(Gesture.SWIPE_DOWN);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //A non-primary pointer has gone up.
//                Log.d(TAG, "ACTION_POINTER_UP");
                touchState = TOUCH;
                break;
        }

        return true;
    }

    /**
     * Interface definition for a callback to be invoked when another room is shown.
     * The callback will be invoked after the new room is shown.
     */
    public interface OnGestureListener {
        /**
         * Called when another room is shown.
         *
         * @param v The view the touch event has been dispatched to.
         * @param gesture The Gesture enum value.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onGesture(View v, Gesture gesture);
    }

    public void setOnGestureListener(OnGestureListener eventListener) {
        mOnGestureListener = eventListener;
    }

    private boolean fireGestureEvent(Gesture gesture) {
        if(mOnGestureListener != null) {
            mOnGestureListener.onGesture(mView, gesture);
            return true;
        } else Log.w(TAG, "Cannot post event. OnGestureListener is NULL");
        return false;
    }
}
