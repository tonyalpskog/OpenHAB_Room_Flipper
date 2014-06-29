package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import org.openhab.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSliderWidget extends OpenHABWidgetBase {

    public OpenHABSliderWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    public View getWidget() {
        mViewData.splitString = mViewData.openHABWidget.getLabel().split("\\[|\\]");
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.splitString[0]);
        SeekBar sliderSeekBar = (SeekBar)mViewData.widgetView.findViewById(R.id.sliderseekbar);
        if (mViewData.openHABWidget.hasItem()) {
            sliderSeekBar.setTag(mViewData.openHABWidget.getItem());
            int sliderState = 0;
            try {
                sliderState = (int)Float.parseFloat(mViewData.openHABWidget.getItem().getState());
            } catch (NumberFormatException e) {
                if (e != null) {
//                    Crittercism.logHandledException(e);
                    Log.e(HABApplication.getLogTag(), e.getMessage());
                }
                if (mViewData.openHABWidget.getItem().getState().equals("OFF")) {
                    sliderState = 0;
                } else if (mViewData.openHABWidget.getItem().getState().equals("ON")) {
                    sliderState = 100;
                }
            }
            sliderSeekBar.setProgress(sliderState);
            sliderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                }
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d(HABApplication.getLogTag(), "onStartTrackingTouch position = " + seekBar.getProgress());
                }
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d(HABApplication.getLogTag(), "onStopTrackingTouch position = " + seekBar.getProgress());
                    OpenHABItem sliderItem = (OpenHABItem)seekBar.getTag();
                    //							sliderItem.sendCommand(String.valueOf(seekBar.getProgress()));
                    if (sliderItem != null && seekBar != null)
                        mHABWidgetCommunication.sendItemCommand(sliderItem, String.valueOf(seekBar.getProgress()));
                }
            });
        }

        return mViewData.widgetView;
    }

}
