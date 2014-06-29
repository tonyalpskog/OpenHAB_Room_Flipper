package org.openhab.habdroid.ui.widget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABItem;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABRollerShutterWidget extends OpenHABWidgetBase {

    public OpenHABRollerShutterWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
        ImageButton rollershutterUpButton = (ImageButton)mViewData.widgetView.findViewById(R.id.rollershutterbutton_up);
        ImageButton rollershutterStopButton = (ImageButton)mViewData.widgetView.findViewById(R.id.rollershutterbutton_stop);
        ImageButton rollershutterDownButton = (ImageButton)mViewData.widgetView.findViewById(R.id.rollershutterbutton_down);
        rollershutterUpButton.setTag(mViewData.openHABWidget.getItem());
        rollershutterStopButton.setTag(mViewData.openHABWidget.getItem());
        rollershutterDownButton.setTag(mViewData.openHABWidget.getItem());
        rollershutterUpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton) v;
                OpenHABItem rollershutterItem = (OpenHABItem) rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    mHABWidgetCommunication.sendItemCommand(rollershutterItem, "UP");
                return false;
            }
        });
        rollershutterStopButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton) v;
                OpenHABItem rollershutterItem = (OpenHABItem) rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    mHABWidgetCommunication.sendItemCommand(rollershutterItem, "STOP");
                return false;
            }
        });
        rollershutterDownButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton) v;
                OpenHABItem rollershutterItem = (OpenHABItem) rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    mHABWidgetCommunication.sendItemCommand(rollershutterItem, "DOWN");
                return false;
            }
        });

        return mViewData.widgetView;
    }
}
