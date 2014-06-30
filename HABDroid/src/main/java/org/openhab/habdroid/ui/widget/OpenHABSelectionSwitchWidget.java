package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import org.openhab.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetMapping;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

import java.util.Iterator;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSelectionSwitchWidget extends OpenHABWidgetBase {

    public OpenHABSelectionSwitchWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        mViewData.splitString = mViewData.openHABWidget.getLabel().split("\\[|\\]");
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.splitString[0]);
        if (mViewData.splitString.length > 1 && mViewData.valueTextView != null) { // We have some value
            mViewData.valueTextView.setText(mViewData.splitString[1]);
        } else {
            // This is needed to clean up cached TextViews
            mViewData.valueTextView.setText("");
        }
        RadioGroup sectionSwitchRadioGroup = (RadioGroup)mViewData.widgetView.findViewById(R.id.sectionswitchradiogroup);
        // As we create buttons in this radio in runtime, we need to remove all
        // exiting buttons first
        sectionSwitchRadioGroup.removeAllViews();
        sectionSwitchRadioGroup.setTag(mViewData.openHABWidget);
        Iterator<OpenHABWidgetMapping> sectionMappingIterator = mViewData.openHABWidget.getMappings().iterator();
        while (sectionMappingIterator.hasNext()) {
            OpenHABWidgetMapping widgetMapping = sectionMappingIterator.next();
            SegmentedControlButton segmentedControlButton =
                    (SegmentedControlButton) LayoutInflater.from(sectionSwitchRadioGroup.getContext()).inflate(
                            R.layout.openhabwidgetlist_sectionswitchitem_button, sectionSwitchRadioGroup, false);
            segmentedControlButton.setText(widgetMapping.getLabel());
            segmentedControlButton.setTag(widgetMapping.getCommand());
            if (mViewData.openHABWidget.getItem() != null && widgetMapping.getCommand() != null) {
                if (widgetMapping.getCommand().equals(mViewData.openHABWidget.getItem().getState())) {
                    segmentedControlButton.setChecked(true);
                } else {
                    segmentedControlButton.setChecked(false);
                }
            } else {
                segmentedControlButton.setChecked(false);
            }
            segmentedControlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(HABApplication.getLogTag(), "Button clicked");
                    RadioGroup group = (RadioGroup)view.getParent();
                    if (group.getTag() != null) {
                        OpenHABWidget radioWidget = (OpenHABWidget)group.getTag();
                        SegmentedControlButton selectedButton = (SegmentedControlButton)view;
                        if (selectedButton.getTag() != null) {
                            mHABWidgetCommunication.sendItemCommand(radioWidget.getItem(), (String) selectedButton.getTag());
                        }
                    }
                }
            });
            sectionSwitchRadioGroup.addView(segmentedControlButton);
        }


        sectionSwitchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                OpenHABWidget radioWidget = (OpenHABWidget) group.getTag();
                SegmentedControlButton selectedButton = (SegmentedControlButton) group.findViewById(checkedId);
                if (selectedButton != null) {
                    Log.d(HABApplication.getLogTag(), "Selected " + selectedButton.getText());
                    Log.d(HABApplication.getLogTag(), "Command = " + (String) selectedButton.getTag());
                    //						radioWidget.getItem().sendCommand((String)selectedButton.getTag());
                    mHABWidgetCommunication.sendItemCommand(radioWidget.getItem(), (String) selectedButton.getTag());
                }
            }
        });

        return mViewData.widgetView;
    }
}
