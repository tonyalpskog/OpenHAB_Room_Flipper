package org.openhab.habdroid.ui.widget;

import android.view.View;

import org.openhab.habdroid.ui.OpenHABWidgetAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABFrameWidget extends OpenHABWidgetBase {

    public OpenHABFrameWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
        mViewData.widgetView.setClickable(false);
        if (mViewData.openHABWidget.getLabel().length() > 0) { // hide empty frames
            mViewData.widgetView.setVisibility(View.VISIBLE);
            mViewData.labelTextView.setVisibility(View.VISIBLE);
        } else {
            mViewData.widgetView.setVisibility(View.GONE);
            mViewData.labelTextView.setVisibility(View.GONE);
        }

        return mViewData.widgetView;
    }
}
