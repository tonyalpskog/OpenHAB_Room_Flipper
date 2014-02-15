package org.openhab.habdroid.ui.widget;

import android.view.View;

import org.openhab.habdroid.ui.OpenHABWidgetAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABTextWidget extends OpenHABWidgetBase {

    public OpenHABTextWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        mViewData.splitString = mViewData.openHABWidget.getLabel().split("\\[|\\]");

        if (mViewData.labelTextView != null)
            if (mViewData.splitString.length > 0) {
                mViewData.labelTextView.setText(mViewData.splitString[0]);
            } else {
                mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
            }

        if (mViewData.valueTextView != null)
            if (mViewData.splitString.length > 1) {
                // If value is not empty, show TextView
                mViewData.valueTextView.setVisibility(View.VISIBLE);
                mViewData.valueTextView.setText(mViewData.splitString[1]);
            } else {
                // If value is empty, hide TextView to fix vertical alignment of label
                mViewData.valueTextView.setVisibility(View.GONE);
                mViewData.valueTextView.setText("");
            }

        return mViewData.widgetView;
    }

}
