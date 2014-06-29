package org.openhab.habdroid.ui.widget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABItem;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSwitchWidget extends OpenHABWidgetBase {

    public OpenHABSwitchWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
        Switch switchSwitch = (Switch)mViewData.widgetView.findViewById(R.id.switchswitch);
        if (mViewData.openHABWidget.hasItem()) {
            if (mViewData.openHABWidget.getItem().getState().equals("ON")) {
                switchSwitch.setChecked(true);
            } else {
                switchSwitch.setChecked(false);
            }
        }
        switchSwitch.setTag(mViewData.openHABWidget.getItem());
        switchSwitch.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                Switch switchSwitch = (Switch)v;
                OpenHABItem linkedItem = (OpenHABItem)switchSwitch.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    if (!switchSwitch.isChecked()) {
                        mHABWidgetCommunication.sendItemCommand(linkedItem, "ON");
                    } else {
                        mHABWidgetCommunication.sendItemCommand(linkedItem, "OFF");
                    }
                return false;
            }
        });

        return mViewData.widgetView;
    }
}
