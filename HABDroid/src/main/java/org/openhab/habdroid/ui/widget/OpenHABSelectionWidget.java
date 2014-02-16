package org.openhab.habdroid.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zenit.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetMapping;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSelectionWidget extends OpenHABWidgetBase {
    private Context mContext;

    public OpenHABSelectionWidget(Context context, IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
        mContext = context;
    }

    @Override
    public View getWidget() {
        int spinnerSelectedIndex = -1;
        if (mViewData.labelTextView != null)
            mViewData.labelTextView.setText(mViewData.openHABWidget.getLabel());
        Spinner selectionSpinner = (Spinner)mViewData.widgetView.findViewById(R.id.selectionspinner);
        ArrayList<String> spinnerArray = new ArrayList<String>();
        Iterator<OpenHABWidgetMapping> mappingIterator = mViewData.openHABWidget.getMappings().iterator();
        while (mappingIterator.hasNext()) {
            OpenHABWidgetMapping openHABWidgetMapping = mappingIterator.next();
            spinnerArray.add(openHABWidgetMapping.getLabel());
            if (openHABWidgetMapping.getCommand().equals(mViewData.openHABWidget.getItem().getState())) {
                spinnerSelectedIndex = spinnerArray.size() - 1;
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext ,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectionSpinner.setAdapter(spinnerAdapter);
        selectionSpinner.setTag(mViewData.openHABWidget);
        if (spinnerSelectedIndex >= 0)
            selectionSpinner.setSelection(spinnerSelectedIndex);

        selectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int index, long id) {
                Log.d(HABApplication.getLogTag(), "Spinner item click on index " + index);
                Spinner spinner = (Spinner) parent;
                String selectedLabel = (String) spinner.getAdapter().getItem(index);
                Log.d(HABApplication.getLogTag(), "Spinner onItemSelected selected label = " + selectedLabel);
                OpenHABWidget openHABWidget = (OpenHABWidget) parent.getTag();
                if (openHABWidget != null) {
                    Log.d(HABApplication.getLogTag(), "Label selected = " + openHABWidget.getMapping(index).getLabel());
                    Iterator<OpenHABWidgetMapping> mappingIterator = openHABWidget.getMappings().iterator();
                    while (mappingIterator.hasNext()) {
                        OpenHABWidgetMapping openHABWidgetMapping = mappingIterator.next();
                        if (openHABWidgetMapping.getLabel().equals(selectedLabel)) {
                            Log.d(HABApplication.getLogTag(), "Spinner onItemSelected found match with " + openHABWidgetMapping.getCommand());
                            if (!openHABWidget.getItem().getState().equals(openHABWidgetMapping.getCommand())) {
                                Log.d(HABApplication.getLogTag(), "Spinner onItemSelected selected label command != current item state");
                                mHABWidgetCommunication.sendItemCommand(openHABWidget.getItem(), openHABWidgetMapping.getCommand());
                            }
                        }
                    }
                }
                //					if (!openHABWidget.getItem().getState().equals(openHABWidget.getMapping(index).getCommand()))
                //						mHABWidgetCommunication.sendItemCommand(openHABWidget.getItem(),
                //								openHABWidget.getMapping(index).getCommand());
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        return mViewData.widgetView;
    }
}
