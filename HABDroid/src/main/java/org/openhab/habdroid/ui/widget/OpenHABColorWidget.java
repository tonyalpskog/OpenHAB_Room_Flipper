package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.ui.OpenHABWidgetAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABColorWidget extends OpenHABWidgetBase {

    public OpenHABColorWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
        ImageButton colorUpButton = (ImageButton)mViewData.widgetView.findViewById(R.id.colorbutton_up);
        ImageButton colorDownButton = (ImageButton)mViewData.widgetView.findViewById(R.id.colorbutton_down);
        ImageButton colorColorButton = (ImageButton)mViewData.widgetView.findViewById(R.id.colorbutton_color);
        colorUpButton.setTag(mViewData.openHABWidget.getItem());
        colorDownButton.setTag(mViewData.openHABWidget.getItem());
        colorColorButton.setTag(mViewData.openHABWidget.getItem());
        colorUpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton) v;
                OpenHABItem colorItem = (OpenHABItem) colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    mHABWidgetCommunication.sendItemCommand(colorItem, "ON");
                return false;
            }
        });
        colorDownButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton) v;
                OpenHABItem colorItem = (OpenHABItem) colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    mHABWidgetCommunication.sendItemCommand(colorItem, "OFF");
                return false;
            }
        });
        colorColorButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton) v;
                OpenHABItem colorItem = (OpenHABItem) colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    Log.d(HABApplication.getLogTag(), "Time to launch color picker!");
                    ColorPickerDialog colorDialog = new ColorPickerDialog(v.getContext(), new OnColorChangedListener() {
                        public void colorChanged(float[] hsv, View v) {
                            Log.d(HABApplication.getLogTag(), "New color HSV = " + hsv[0] + ", " + hsv[1] + ", " +
                                    hsv[2]);
                            String newColor = String.valueOf(hsv[0]) + "," + String.valueOf(hsv[1] * 100) + "," + String.valueOf(hsv[2] * 100);
                            OpenHABItem colorItem = (OpenHABItem) v.getTag();
                            mHABWidgetCommunication.sendItemCommand(colorItem, newColor);
                        }
                    }, colorItem.getStateAsHSV());
                    colorDialog.setTag(colorItem);
                    colorDialog.show();
                }
                return false;
            }
        });

        return mViewData.widgetView;    }
}
