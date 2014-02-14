package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.ui.OpenHABWidgetAdapter;
import org.openhab.habdroid.ui.TouchRepeatListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SetpointWidget {

    OpenHABWidgetAdapter.ViewData mViewData;
    IHABWidgetCommunication mHABWidgetCommunication;

    public SetpointWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        mHABWidgetCommunication = habWidgetCommunication;
        mViewData = viewData;
    }

    public View getWidget() {
        mViewData.splitString = mViewData.openHABWidget.getLabel().split("\\[|\\]");

        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.splitString[0]);

        if (mViewData.valueTextView != null) {
            if (mViewData.splitString.length > 1) {
                // If value is not empty, show TextView
                mViewData.valueTextView.setVisibility(View.VISIBLE);
                mViewData.valueTextView.setText(mViewData.splitString[1]);
            }
        }

        Button setPointMinusButton = (Button)mViewData.widgetView.findViewById(R.id.setpointbutton_minus);
        Button setPointPlusButton = (Button)mViewData.widgetView.findViewById(R.id.setpointbutton_plus);
        setPointMinusButton.setTag(mViewData.openHABWidget);
        setPointPlusButton.setTag(mViewData.openHABWidget);

//        setPointMinusButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.d(HABApplication.getLogTag(), "Minus");
//
//                float currentValue = activateMinusButton(Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue(),
//                        mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());
//
//                mHABWidgetCommunication.sendItemCommand(mViewData.openHABWidget.getItem(), String.valueOf(currentValue));
//            }
//        });

        TouchRepeatListener.OnRepeatClickListener minusButtonOnRepeatClickListener = new TouchRepeatListener.OnRepeatClickListener() {
            private Float currentUnsentValue;

            @Override
            public boolean onRepeat(View v, TouchRepeatListener.RepeatClickEvent event) {
                switch (event) {
                    case InitialClick:
                        //Do nothing - Already using a OnClickListener.
                        if(currentUnsentValue == null)
                            currentUnsentValue = Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue();

                        currentUnsentValue = activateMinusButton(currentUnsentValue,
                                mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());

                        if (mViewData.valueTextView != null) {
                            if (mViewData.splitString.length > 1) {
                                // If value is not empty, edit TextView
                                mViewData.valueTextView.setText(mViewData.valueTextView.getText().toString().replaceFirst("\\d*\\.?,?\\d*", currentUnsentValue.toString()));
                            }
                        }
                        break;
                    case RepeatClick:
                        //Just subtract a step
                        if(currentUnsentValue == null)
                            currentUnsentValue = Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue();

                        currentUnsentValue = activateMinusButton(currentUnsentValue,
                                mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());

                        if (mViewData.valueTextView != null) {
                            if (mViewData.splitString.length > 1) {
                                // If value is not empty, edit TextView
                                mViewData.valueTextView.setText(mViewData.valueTextView.getText().toString().replaceFirst("\\d*\\.?,?\\d*", currentUnsentValue.toString()));
                            }
                        }

                        break;
                    case Done:
                        //Send value
                        mHABWidgetCommunication.sendItemCommand(mViewData.openHABWidget.getItem(), String.valueOf(currentUnsentValue));
                        currentUnsentValue = null;
                        break;
                }

                return false;
            }
        };

        setPointMinusButton.setOnTouchListener(new TouchRepeatListener(400, 150, minusButtonOnRepeatClickListener));

//        setPointPlusButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.d(HABApplication.getLogTag(), "Plus");
//
//                float currentValue = activatePlusButton(Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue(),
//                        mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());
//
//                mHABWidgetCommunication.sendItemCommand(mViewData.openHABWidget.getItem(), String.valueOf(currentValue));
//            }
//        });

        TouchRepeatListener.OnRepeatClickListener plusButtonOnRepeatClickListener = new TouchRepeatListener.OnRepeatClickListener() {
            private Float currentUnsentValue;

            @Override
            public boolean onRepeat(View v, TouchRepeatListener.RepeatClickEvent event) {
                switch (event) {
                    case InitialClick:
                        //Do nothing - Already using a OnClickListener.
                        if(currentUnsentValue == null)
                            currentUnsentValue = Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue();

                        currentUnsentValue = activatePlusButton(currentUnsentValue,
                                mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());

                        if (mViewData.valueTextView != null) {
                            if (mViewData.splitString.length > 1) {
                                // If value is not empty, edit TextView
                                mViewData.valueTextView.setText(mViewData.valueTextView.getText().toString().replaceFirst("\\d*\\.?,?\\d*", currentUnsentValue.toString()));
                            }
                        }
                        break;
                    case RepeatClick:
                        //Just add a step
                        if(currentUnsentValue == null)
                            currentUnsentValue = Float.valueOf(mViewData.openHABWidget.getItem().getState()).floatValue();

                        currentUnsentValue = activatePlusButton(currentUnsentValue,
                                mViewData.openHABWidget.getStep(), mViewData.openHABWidget.getMinValue(), mViewData.openHABWidget.getMaxValue());

                        if (mViewData.valueTextView != null) {
                            if (mViewData.splitString.length > 1) {
                                // If value is not empty, edit TextView
                                mViewData.valueTextView.setText(mViewData.valueTextView.getText().toString().replaceFirst("\\d*\\.?,?\\d*", currentUnsentValue.toString()));
                            }
                        }

                        break;
                    case Done:
                        //Send value
                        mHABWidgetCommunication.sendItemCommand(mViewData.openHABWidget.getItem(), String.valueOf(currentUnsentValue));
                        currentUnsentValue = null;
                        break;
                }

                return false;
            }
        };

        setPointPlusButton.setOnTouchListener(new TouchRepeatListener(400, 150, plusButtonOnRepeatClickListener));

        return mViewData.widgetView;
    }

    private float activateMinusButton(float current, float step, float min, float max) {
        current -= step;
        if (current < min)
            current = min;
        if (current > max)
            current = max;

        return current;
    }


    private float activatePlusButton(float current, float step, float min, float max) {
        current += step;
        if (current < min)
            current = min;
        if (current > max)
            current = max;

        return current;
    }}
