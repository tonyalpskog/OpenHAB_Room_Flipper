package org.openhab.habdroid.ui.widget;

import android.view.View;

import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class OpenHABWidgetBase {
    protected OpenHABWidgetArrayAdapter.ViewData mViewData;
    protected IHABWidgetCommunication mHABWidgetCommunication;

    public OpenHABWidgetBase(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        mHABWidgetCommunication = habWidgetCommunication;
        mViewData = viewData;
    }

    public abstract View getWidget();
}
