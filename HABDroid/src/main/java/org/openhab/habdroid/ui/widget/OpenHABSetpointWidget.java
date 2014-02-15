package org.openhab.habdroid.ui.widget;

import android.view.View;
import android.widget.Button;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABWidgetAdapter;
import org.openhab.habdroid.ui.TouchRepeatListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSetpointWidget extends OpenHABWidgetBase {

    public OpenHABSetpointWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
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

        TouchRepeatListener.OnRepeatClickListener minusButtonOnRepeatClickListener = new SetpointRepeatClickListener(mViewData, true, mHABWidgetCommunication);
        setPointMinusButton.setOnTouchListener(new TouchRepeatListener(400, 150, minusButtonOnRepeatClickListener));

        TouchRepeatListener.OnRepeatClickListener plusButtonOnRepeatClickListener = new SetpointRepeatClickListener(mViewData, false, mHABWidgetCommunication);
        setPointPlusButton.setOnTouchListener(new TouchRepeatListener(400, 150, plusButtonOnRepeatClickListener));

        return mViewData.widgetView;
    }
}
