package org.openhab.habdroid.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;
import org.openhab.habdroid.ui.SecondaryTouchListener;
import org.openhab.habdroid.ui.TouchRepeatListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSetpointWidget extends OpenHABWidgetBase {
    private Context mActivityContext;

    public OpenHABSetpointWidget(Context activityContext, IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
        mActivityContext = activityContext;
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

        SecondaryTouchListener.OnSecondaryClickListener valueTextOnSecondaryClickListener = new SecondaryTouchListener.OnSecondaryClickListener() {
            @Override
            public boolean onSecondary(View v, SecondaryTouchListener.SecondaryClickEvent event) {
                if(event == SecondaryTouchListener.SecondaryClickEvent.Down) {
                    Toast.makeText(getWidget().getContext(), "A numeric input dialog will be shown...", Toast.LENGTH_LONG).show();
//                    EditText txtName = new EditText(mActivityContext);
//                    txtName.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
//                    txtName.requestFocus();

                    InputMethodManager imm = (InputMethodManager) mActivityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN);

//                    ((InputMethodManager) mActivityContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(txtName, InputMethodManager.SHOW_FORCED);
//                } else if(event == SecondaryTouchListener.SecondaryClickEvent.Up) {
//                    ((InputMethodManager) mActivityContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtName.getWindowToken(), 0);
                }
                return false;
            }
        };

        mViewData.valueTextView.setOnTouchListener(new SecondaryTouchListener(valueTextOnSecondaryClickListener));

        return mViewData.widgetView;
    }
}
