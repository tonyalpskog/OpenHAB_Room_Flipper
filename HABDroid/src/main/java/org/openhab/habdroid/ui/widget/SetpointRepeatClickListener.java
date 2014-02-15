package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.View;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.ui.OpenHABWidgetAdapter;
import org.openhab.habdroid.ui.TouchRepeatListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SetpointRepeatClickListener implements TouchRepeatListener.OnRepeatClickListener {
    private Float currentUnsentValue;
    private boolean mIsMinusButton;
    private OpenHABWidgetAdapter.ViewData mViewData;
    private IHABWidgetCommunication mHABWidgetCommunication;

    public SetpointRepeatClickListener(OpenHABWidgetAdapter.ViewData viewData, boolean isMinusButton, IHABWidgetCommunication habWidgetCommunication) {
        mViewData = viewData;
        mIsMinusButton = isMinusButton;
        mHABWidgetCommunication = habWidgetCommunication;
    }

    @Override
    public boolean onRepeat(View v, TouchRepeatListener.RepeatClickEvent event) {
        switch (event) {
            case InitialClick:
                doButtonAction(true);
                break;
            case RepeatClick:
                doButtonAction(false);
                break;
            case Done:
                //Send value
                mHABWidgetCommunication.sendItemCommand(mViewData.openHABWidget.getItem(), String.valueOf(currentUnsentValue));
                currentUnsentValue = null;
                break;
        }

        return false;
    }

    private void doButtonAction(boolean isInitialAction) {
        if(currentUnsentValue == null)
            currentUnsentValue = Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue();

        float nextValue = calculateValue(currentUnsentValue,
                mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());

        Log.d(HABApplication.getLogTag(), String.format("[TouchRepeat] currentUnsentValue(%f) != nextValue(%f)", currentUnsentValue, nextValue));
        if (currentUnsentValue != nextValue && mViewData.valueTextView != null) {
            if (mViewData.splitString.length > 1) {
                // If value is not empty, edit TextView
                currentUnsentValue = nextValue;
                Log.d(HABApplication.getLogTag(), String.format("[TouchRepeat] (%s) New value before %s text update = %f", (mIsMinusButton? "-" : "+"), (isInitialAction? "INITIAL" : "REPEAT"), currentUnsentValue));
                mViewData.valueTextView.setText(mViewData.valueTextView.getText().toString().replaceFirst("\\d*\\.?,?\\d*", currentUnsentValue.toString()));
            }
        }
    }

    private float calculateValue(float current, float step, float min, float max) {
        current = mIsMinusButton? current - step : current + step;

        if (current < min)
            current = min;
        else if (current > max)
            current = max;

        return current;
    }
}
