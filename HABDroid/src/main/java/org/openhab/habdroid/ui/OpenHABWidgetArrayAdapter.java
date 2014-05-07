/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zenit.habclient.HABApplication;

import org.apache.http.entity.StringEntity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetType;
import org.openhab.habdroid.ui.widget.IHABWidgetCommunication;
import org.openhab.habdroid.ui.widget.OpenHABChartWidget;
import org.openhab.habdroid.ui.widget.OpenHABColorWidget;
import org.openhab.habdroid.ui.widget.OpenHABFrameWidget;
import org.openhab.habdroid.ui.widget.OpenHABGroupWidget;
import org.openhab.habdroid.ui.widget.OpenHABImageWidget;
import org.openhab.habdroid.ui.widget.OpenHABRollerShutterWidget;
import org.openhab.habdroid.ui.widget.OpenHABSelectionSwitchWidget;
import org.openhab.habdroid.ui.widget.OpenHABSelectionWidget;
import org.openhab.habdroid.ui.widget.OpenHABSetpointWidget;
import org.openhab.habdroid.ui.widget.OpenHABSliderWidget;
import org.openhab.habdroid.ui.widget.OpenHABSwitchWidget;
import org.openhab.habdroid.ui.widget.OpenHABTextWidget;
import org.openhab.habdroid.ui.widget.OpenHABVideoWidget;
import org.openhab.habdroid.ui.widget.OpenHABWebWidget;
import org.openhab.habdroid.util.AutoRefreshImageView;
import org.openhab.habdroid.util.MyAsyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides openHAB widgets adapter for list view.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABWidgetArrayAdapter extends ArrayAdapter<OpenHABWidget> implements IHABWidgetCommunication {
	private String openHABBaseUrl = "https://demo.openhab.org:8443/";
	private String openHABUsername = "";
	private String openHABPassword = "";
	private ArrayList<VideoView> videoWidgetList;
	private ArrayList<AutoRefreshImageView> refreshImageList;
    private MyAsyncHttpClient mAsyncHttpClient;

	public OpenHABWidgetArrayAdapter(Context context, int resource,
                                     List<OpenHABWidget> objects) {
		super(context, resource, objects);
		// Initialize video view array
		videoWidgetList = new ArrayList<VideoView>();
		refreshImageList = new ArrayList<AutoRefreshImageView>();
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewData preparedViewData = new ViewData();
    	int widgetLayout;
    	preparedViewData.openHABWidget = getItem(position);
    	widgetLayout = getItem(position).getType().RowLayoutId;

    	if (convertView == null) {
            preparedViewData.widgetView = new RelativeLayout(getContext());
    		String inflater = Context.LAYOUT_INFLATER_SERVICE;
    		LayoutInflater vi;
    		vi = (LayoutInflater)getContext().getSystemService(inflater);
    		vi.inflate(widgetLayout, preparedViewData.widgetView, true);
    	} else {
            preparedViewData.widgetView = (RelativeLayout) convertView;
    	}

        processIconImage(preparedViewData.widgetView, preparedViewData.openHABWidget);
        preparedViewData.labelTextView = getLabelTextView(preparedViewData.widgetView, preparedViewData.openHABWidget);
        preparedViewData.valueTextView = getValueTextView(preparedViewData.widgetView, preparedViewData.openHABWidget);

        View widgetView = null;
    	switch (getItem(position).getType()) {
            case Frame:
                widgetView = new OpenHABFrameWidget(this, preparedViewData).getWidget();
                break;
            case Group:
                widgetView = new OpenHABGroupWidget(this, preparedViewData).getWidget();
                break;
            case SelectionSwitch:
                widgetView = new OpenHABSelectionSwitchWidget(this, preparedViewData).getWidget();
                break;
            case Switch:
                widgetView = new OpenHABSwitchWidget(this, preparedViewData).getWidget();
                break;
            case Color:
                widgetView = new OpenHABColorWidget(this, preparedViewData).getWidget();
                break;
            case RollerShutter:
                widgetView = new OpenHABRollerShutterWidget(this, preparedViewData).getWidget();
                break;
            case ItemText:
            case SitemapText:
                widgetView = new OpenHABTextWidget(this, preparedViewData).getWidget();
                break;
            case Slider:
                widgetView = new OpenHABSliderWidget(this, preparedViewData).getWidget();
                break;
            case Image:
                widgetView = new OpenHABImageWidget(refreshImageList, this, preparedViewData).getWidget();
                break;
            case Chart:
                int screenWidth = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
                widgetView = new OpenHABChartWidget(screenWidth, refreshImageList, this, preparedViewData).getWidget();
                break;
            case Video:
                widgetView = new OpenHABVideoWidget(this.getContext(), videoWidgetList, this, preparedViewData).getWidget();
                break;
            case Web:
                widgetView = new OpenHABWebWidget(this, preparedViewData).getWidget();
                break;
            case Selection:
                widgetView = new OpenHABSelectionWidget(this.getContext(), this, preparedViewData).getWidget();
                break;
            case Setpoint:
                widgetView = new OpenHABSetpointWidget(this.getContext(), this, preparedViewData).getWidget();
                break;
            default:
                if (preparedViewData.labelTextView != null)
                    preparedViewData.labelTextView.setText(preparedViewData.openHABWidget.getLabel());
                break;
    	}

    	LinearLayout dividerLayout = (LinearLayout)widgetView.findViewById(R.id.listdivider);
    	if (dividerLayout != null) {
    		if (position < this.getCount()-1) {
    			if (getItem(position + 1).getType() == OpenHABWidgetType.Frame) {
        			dividerLayout.setVisibility(View.GONE); // hide dividers before frame widgets
    			} else {
    				dividerLayout.setVisibility(View.VISIBLE); // show dividers for all others
    			}
    		} else { // last widget in the list, hide divider
    			dividerLayout.setVisibility(View.GONE);
    		}
    	}
    	return widgetView;
    }

    private TextView getValueTextView(RelativeLayout widgetView, OpenHABWidget openHABWidget) {
        // Get TextView for widget value and set it's color
        Integer valueColor = openHABWidget.getValueColor();
        TextView valueTextView = (TextView) widgetView.findViewById(R.id.widgetvalue);

        if (valueColor != null && valueTextView != null) {
            Log.d(HABApplication.getLogTag(), String.format("Setting value color to %d", valueColor));
            valueTextView.setTextColor(valueColor);
        } else if (valueTextView != null) {
            TextView defaultTextView = new TextView(widgetView.getContext());
            valueTextView.setTextColor(defaultTextView.getTextColors().getDefaultColor());
        }

        return valueTextView;
    }

    private TextView getLabelTextView(RelativeLayout widgetView, OpenHABWidget openHABWidget) {
        // Get TextView for widget label and set it's color
        Integer labelColor = openHABWidget.getLabelColor();
        TextView labelTextView = (TextView) widgetView.findViewById(R.id.widgetlabel);

        if(labelColor != null && labelTextView != null) {
            Log.d(HABApplication.getLogTag(), String.format("Setting label color to %d", labelColor));
            labelTextView.setTextColor(labelColor);
        } else if (labelTextView != null) {
            TextView defaultTextView = new TextView(widgetView.getContext());
            labelTextView.setTextColor(defaultTextView.getTextColors().getDefaultColor());
        }

        return labelTextView;
    }

    private void processIconImage(RelativeLayout widgetView, OpenHABWidget openHABWidget) {
        // Process widgets icon image
        Integer iconColor = openHABWidget.getIconColor();
        AutoRefreshImageView widgetImage = (AutoRefreshImageView) widgetView.findViewById(R.id.widgetimage);
        // Some of widgets, for example Frame doesnt' have an icon, so...
        if (widgetImage != null) {
            if (openHABWidget.getIcon() != null) {
                // This is needed to escape possible spaces and everything according to rfc2396
                String iconUrl = openHABBaseUrl + "images/" + Uri.encode(openHABWidget.getIcon() + ".png");
//                Log.d(TAG, "Will try to load icon from " + iconUrl);
                // Now set image URL
                widgetImage.setImageUrl(iconUrl, R.drawable.blank_icon,
                        openHABUsername, openHABPassword);
                if(iconColor != null)
                    widgetImage.setColorFilter(iconColor);
                else
                    widgetImage.clearColorFilter();
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return OpenHABWidgetType.values().length;
    }
    
    @Override
    public int getItemViewType(int position) {
        if(getItem(position) ==  null || getItem(position).getType() == null)
            Log.e(HABApplication.getLogTag(), getItem(position) ==  null? "getItem(position) ==  null" : "getItem(position).getType() == null");
        return getItem(position).getType().Id;
    }
	
    public void setOpenHABBaseUrl(String baseUrl) {
    	openHABBaseUrl = baseUrl;
    }
    
    public void sendItemCommand(OpenHABItem item, String command) {
        try {
            Log.d(HABApplication.getLogTag(), String.format("[AsyncHttpClient] POST Request for OpenHABItem = '%s'   command = '%s'", item.getLink(), command));
            StringEntity se = new StringEntity(command);
            mAsyncHttpClient.post(getContext(), item.getLink(), se, "text/plain", new AsyncHttpResponseHandler() {
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

    @Override
    public String getNewValueAsFullText(String currentFullTextValue, float value) {
        String textValue = getRegExMatch(currentFullTextValue, Pattern.compile("\\d*\\[.,]?\\d*", Pattern.CASE_INSENSITIVE));
        return currentFullTextValue.replaceFirst("\\d*\\[.,]?\\d*", Float.toString(value));
    }

    public String getOpenHABBaseUrl() {
        return openHABBaseUrl;
    }

    public String getOpenHABUsername() {
        return openHABUsername;
    }

    public String getOpenHABPassword() {
        return openHABPassword;
    }

    private String getRegExMatch(String source, Pattern pattern) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find())
            result = (matcher.group().subSequence(1, matcher.group().length()-1)).toString();

        return result;
    }

	public void setOpenHABUsername(String openHABUsername) {
		this.openHABUsername = openHABUsername;
	}

	public void setOpenHABPassword(String openHABPassword) {
		this.openHABPassword = openHABPassword;
	}
	
	public void stopVideoWidgets() {
		Log.d(HABApplication.getLogTag(), "Stopping video for " + videoWidgetList.size() + " widgets");
		for (int i=0; i<videoWidgetList.size(); i++) {
			if (videoWidgetList.get(i) != null)
				videoWidgetList.get(i).stopPlayback();
		}
		videoWidgetList.clear();
	}
	
	public void stopImageRefresh() {
		Log.d(HABApplication.getLogTag(), "Stopping image refresh for " + refreshImageList.size() + " widgets");
		for (int i=0; i<refreshImageList.size(); i++) {
			if (refreshImageList.get(i) != null)
				refreshImageList.get(i).cancelRefresh();
		}
		refreshImageList.clear();
	}

    public MyAsyncHttpClient getAsyncHttpClient() {
        return mAsyncHttpClient;
    }

    public void setAsyncHttpClient(MyAsyncHttpClient asyncHttpClient) {
        mAsyncHttpClient = asyncHttpClient;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        OpenHABWidget openHABWidget = getItem(position);
        if (openHABWidget.getType() == OpenHABWidgetType.Frame)
            return false;
        return true;
    }

    public class ViewData {
        public RelativeLayout widgetView;
        public OpenHABWidget openHABWidget;
        public String[] splitString;
        public TextView labelTextView;
        public TextView valueTextView;
    }

}
