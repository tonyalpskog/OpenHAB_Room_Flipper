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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.entity.StringEntity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABItemType;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetMapping;
import org.openhab.habdroid.model.OpenHABWidgetType;
import org.openhab.habdroid.ui.widget.ColorPickerDialog;
import org.openhab.habdroid.ui.widget.OnColorChangedListener;
import org.openhab.habdroid.util.AutoRefreshImageView;
import org.openhab.habdroid.util.MyAsyncHttpClient;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;
import org.openhab.habdroid.ui.widget.SegmentedControlButton;

import com.crittercism.app.Crittercism;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * This class provides openHAB widgets adapter for list view.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABWidgetAdapter extends ArrayAdapter<OpenHABWidget> {
	private static final String TAG = "OpenHABWidgetAdapter";
	private String openHABBaseUrl = "http://demo.openhab.org:8080/";
	private String openHABUsername = "";
	private String openHABPassword = "";
	private ArrayList<VideoView> videoWidgetList;
	private ArrayList<AutoRefreshImageView> refreshImageList;
    private MyAsyncHttpClient mAsyncHttpClient;

	public OpenHABWidgetAdapter(Context context, int resource,
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
    	int screenWidth = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    	widgetLayout = getItem(position).getType().LayoutId;

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
                widgetView = getFrameWidget(preparedViewData);
                break;
            case Group:
                widgetView = getGroupWidget(preparedViewData);
                break;
            case SelectionSwitch:
                widgetView = getSelectionSwitchWidget(preparedViewData);
                break;
            case Switch:
                widgetView = getSwitchWidget(preparedViewData);
                break;
            case Color:
                widgetView = getColorWidget(preparedViewData);
                break;
            case RollerShutter:
                widgetView = getRollerShutterWidget(preparedViewData);
                break;
            case Text:
                widgetView = getTextWidget(preparedViewData);
                break;
            case Slider:
                widgetView = getSliderWidget(preparedViewData);
                break;
            case Image:
                widgetView = getImageWidget(preparedViewData);
                break;
            case Chart:
                widgetView = getChartWidget(preparedViewData, screenWidth);
                break;
            case Video:
                widgetView = getVideoWidget(preparedViewData);
                break;
            case Web:
                widgetView = getWebWidget(preparedViewData);
                break;
            case Selection:
                widgetView = getSelectionWidget(preparedViewData);
                break;
            case Setpoint:
                widgetView = getSetpointWidget(preparedViewData);
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
            Log.d(TAG, String.format("Setting value color to %d", valueColor));
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
            Log.d(TAG, String.format("Setting label color to %d", labelColor));
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

    private View getFrameWidget(ViewData viewData) {
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
        viewData.widgetView.setClickable(false);
        if (viewData.openHABWidget.getLabel().length() > 0) { // hide empty frames
            viewData.widgetView.setVisibility(View.VISIBLE);
            viewData.labelTextView.setVisibility(View.VISIBLE);
        } else {
            viewData.widgetView.setVisibility(View.GONE);
            viewData.labelTextView.setVisibility(View.GONE);
        }

        return viewData.widgetView;
    }

    private View getGroupWidget(ViewData viewData) {
        if (viewData.labelTextView != null && viewData.valueTextView != null) {
            viewData.splitString = viewData.openHABWidget.getLabel().split("\\[|\\]");
            viewData.labelTextView.setText(viewData.splitString[0]);
            if (viewData.splitString.length > 1) { // We have some value
                viewData.valueTextView.setText(viewData.splitString[1]);
            } else {
                // This is needed to clean up cached TextViews
                viewData.valueTextView.setText("");
            }
        }

        return viewData.widgetView;
    }

    private View getSelectionSwitchWidget(ViewData viewData) {
        viewData.splitString = viewData.openHABWidget.getLabel().split("\\[|\\]");
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.splitString[0]);
        if (viewData.splitString.length > 1 && viewData.valueTextView != null) { // We have some value
            viewData.valueTextView.setText(viewData.splitString[1]);
        } else {
            // This is needed to clean up cached TextViews
            viewData.valueTextView.setText("");
        }
        RadioGroup sectionSwitchRadioGroup = (RadioGroup)viewData.widgetView.findViewById(R.id.sectionswitchradiogroup);
        // As we create buttons in this radio in runtime, we need to remove all
        // exiting buttons first
        sectionSwitchRadioGroup.removeAllViews();
        sectionSwitchRadioGroup.setTag(viewData.openHABWidget);
        Iterator<OpenHABWidgetMapping> sectionMappingIterator = viewData.openHABWidget.getMappings().iterator();
        while (sectionMappingIterator.hasNext()) {
            OpenHABWidgetMapping widgetMapping = sectionMappingIterator.next();
            SegmentedControlButton segmentedControlButton =
                    (SegmentedControlButton)LayoutInflater.from(sectionSwitchRadioGroup.getContext()).inflate(
                            R.layout.openhabwidgetlist_sectionswitchitem_button, sectionSwitchRadioGroup, false);
            segmentedControlButton.setText(widgetMapping.getLabel());
            segmentedControlButton.setTag(widgetMapping.getCommand());
            if (viewData.openHABWidget.getItem() != null && widgetMapping.getCommand() != null) {
                if (widgetMapping.getCommand().equals(viewData.openHABWidget.getItem().getState())) {
                    segmentedControlButton.setChecked(true);
                } else {
                    segmentedControlButton.setChecked(false);
                }
            } else {
                segmentedControlButton.setChecked(false);
            }
            segmentedControlButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Button clicked");
                    RadioGroup group = (RadioGroup)view.getParent();
                    if (group.getTag() != null) {
                        OpenHABWidget radioWidget = (OpenHABWidget)group.getTag();
                        SegmentedControlButton selectedButton = (SegmentedControlButton)view;
                        if (selectedButton.getTag() != null) {
                            sendItemCommand(radioWidget.getItem(), (String)selectedButton.getTag());
                        }
                    }
                }
            });
            sectionSwitchRadioGroup.addView(segmentedControlButton);
        }


        sectionSwitchRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                OpenHABWidget radioWidget = (OpenHABWidget)group.getTag();
                SegmentedControlButton selectedButton = (SegmentedControlButton)group.findViewById(checkedId);
                if (selectedButton != null) {
                    Log.d(TAG, "Selected " + selectedButton.getText());
                    Log.d(TAG, "Command = " + (String)selectedButton.getTag());
                    //						radioWidget.getItem().sendCommand((String)selectedButton.getTag());
                    sendItemCommand(radioWidget.getItem(), (String)selectedButton.getTag());
                }
            }
        });

        return viewData.widgetView;
    }

    private View getSwitchWidget(ViewData viewData) {
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
        Switch switchSwitch = (Switch)viewData.widgetView.findViewById(R.id.switchswitch);
        if (viewData.openHABWidget.hasItem()) {
            if (viewData.openHABWidget.getItem().getState().equals("ON")) {
                switchSwitch.setChecked(true);
            } else {
                switchSwitch.setChecked(false);
            }
        }
        switchSwitch.setTag(viewData.openHABWidget.getItem());
        switchSwitch.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                Switch switchSwitch = (Switch)v;
                OpenHABItem linkedItem = (OpenHABItem)switchSwitch.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    if (!switchSwitch.isChecked()) {
                        sendItemCommand(linkedItem, "ON");
                    } else {
                        sendItemCommand(linkedItem, "OFF");
                    }
                return false;
            }
        });

        return viewData.widgetView;
    }

    private View getColorWidget(ViewData viewData) {
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
        ImageButton colorUpButton = (ImageButton)viewData.widgetView.findViewById(R.id.colorbutton_up);
        ImageButton colorDownButton = (ImageButton)viewData.widgetView.findViewById(R.id.colorbutton_down);
        ImageButton colorColorButton = (ImageButton)viewData.widgetView.findViewById(R.id.colorbutton_color);
        colorUpButton.setTag(viewData.openHABWidget.getItem());
        colorDownButton.setTag(viewData.openHABWidget.getItem());
        colorColorButton.setTag(viewData.openHABWidget.getItem());
        colorUpButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton)v;
                OpenHABItem colorItem = (OpenHABItem)colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    sendItemCommand(colorItem, "ON");
                return false;
            }
        });
        colorDownButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton) v;
                OpenHABItem colorItem = (OpenHABItem) colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    sendItemCommand(colorItem, "OFF");
                return false;
            }
        });
        colorColorButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton colorButton = (ImageButton)v;
                OpenHABItem colorItem = (OpenHABItem)colorButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Time to launch color picker!");
                    ColorPickerDialog colorDialog = new ColorPickerDialog(v.getContext(), new OnColorChangedListener() {
                        public void colorChanged(float[] hsv, View v) {
                            Log.d(TAG, "New color HSV = " + hsv[0] + ", " + hsv[1] + ", " +
                                    hsv[2]);
                            String newColor = String.valueOf(hsv[0]) + "," + String.valueOf(hsv[1]*100) + "," + String.valueOf(hsv[2]*100);
                            OpenHABItem colorItem = (OpenHABItem) v.getTag();
                            sendItemCommand(colorItem, newColor);
                        }
                    }, colorItem.getStateAsHSV());
                    colorDialog.setTag(colorItem);
                    colorDialog.show();
                }
                return false;
            }
        });

        return viewData.widgetView;
    }

    public View getRollerShutterWidget(ViewData viewData) {
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
        ImageButton rollershutterUpButton = (ImageButton)viewData.widgetView.findViewById(R.id.rollershutterbutton_up);
        ImageButton rollershutterStopButton = (ImageButton)viewData.widgetView.findViewById(R.id.rollershutterbutton_stop);
        ImageButton rollershutterDownButton = (ImageButton)viewData.widgetView.findViewById(R.id.rollershutterbutton_down);
        rollershutterUpButton.setTag(viewData.openHABWidget.getItem());
        rollershutterStopButton.setTag(viewData.openHABWidget.getItem());
        rollershutterDownButton.setTag(viewData.openHABWidget.getItem());
        rollershutterUpButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton)v;
                OpenHABItem rollershutterItem = (OpenHABItem)rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    sendItemCommand(rollershutterItem, "UP");
                return false;
            }
        });
        rollershutterStopButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton) v;
                OpenHABItem rollershutterItem = (OpenHABItem) rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    sendItemCommand(rollershutterItem, "STOP");
                return false;
            }
        });
        rollershutterDownButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ImageButton rollershutterButton = (ImageButton)v;
                OpenHABItem rollershutterItem = (OpenHABItem)rollershutterButton.getTag();
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                    sendItemCommand(rollershutterItem, "DOWN");
                return false;
            }
        });

        return viewData.widgetView;
    }

    private View getTextWidget(ViewData viewData) {
        viewData.splitString = viewData.openHABWidget.getLabel().split("\\[|\\]");
        if (viewData.labelTextView != null)
            if (viewData.splitString.length > 0) {
                viewData.labelTextView.setText(viewData.splitString[0]);
            } else {
                viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
            }

        if (viewData.valueTextView != null)
            if (viewData.splitString.length > 1) {
                // If value is not empty, show TextView
                viewData.valueTextView.setVisibility(View.VISIBLE);
                viewData.valueTextView.setText(viewData.splitString[1]);
            } else {
                // If value is empty, hide TextView to fix vertical alignment of label
                viewData.valueTextView.setVisibility(View.GONE);
                viewData.valueTextView.setText("");
            }

        return viewData.widgetView;
    }

    private View getSliderWidget(ViewData viewData) {
        viewData.splitString = viewData.openHABWidget.getLabel().split("\\[|\\]");
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.splitString[0]);
        SeekBar sliderSeekBar = (SeekBar)viewData.widgetView.findViewById(R.id.sliderseekbar);
        if (viewData.openHABWidget.hasItem()) {
            sliderSeekBar.setTag(viewData.openHABWidget.getItem());
            int sliderState = 0;
            try {
                sliderState = (int)Float.parseFloat(viewData.openHABWidget.getItem().getState());
            } catch (NumberFormatException e) {
                if (e != null) {
                    Crittercism.logHandledException(e);
                    Log.e(TAG, e.getMessage());
                }
                if (viewData.openHABWidget.getItem().getState().equals("OFF")) {
                    sliderState = 0;
                } else if (viewData.openHABWidget.getItem().getState().equals("ON")) {
                    sliderState = 100;
                }
            }
            sliderSeekBar.setProgress(sliderState);
            sliderSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                }
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG, "onStartTrackingTouch position = " + seekBar.getProgress());
                }
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG, "onStopTrackingTouch position = " + seekBar.getProgress());
                    OpenHABItem sliderItem = (OpenHABItem)seekBar.getTag();
                    //							sliderItem.sendCommand(String.valueOf(seekBar.getProgress()));
                    if (sliderItem != null && seekBar != null)
                        sendItemCommand(sliderItem, String.valueOf(seekBar.getProgress()));
                }
            });
        }

        return viewData.widgetView;
    }

    private View getImageWidget(ViewData viewData) {
        AutoRefreshImageView imageImage = (AutoRefreshImageView)viewData.widgetView.findViewById(R.id.imageimage);
        imageImage.setImageUrl(ensureAbsoluteURL(openHABBaseUrl, viewData.openHABWidget.getUrl()), false,
                openHABUsername, openHABPassword);
        //    		ViewGroup.LayoutParams imageLayoutParams = imageImage.getLayoutParams();
        //    		float imageRatio = imageImage.getDrawable().getIntrinsicWidth()/imageImage.getDrawable().getIntrinsicHeight();
        //    		imageLayoutParams.height = (int) (screenWidth/imageRatio);
        //    		imageImage.setLayoutParams(imageLayoutParams);
        if (viewData.openHABWidget.getRefresh() > 0) {
            imageImage.setRefreshRate(viewData.openHABWidget.getRefresh());
            refreshImageList.add(imageImage);
        }

        return viewData.widgetView;
    }

    private View getChartWidget(ViewData viewData, int screenWidth) {
        AutoRefreshImageView chartImage = (AutoRefreshImageView)viewData.widgetView.findViewById(R.id.chartimage);
        OpenHABItem chartItem = viewData.openHABWidget.getItem();
        Random random = new Random();
        String chartUrl = "";
        if (chartItem != null) {
            if (chartItem.getType() == OpenHABItemType.Group) {
                chartUrl = openHABBaseUrl + "rrdchart.png?groups=" + chartItem.getName() +
                        "&period=" + viewData.openHABWidget.getPeriod() + "&random=" +
                        String.valueOf(random.nextInt());
            } else {
                chartUrl = openHABBaseUrl + "rrdchart.png?items=" + chartItem.getName() +
                        "&period=" + viewData.openHABWidget.getPeriod() + "&random=" +
                        String.valueOf(random.nextInt());
            }
        }
        Log.d(TAG, "Chart url = " + chartUrl);
        if (chartImage == null)
            Log.e(TAG, "chartImage == null !!!");
        //    		if (openHABUsername != null && openHABPassword != null)
        chartImage.setImageUrl(chartUrl, false, openHABUsername, openHABPassword);
        //    		else
        //    			chartImage.setImageUrl(chartUrl, false);
        // TODO: This is quite dirty fix to make charts look full screen width on all displays
        ViewGroup.LayoutParams chartLayoutParams = chartImage.getLayoutParams();
        chartLayoutParams.height = (int) (screenWidth/1.88);
        chartImage.setLayoutParams(chartLayoutParams);
        if (viewData.openHABWidget.getRefresh() > 0) {
            chartImage.setRefreshRate(viewData.openHABWidget.getRefresh());
            refreshImageList.add(chartImage);
        }
        Log.d(TAG, "chart size = " + chartLayoutParams.width + " " + chartLayoutParams.height);

        return viewData.widgetView;
    }

    private View getVideoWidget(ViewData viewData) {
        VideoView videoVideo = (VideoView)viewData.widgetView.findViewById(R.id.videovideo);
        Log.d(TAG, "Opening video at " + viewData.openHABWidget.getUrl());
        // TODO: This is quite dirty fix to make video look maximum available size on all screens
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams videoLayoutParams = videoVideo.getLayoutParams();
        videoLayoutParams.height = (int)(wm.getDefaultDisplay().getWidth()/1.77);
        videoVideo.setLayoutParams(videoLayoutParams);
        // We don't have any event handler to know if the VideoView is on the screen
        // so we manage an array of all videos to stop them when user leaves the page
        if (!videoWidgetList.contains(videoVideo))
            videoWidgetList.add(videoVideo);
        // Start video
        if (!videoVideo.isPlaying()) {
            videoVideo.setVideoURI(Uri.parse(viewData.openHABWidget.getUrl()));
            videoVideo.start();
        }
        Log.d(TAG, "Video height is " + videoVideo.getHeight());

        return viewData.widgetView;
    }

    private View getWebWidget(ViewData viewData) {
        WebView webWeb = (WebView)viewData.widgetView.findViewById(R.id.webweb);
        if (viewData.openHABWidget.getHeight() > 0) {
            ViewGroup.LayoutParams webLayoutParams = webWeb.getLayoutParams();
            webLayoutParams.height = viewData.openHABWidget.getHeight() * 80;
            webWeb.setLayoutParams(webLayoutParams);
        }
        webWeb.setWebViewClient(new WebViewClient());
        webWeb.loadUrl(viewData.openHABWidget.getUrl());

        return viewData.widgetView;
    }

    private View getSelectionWidget(ViewData viewData) {
        int spinnerSelectedIndex = -1;
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.openHABWidget.getLabel());
        Spinner selectionSpinner = (Spinner)viewData.widgetView.findViewById(R.id.selectionspinner);
        ArrayList<String> spinnerArray = new ArrayList<String>();
        Iterator<OpenHABWidgetMapping> mappingIterator = viewData.openHABWidget.getMappings().iterator();
        while (mappingIterator.hasNext()) {
            OpenHABWidgetMapping openHABWidgetMapping = mappingIterator.next();
            spinnerArray.add(openHABWidgetMapping.getLabel());
            if (openHABWidgetMapping.getCommand().equals(viewData.openHABWidget.getItem().getState())) {
                spinnerSelectedIndex = spinnerArray.size() - 1;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getContext() ,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectionSpinner.setAdapter(spinnerAdapter);
        selectionSpinner.setTag(viewData.openHABWidget);
        if (spinnerSelectedIndex >= 0)
            selectionSpinner.setSelection(spinnerSelectedIndex);

        selectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int index, long id) {
                Log.d(TAG, "Spinner item click on index " + index);
                Spinner spinner = (Spinner) parent;
                String selectedLabel = (String) spinner.getAdapter().getItem(index);
                Log.d(TAG, "Spinner onItemSelected selected label = " + selectedLabel);
                OpenHABWidget openHABWidget = (OpenHABWidget) parent.getTag();
                if (openHABWidget != null) {
                    Log.d(TAG, "Label selected = " + openHABWidget.getMapping(index).getLabel());
                    Iterator<OpenHABWidgetMapping> mappingIterator = openHABWidget.getMappings().iterator();
                    while (mappingIterator.hasNext()) {
                        OpenHABWidgetMapping openHABWidgetMapping = mappingIterator.next();
                        if (openHABWidgetMapping.getLabel().equals(selectedLabel)) {
                            Log.d(TAG, "Spinner onItemSelected found match with " + openHABWidgetMapping.getCommand());
                            if (!openHABWidget.getItem().getState().equals(openHABWidgetMapping.getCommand())) {
                                Log.d(TAG, "Spinner onItemSelected selected label command != current item state");
                                sendItemCommand(openHABWidget.getItem(), openHABWidgetMapping.getCommand());
                            }
                        }
                    }
                }
                //					if (!openHABWidget.getItem().getState().equals(openHABWidget.getMapping(index).getCommand()))
                //						sendItemCommand(openHABWidget.getItem(),
                //								openHABWidget.getMapping(index).getCommand());
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        return viewData.widgetView;
    }

    private View getSetpointWidget(ViewData viewData) {
        viewData.splitString = viewData.openHABWidget.getLabel().split("\\[|\\]");
        if (viewData.labelTextView != null)
            viewData.labelTextView.setText(viewData.splitString[0]);
        if (viewData.valueTextView != null)
            if (viewData.splitString.length > 1) {
                // If value is not empty, show TextView
                viewData.valueTextView.setVisibility(View.VISIBLE);
                viewData.valueTextView.setText(viewData.splitString[1]);
            }
        Button setPointMinusButton = (Button)viewData.widgetView.findViewById(R.id.setpointbutton_minus);
        Button setPointPlusButton = (Button)viewData.widgetView.findViewById(R.id.setpointbutton_plus);
        setPointMinusButton.setTag(viewData.openHABWidget);
        setPointPlusButton.setTag(viewData.openHABWidget);
        setPointMinusButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Minus");
                OpenHABWidget setPointWidget = (OpenHABWidget)v.getTag();
                float currentValue = Float.valueOf(setPointWidget.getItem().getState()).floatValue();
                currentValue = currentValue - setPointWidget.getStep();
                if (currentValue < setPointWidget.getMinValue())
                    currentValue = setPointWidget.getMinValue();
                if (currentValue > setPointWidget.getMaxValue())
                    currentValue = setPointWidget.getMaxValue();
                sendItemCommand(setPointWidget.getItem(), String.valueOf(currentValue));

            }
        });
        setPointPlusButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Plus");
                OpenHABWidget setPointWidget = (OpenHABWidget)v.getTag();
                float currentValue = Float.valueOf(setPointWidget.getItem().getState()).floatValue();
                currentValue = currentValue + setPointWidget.getStep();
                if (currentValue < setPointWidget.getMinValue())
                    currentValue = setPointWidget.getMinValue();
                if (currentValue > setPointWidget.getMaxValue())
                    currentValue = setPointWidget.getMaxValue();
                sendItemCommand(setPointWidget.getItem(), String.valueOf(currentValue));
            }
        });

        return viewData.widgetView;
    }

    @Override
    public int getViewTypeCount() {
        return OpenHABWidgetType.values().length;
    }
    
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().Id;
    }
	
    public void setOpenHABBaseUrl(String baseUrl) {
    	openHABBaseUrl = baseUrl;
    }
    
    private String ensureAbsoluteURL(String base, String maybeRelative) {
        if (maybeRelative.startsWith("http")) {
            return maybeRelative;
        } else {
            try {
               return new URL(new URL(base), maybeRelative).toExternalForm();
            } catch (MalformedURLException e) {
               return "";
            }
        }
    }
    
    public void sendItemCommand(OpenHABItem item, String command) {
        try {
            Log.d(TAG, String.format("sendItemCommand() -> OpenHABItem = '%s'   command = '%s'", item.getLink(), command));
            StringEntity se = new StringEntity(command);
            mAsyncHttpClient.post(getContext(), item.getLink(), se, "text/plain", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "Command was sent successfully");
                }
                @Override
                public void onFailure(Throwable error, String errorResponse) {
                    Log.e(TAG, "Got command error " + error.getMessage());
                    if (errorResponse != null)
                        Log.e(TAG, "Error response = " + errorResponse);
                }
            });
        } catch (UnsupportedEncodingException e) {
            if (e != null)
            Log.e(TAG, e.getMessage());
        }
    }

	public String getOpenHABUsername() {
		return openHABUsername;
	}

	public void setOpenHABUsername(String openHABUsername) {
		this.openHABUsername = openHABUsername;
	}

	public String getOpenHABPassword() {
		return openHABPassword;
	}

	public void setOpenHABPassword(String openHABPassword) {
		this.openHABPassword = openHABPassword;
	}
	
	public void stopVideoWidgets() {
		Log.d(TAG, "Stopping video for " + videoWidgetList.size() + " widgets");
		for (int i=0; i<videoWidgetList.size(); i++) {
			if (videoWidgetList.get(i) != null)
				videoWidgetList.get(i).stopPlayback();
		}
		videoWidgetList.clear();
	}
	
	public void stopImageRefresh() {
		Log.d(TAG, "Stopping image refresh for " + refreshImageList.size() + " widgets");
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

    private class ViewData {
        public RelativeLayout widgetView;
        public OpenHABWidget openHABWidget;
        public String[] splitString;
        public TextView labelTextView;
        public TextView valueTextView;
    }

}
