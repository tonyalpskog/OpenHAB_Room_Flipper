package org.openhab.habclient;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABItem;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.habdroid.util.MyAsyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetControl implements IOpenHABWidgetControl {
    private final Context mContext;
    private final IOpenHABWidgetProvider mWidgetProvider;
    private final MyAsyncHttpClient mAsyncHttpClient;

    @Inject
    public OpenHABWidgetControl(Context context, IOpenHABWidgetProvider widgetProvider) {
        if(context == null) throw new IllegalArgumentException("context is null");
        mContext = context;
        mWidgetProvider = widgetProvider;
        mAsyncHttpClient = new MyAsyncHttpClient(context);
    }

    @Override
    public boolean sendItemCommandFromWidget(String widgetId, String command) {
        OpenHABWidget widget = mWidgetProvider.getWidgetByID(widgetId);
        if(widget == null || !widget.hasItem())
            return false;
        sendItemCommand(widget, command);
        return true;
    }

    @Override
    public void sendItemCommand(String itemName, String command) {
        sendItemCommand(mWidgetProvider.getWidgetByItemName(itemName), command);
    }

    @Override
    public void sendItemCommand(final OpenHABWidget habWidget, String command) {
        try {
            Log.d(HABApplication.getLogTag(), String.format("sendItemCommand() -> OpenHABItem = '%s'   command = '%s'", habWidget.getItem().getLink(), command));
            StringEntity se = new StringEntity(command);
            mAsyncHttpClient.post(mContext, habWidget.getItem().getLink(), se, "text/plain", new AsyncHttpResponseHandler() {
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
            Log.e(HABApplication.getLogTag(), e.getMessage());
        }
    }

    public View initializeSwitchWidget(final OpenHABWidget openHABWidget, View inflatedView) {
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
                        sendItemCommand(openHABWidget, "ON");
                    } else {
                        sendItemCommand(openHABWidget, "OFF");
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
