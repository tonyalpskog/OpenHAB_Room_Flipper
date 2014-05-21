package com.zenit.habclient;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.util.MyAsyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetControl {
    private Context mContext;

    public OpenHABWidgetControl(Context context) {
        mContext = context;
        mAsyncHttpClient = new MyAsyncHttpClient(context);
    }

    private MyAsyncHttpClient mAsyncHttpClient;

    public MyAsyncHttpClient getAsyncHttpClient() {
        return mAsyncHttpClient;
    }

    public void setAsyncHttpClient(MyAsyncHttpClient asyncHttpClient) {
        mAsyncHttpClient = asyncHttpClient;
    }

    public boolean sendItemCommandFromWidget(String widgetId, String command) {
        OpenHABWidget widget = HABApplication.getOpenHABWidgetProvider2().getWidgetByID(widgetId);
        if(widget == null || !widget.hasItem())
            return false;
        sendItemCommand(widget.getItem(), command);
        return true;
    }

    public void sendItemCommand(String itemName, String command) {
        sendItemCommand(HABApplication.getOpenHABWidgetProvider2().getWidgetByItemName(itemName).getItem(), command);
    }

    public void sendItemCommand(OpenHABItem item, String command) {
        try {
            Log.d(HABApplication.getLogTag(), String.format("sendItemCommand() -> OpenHABItem = '%s'   command = '%s'", item.getLink(), command));
            StringEntity se = new StringEntity(command);
            mAsyncHttpClient.post(mContext, item.getLink(), se, "text/plain", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.d(HABApplication.getLogTag(), "Command was sent successfully");
                }
                @Override
                public void onFailure(Throwable error, String errorResponse) {
                    Log.e(HABApplication.getLogTag(), "Got command error " + error.getMessage());
                    if (errorResponse != null)
                        Log.e(HABApplication.getLogTag(), "Error response = " + errorResponse);
                }
            });
        } catch (UnsupportedEncodingException e) {
            if (e != null)
                Log.e(HABApplication.getLogTag(), e.getMessage());
        }
    }

    public View initializeSwitchWidget(OpenHABWidget openHABWidget, View inflatedView) {
        final Switch switchView = (Switch)inflatedView.findViewById(R.id.switchswitch);

        if(switchView == null || openHABWidget.getItem() == null) {
            Log.e(HABApplication.getLogTag(), switchView == null? "switchView": "openHABItem" + " = NULL");
            return null;
        }

        if (openHABWidget.getItem().getState().equals("ON")) {
            switchView.setChecked(true);
        } else {
            switchView.setChecked(false);
        }

        switchView.setTag(openHABWidget.getItem());
        switchView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                Switch switchSwitch = (Switch) v;
                OpenHABItem linkedItem = (OpenHABItem) switchSwitch.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    if (!switchSwitch.isChecked()) {
                        sendItemCommand(linkedItem, "ON");
                    } else {
                        sendItemCommand(linkedItem, "OFF");
                    }
                return false;
            }
        });

        return switchView;
    }

    public View initializeTextWidget(OpenHABWidget openHABWidget, View inflatedView) {
        Integer labelColor = openHABWidget.getLabelColor();
        TextView itemValueTextView = (TextView) inflatedView.findViewById(R.id.widgetlabel);

        OpenHABItem openHABItem = openHABWidget.getItem();

        if(itemValueTextView == null || openHABItem == null) {
            Log.e(HABApplication.getLogTag(), itemValueTextView == null? "itemValueTextView": "openHABItem" + " = NULL");
            return null;
        }

        if(labelColor != null) {
            Log.d(HABApplication.getLogTag(), String.format("Setting label color to %d", labelColor));
            itemValueTextView.setTextColor(labelColor);
        }

        if (openHABWidget.getLabel() != null && !openHABWidget.getLabel().isEmpty()) {
            itemValueTextView.setText(getShortOpenHABWidgetLabelValue(openHABWidget.getLabel()));
        }

        return itemValueTextView;
    }

    @Deprecated //Use method in OpenHABWidget class.
    private String getShortOpenHABWidgetLabelValue(String openHABWidgetLabel) {
        return getRegExMatch(openHABWidgetLabel, Pattern.compile("\\[.*\\]", Pattern.CASE_INSENSITIVE));
    }

    private String getRegExMatch(String source, Pattern pattern) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find())
            result = (matcher.group().subSequence(1, matcher.group().length()-1)).toString();

        return result;
    }

    private String getRegExReplaceMatch(String source, Pattern pattern, String replacement) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find()) {
            matcher.replaceAll(replacement);
            result = (matcher.group().subSequence(1, matcher.group().length() - 1)).toString();
        }

        return result;
    }
}
