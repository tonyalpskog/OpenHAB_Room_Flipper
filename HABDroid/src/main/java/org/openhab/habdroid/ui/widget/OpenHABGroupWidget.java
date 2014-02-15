package org.openhab.habdroid.ui.widget;

import android.view.View;

import org.openhab.habdroid.ui.OpenHABWidgetAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABGroupWidget extends OpenHABWidgetBase {

    public OpenHABGroupWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        if (mViewData.labelTextView != null && mViewData.valueTextView != null) {
            mViewData.splitString = mViewData.openHABWidget.getLabel().split("\\[|\\]");
            mViewData.labelTextView.setText(mViewData.splitString[0]);
            if (mViewData.splitString.length > 1) { // We have some value
                mViewData.valueTextView.setText(mViewData.splitString[1]);
            } else {
                // This is needed to clean up cached TextViews
                mViewData.valueTextView.setText("");
            }
        }

        return mViewData.widgetView;
    }
}